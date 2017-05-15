package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.sql.TaskRDbContract;
import taskr.se.taskr.sql.TaskRDbHelper;
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
        fakeData(); // creates som fake data if db is empty
    }

    // This method will be removed later
    private void fakeData() {
        if(getWorkItems().size() < 0)  {
            for(int i = 0; i < 12; i++) {
                WorkItem workItem = new WorkItem("Title" + i, "Description", "UNSTARTED");
                addOrUpdateWorkItem(workItem);
            }
            for(int i = 0; i < 8; i++) {
                WorkItem workItem = new WorkItem("Title" + i, "Description", "STARTED");
                addOrUpdateWorkItem(workItem);
            }
            for(int i = 0; i < 7; i++) {
                WorkItem workItem = new WorkItem("Title" + i, "Description", "DONE");
                addOrUpdateWorkItem(workItem);
            }
        }
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
        ContentValues cv = new ContentValues();
        cv.put(UserWorkItemEntry.COLUMN_NAME_WORKITEMID, workItem.getId());
        cv.put(UserWorkItemEntry.COLUMN_NAME_USERID, user.getId());
        database.insert(UserWorkItemEntry.TABLE_NAME, null, cv);
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
                workItems.add(workItemCursorWrapper.getWorkItem());
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
        }
        workItemCursorWrapper.close();

        return workItem;
    }

    private class WorkItemCursorWrapper extends CursorWrapper {

        WorkItemCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        WorkItem getWorkItem() {
            long id = getLong(getColumnIndexOrThrow(WorkItemsEntry._ID));
            String itemKey = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_ITEMKEY));
            String title = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_TITLE));
            String description = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_DESCRIPTION));
            String status = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_STATUS));

            return new WorkItem(id, itemKey, title, description, status, null);
        }

        WorkItem getFirstWorkItem() {
            moveToFirst();
            return getWorkItem();
        }

    }
}
