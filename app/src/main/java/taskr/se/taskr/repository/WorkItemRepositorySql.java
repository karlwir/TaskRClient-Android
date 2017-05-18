package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    public List<WorkItem> getMyWorkItems(boolean notifyObserver) {
        // TODO
        return null;
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
    public List<WorkItem> searchWorkItem(String query) {
        List<WorkItem> byTitle = queryWorkItems(WorkItemsEntry.COLUMN_NAME_TITLE + " LIKE ?", new String[]{"%" + query + "%"});
        List<WorkItem> byDesc = queryWorkItems(WorkItemsEntry.COLUMN_NAME_DESCRIPTION + " LIKE ?", new String[]{"%" + query + "%"});
        byTitle.addAll(byDesc);
        return byTitle;
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
    public List<WorkItem> syncWorkItems(List<WorkItem> workItemsServer) {
        List<WorkItem> workItemsLocal = getWorkItems(false);
        List<WorkItem> syncedToReturn = new ArrayList<>();
        for(WorkItem workItem : workItemsServer) {
            WorkItem persistedVersion = getByItemKey(workItem.getItemKey());
            if(persistedVersion == null) {
                long id = addOrUpdateWorkItem(workItem);
                syncedToReturn.add(getWorkItem(id));
            } else {
                ContentValues cv = getContentValues(workItem);
                database.update(WorkItemsEntry.TABLE_NAME, cv, WorkItemsEntry._ID + " = ?", new String[] { String.valueOf(persistedVersion.getId()) });
                syncedToReturn.add(getWorkItem(persistedVersion.getId()));
            }
        }
        if(workItemsLocal.size() > workItemsServer.size()) {
            List<WorkItem> dontRemove = new ArrayList<>();
            for (WorkItem workItemLocal : workItemsLocal) {
                for(WorkItem workItemServer: workItemsServer) {
                    if (workItemLocal.getItemKey() != null) {
                        if (workItemLocal.getItemKey().equals(workItemServer.getItemKey())) {
                            dontRemove.add(workItemLocal);
                        }
                    }
                }
            }
            workItemsLocal.removeAll(dontRemove);
            for (WorkItem workItemLocal : workItemsLocal) {
                removeWorkItem(workItemLocal);
            }
        }

        return syncedToReturn;
    }

    @Override
    public void syncWorkItemAssignments(List<Map<String, User>> assignments) {
        List<Map<Long, Long>> syncedPersistedAssignments = new ArrayList<>();
        List<Map<Long, Long>> localUnsyncedAssignments = getAllAssignments();

        for (Map<String, User> map : assignments) {
            for (Map.Entry<String, User> entry : map.entrySet()) {
                WorkItem workItem = getByItemKey(entry.getKey());
                User user = entry.getValue();
                assignWorkItem(workItem, user);
                Map<Long, Long> assignment = new HashMap<>();
                assignment.put(workItem.getId(), user.getId());
                syncedPersistedAssignments.add(assignment);
            }
        }
        if (localUnsyncedAssignments.size() > syncedPersistedAssignments.size()) {
            localUnsyncedAssignments.removeAll(syncedPersistedAssignments);
            for (Map<Long, Long> map : localUnsyncedAssignments) {
                for (Map.Entry<Long, Long> entry : map.entrySet()) {
                    unAssignWorkItem(entry.getKey(), entry.getValue());
                }
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
                addWorkitemUsers(workItem);
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
            addWorkitemUsers(workItem);
        }
        workItemCursorWrapper.close();

        return workItem;
    }

    private WorkItem addWorkitemUsers(WorkItem workItem) {
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

    private List<Map<Long, Long>> getAllAssignments() {
        String query = "SELECT * FROM " + UserWorkItemEntry.TABLE_NAME;

        Cursor cursor = database.rawQuery(query, null);
        RelationCursorWrapper cursorWrapper = new RelationCursorWrapper(cursor, UserWorkItemEntry.COLUMN_NAME_WORKITEMID, UserWorkItemEntry.COLUMN_NAME_USERID);
        List<Map<Long, Long>> assignments = new ArrayList<>();

        if (cursorWrapper.getCount() > 0) {
            while(cursor.moveToNext()) {
                Map.Entry<Long, Long> entry = cursorWrapper.getEntry();
                Map<Long, Long> assignment = new HashMap<>();
                assignment.put(entry.getKey(), entry.getValue());
                assignments.add(assignment);
            }
        }

        cursorWrapper.close();

        return assignments;
    }

}