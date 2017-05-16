package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.sql.TaskRDbHelper;
import taskr.se.taskr.sql.TaskRDbContract.UsersEntry;
import taskr.se.taskr.sql.TaskRDbContract.WorkItemsEntry;
import taskr.se.taskr.sql.TaskRDbContract.UserWorkItemEntry;


/**
 * Created by kawi01 on 2017-05-12.
 */

public class WorkItemRepositorySql implements WorkItemRepository {

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
    public List<WorkItem> getWorkItems() {
        return queryWorkItems(null, null);
    }

    @Override
    public List<WorkItem> getUnstartedWorkItems() {
        return queryWorkItems(WorkItemsEntry.COLUMN_NAME_STATUS + " = ?", new String[]{"UNSTARTED"});
    }

    @Override
    public List<WorkItem> getStartedWorkItems() {
        return queryWorkItems(WorkItemsEntry.COLUMN_NAME_STATUS + " = ?", new String[]{"STARTED"});
    }

    @Override
    public List<WorkItem> getDoneWorkItems() {
        return queryWorkItems(WorkItemsEntry.COLUMN_NAME_STATUS + " = ?" , new String[]{"DONE"});
    }

    @Override
    public List<WorkItem> getMyWorkItems() {
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
        if (workItem.hasBeenPersisted() && user.hasBeenPersisted()) {
            ContentValues cv = new ContentValues();
            cv.put(UserWorkItemEntry.COLUMN_NAME_WORKITEMID, workItem.getId());
            cv.put(UserWorkItemEntry.COLUMN_NAME_USERID, user.getId());
            database.insert(UserWorkItemEntry.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void unAssignWorkItem(WorkItem workItem, User user) {
        if (workItem.hasBeenPersisted() && user.hasBeenPersisted()) {
            database.delete(UserWorkItemEntry.TABLE_NAME, UserWorkItemEntry.COLUMN_NAME_WORKITEMID +
                    " = ? AND " + UserWorkItemEntry.COLUMN_NAME_USERID + "= ?", new String[] { String.valueOf(workItem.getId()), String.valueOf(user.getId()) });
        }
    }

    @Override
    public void syncWorkItems(List<WorkItem> workItemsServer) {
        List<WorkItem> workItemsLocal = getWorkItems();
        for(WorkItem workItem : workItemsServer) {
            WorkItem persistedVersion = getByItemKey(workItem.getItemKey());
            if(persistedVersion == null) {
                addOrUpdateWorkItem(workItem);
            }
            else {
                ContentValues cv = getContentValues(workItem);
                database.update(WorkItemsEntry.TABLE_NAME, cv, WorkItemsEntry._ID + " = ?", new String[] { String.valueOf(persistedVersion.getId()) });
            }
        }
        if(workItemsLocal.size() > workItemsServer.size()) {
            List<WorkItem> dontRemove = new ArrayList<>();
            for (WorkItem workItemLocal : workItemsLocal) {
                for(WorkItem workItemServer: workItemsServer) {
                    if (workItemLocal.getItemKey().equals(workItemServer.getItemKey())) {
                        dontRemove.add(workItemLocal);
                    }
                }
            }
            workItemsLocal.removeAll(dontRemove);
            for (WorkItem workItemLocal : workItemsLocal) {
                removeWorkItem(workItemLocal);
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
                WorkItem workItem =workItemCursorWrapper.getWorkItem();
                addWorkitemUser(workItem);
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
            addWorkitemUser(workItem);
        }
        workItemCursorWrapper.close();

        return workItem;
    }

    private WorkItem addWorkitemUser(WorkItem workItem) {
        String query =
                "SELECT * FROM " + UsersEntry.TABLE_NAME + " INNER JOIN " +
                        UserWorkItemEntry.TABLE_NAME + " ON " +
                        UsersEntry.TABLE_NAME + "." + UsersEntry._ID + "="  + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_USERID +
                        " WHERE " + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_WORKITEMID + "=" + String.valueOf(workItem.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        UserCursorWrapper userCursorWrapper = new UserCursorWrapper(cursor);
        List<User> users = new ArrayList<>();

        if(userCursorWrapper.getCount() > 0) {
            User user = userCursorWrapper.getFirstUser();
            workItem.setUser(user);
        }

        userCursorWrapper.close();

        return workItem;
    }

}
