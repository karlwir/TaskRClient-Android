package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.sql.TaskRDbHelper;
import taskr.se.taskr.sql.TaskRDbContract.UsersEntry;
import taskr.se.taskr.sql.TaskRDbContract.WorkItemsEntry;
import taskr.se.taskr.sql.TaskRDbContract.UserWorkItemEntry;


/**
 * Created by kawi01 on 2017-05-12.
 */

class WorkItemRepositorySql implements WorkItemRepository {

    private final SQLiteDatabase database;
    private static WorkItemRepositorySql instance;

    public static synchronized WorkItemRepositorySql getInstance(Context context) {
        if(instance == null) {
            instance = new WorkItemRepositorySql(context);
        }
        return instance;
    }

    private WorkItemRepositorySql(Context context) {
        database = TaskRDbHelper.getInstance(context).getWritableDatabase();
    }

    @Override
    public List<WorkItem> getWorkItems(boolean notifyObserver) {
        return queryWorkItems(null, null);
    }

    @Override
    public List<WorkItem> getUnstartedWorkItems(boolean notifyObserver) {
        return queryWorkItems(WorkItemsEntry.COLUMN_NAME_STATUS + " = ?", new String[]{"UNSTARTED"});
    }

    @Override
    public List<WorkItem> getStartedWorkItems(boolean notifyObserver) {
        return queryWorkItems(WorkItemsEntry.COLUMN_NAME_STATUS + " = ?", new String[]{"STARTED"});
    }

    @Override
    public List<WorkItem> getDoneWorkItems(boolean notifyObserver) {
        return queryWorkItems(WorkItemsEntry.COLUMN_NAME_STATUS + " = ?" , new String[]{"DONE"});
    }



    @Override
    public List<WorkItem> getWorkItemsByUser(User user) {

        String query =
                "SELECT * FROM " + WorkItemsEntry.TABLE_NAME + " INNER JOIN " +
                        UserWorkItemEntry.TABLE_NAME + " ON " +
                        WorkItemsEntry.TABLE_NAME + "." + WorkItemsEntry._ID + "="  + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_WORKITEMID +
                        " WHERE " + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_USERID + "=" + String.valueOf(user.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        WorkItemCursorWrapper workItemCursorWrapper = new WorkItemCursorWrapper(cursor);
        List<WorkItem> workItems = new ArrayList<>();

        if(workItemCursorWrapper.getCount() > 0) {
            while(cursor.moveToNext()) {
                workItems.add(workItemCursorWrapper.getWorkItem());
            }
        }

        workItemCursorWrapper.close();

        return workItems;
    }

    @Override
    public WorkItem getWorkItem(long id) {
        return queryWorkItem(WorkItemsEntry._ID + " = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public long addOrUpdateWorkItem(WorkItem workItem) {
        ContentValues cv = getContentValues(workItem);

        if(workItem.hasBeenPersisted()) {
            cv.put(WorkItemsEntry._ID, workItem.getId());
            database.update(WorkItemsEntry.TABLE_NAME, cv, WorkItemsEntry._ID + " = ?", new String[] { String.valueOf(workItem.getId()) });

            return workItem.getId();
        } else {
            return database.insert(WorkItemsEntry.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void removeWorkItem(WorkItem workItem) {
        database.delete(WorkItemsEntry.TABLE_NAME, WorkItemsEntry._ID + " = ?", new String[] { String.valueOf(workItem.getId()) });
    }

    @Override
    public void assignWorkItem(WorkItem workItem, User user) {
        if (workItem.hasBeenPersisted() && user.hasBeenPersisted() && !assignmentPersisted(workItem, user)) {
            assignWorkItem(workItem.getId(), user.getId());
        }
    }

    private void assignWorkItem(Long workItemId, Long userId) {
        ContentValues cv = new ContentValues();
        cv.put(UserWorkItemEntry.COLUMN_NAME_WORKITEMID, workItemId);
        cv.put(UserWorkItemEntry.COLUMN_NAME_USERID, userId);
        database.insert(UserWorkItemEntry.TABLE_NAME, null, cv);
    }

    @Override
    public void unAssignWorkItem(WorkItem workItem, User user) {
        if (workItem.hasBeenPersisted() && user.hasBeenPersisted()) {
            unAssignWorkItem(workItem.getId(), user.getId());
        }
    }

    private void unAssignWorkItem(Long workItemId, Long userId) {
        database.delete(UserWorkItemEntry.TABLE_NAME, UserWorkItemEntry.COLUMN_NAME_WORKITEMID +
                " = ? AND " + UserWorkItemEntry.COLUMN_NAME_USERID + "= ?", new String[] { String.valueOf(workItemId), String.valueOf(userId) });
    }

    @Override
    public List<WorkItem> syncWorkItems(List<WorkItem> workItemsFromServer) {
        List<WorkItem> localUnsyncedWorkItems = getWorkItems(false);
        List<WorkItem> syncedPersistedWorkItems = new ArrayList<>();
        for(WorkItem workItem : workItemsFromServer) {
            Long id;
            WorkItem persistedVersion = getByItemKey(workItem.getItemKey());
            if(persistedVersion == null) {
                id = addOrUpdateWorkItem(workItem);
            } else {
                id = persistedVersion.getId();
                ContentValues cv = getContentValues(workItem);
                database.update(WorkItemsEntry.TABLE_NAME, cv, WorkItemsEntry._ID + " = ?", new String[] { String.valueOf(id) });
            }
            syncedPersistedWorkItems.add(getWorkItem(id));
        }
        if(localUnsyncedWorkItems.size() > syncedPersistedWorkItems.size()) {
            localUnsyncedWorkItems.removeAll(syncedPersistedWorkItems);
            for (WorkItem workItem : localUnsyncedWorkItems) {
                if(workItem.hasBeenSavedToServer()) {
                    removeWorkItem(workItem);
                }
            }
        }

        return syncedPersistedWorkItems;
    }

    @Override
    public void syncWorkItemAssignments(List<Map.Entry<String, User>> assignments) {
        List<Map.Entry<Long, Long>> syncedPersistedAssignments = new ArrayList<>();
        List<Map.Entry<Long, Long>> localUnsyncedAssignments = getAllAssignments();

        for (Map.Entry<String, User> assignment : assignments) {
                WorkItem workItem = getByItemKey(assignment.getKey());
                User user = assignment.getValue();
                assignWorkItem(workItem, user);
                Map.Entry<Long, Long> syncedPersistedAssignment = new AbstractMap.SimpleEntry<>(workItem.getId(), user.getId());
                syncedPersistedAssignments.add(syncedPersistedAssignment);
        }
        if (localUnsyncedAssignments.size() > syncedPersistedAssignments.size()) {
            localUnsyncedAssignments.removeAll(syncedPersistedAssignments);
            for (Map.Entry<Long, Long> oldAssignment : localUnsyncedAssignments) {
                unAssignWorkItem(oldAssignment.getKey(), oldAssignment.getValue());
            }
        }
    }

    private WorkItem getByItemKey(String itemKey) {
        return queryWorkItem(WorkItemsEntry.COLUMN_NAME_ITEMKEY + " = ?", new String[]{itemKey});
    }

    private ContentValues getContentValues(WorkItem workItem) {
        ContentValues cv = new ContentValues();
        cv.put(WorkItemsEntry.COLUMN_NAME_ITEMKEY, workItem.getItemKey());
        cv.put(WorkItemsEntry.COLUMN_NAME_TITLE, workItem.getTitle());
        cv.put(WorkItemsEntry.COLUMN_NAME_DESCRIPTION, workItem.getDescription());
        cv.put(WorkItemsEntry.COLUMN_NAME_STATUS, workItem.getStatus());

        return cv;
    }

    private List<WorkItem> queryWorkItems(String where, String[] whereArg) {
        Cursor cursor = database.query(
                WorkItemsEntry.TABLE_NAME,
                null,
                where,
                whereArg,
                null,
                null,
                null
        );

        WorkItemCursorWrapper workItemCursorWrapper = new WorkItemCursorWrapper(cursor);
        List<WorkItem> workItems = new ArrayList<>();

        if(workItemCursorWrapper.getCount() > 0) {
            while(cursor.moveToNext()) {
                WorkItem workItem = workItemCursorWrapper.getWorkItem();
                joinWorkitemAssignees(workItem);
                workItems.add(workItem);
            }
        }

        workItemCursorWrapper.close();

        return workItems;
    }

    private WorkItem queryWorkItem(String where, String[] whereArg) {
        Cursor cursor = database.query(
                WorkItemsEntry.TABLE_NAME,
                null,
                where,
                whereArg,
                null,
                null,
                null
        );

        WorkItemCursorWrapper workItemCursorWrapper = new WorkItemCursorWrapper(cursor);
        WorkItem workItem = null;

        if(workItemCursorWrapper.getCount() > 0) {
            workItem = workItemCursorWrapper.getFirstWorkItem();
            joinWorkitemAssignees(workItem);
        }
        workItemCursorWrapper.close();

        return workItem;
    }

    private WorkItem joinWorkitemAssignees(WorkItem workItem) {
        String query =
                "SELECT * FROM " + UsersEntry.TABLE_NAME + " INNER JOIN " +
                        UserWorkItemEntry.TABLE_NAME + " ON " +
                        UsersEntry.TABLE_NAME + "." + UsersEntry._ID + "="  + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_USERID +
                        " WHERE " + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_WORKITEMID + "=" + String.valueOf(workItem.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        UserCursorWrapper userCursorWrapper = new UserCursorWrapper(cursor);
        List<User> users = new ArrayList<>();

        if(userCursorWrapper.getCount() > 0) {
            while(userCursorWrapper.moveToNext()) {
                User user = userCursorWrapper.getUser();
                workItem.addUser(user);
            }
        }

        userCursorWrapper.close();

        return workItem;
    }

    private boolean assignmentPersisted(WorkItem workItem, User user) {
        String query = "SELECT * FROM " + UserWorkItemEntry.TABLE_NAME + " WHERE "
                + UserWorkItemEntry.COLUMN_NAME_WORKITEMID + "=" + String.valueOf(workItem.getId()) + " AND "
                + UserWorkItemEntry.COLUMN_NAME_USERID + "=" + String.valueOf(user.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        RelationCursorWrapper cursorWrapper = new RelationCursorWrapper(cursor, UserWorkItemEntry.COLUMN_NAME_WORKITEMID, UserWorkItemEntry.COLUMN_NAME_USERID);

        if (cursorWrapper.getCount() > 0) {
            cursorWrapper.close();
            return true;
        }
        cursorWrapper.close();

        return false;
    }

    private List<Map.Entry<Long, Long>> getAllAssignments() {
        String query = "SELECT * FROM " + UserWorkItemEntry.TABLE_NAME;

        Cursor cursor = database.rawQuery(query, null);
        RelationCursorWrapper cursorWrapper = new RelationCursorWrapper(cursor, UserWorkItemEntry.COLUMN_NAME_WORKITEMID, UserWorkItemEntry.COLUMN_NAME_USERID);
        List<Map.Entry<Long, Long>> assignments = new ArrayList<>();

        if (cursorWrapper.getCount() > 0) {
            while(cursor.moveToNext()) {
                Map.Entry<Long, Long> assignment = cursorWrapper.getEntry();
                assignments.add(assignment);
            }
        }
        cursorWrapper.close();

        return assignments;
    }

}