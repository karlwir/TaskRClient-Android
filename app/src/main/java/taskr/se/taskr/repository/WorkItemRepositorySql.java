package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.sql.TaskRDbHelper;
import taskr.se.taskr.sql.TaskRDbContract.WorkItemsEntry;


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
        if(getWorkItems().size() < 5)  {
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
        return queryWorkItems(WorkItemsEntry.COLUMN_NAME_STATUS + " = ?", new String[]{"DONE"});
    }

    @Override
    public List<WorkItem> getMyWorkItems() {
        return null;
    }

    @Override
    public WorkItem getWorkItem(long id) {
        return null;
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
    public void deleteWorkItem(WorkItem workItem) {

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
//        Log.d("TAG", whereArg.toString());
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
        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                workItems.add(workItemCursorWrapper.getWorkItem());
            }
        }

        workItemCursorWrapper.close();

        return workItems;
    }

    private class WorkItemCursorWrapper extends CursorWrapper {

        public WorkItemCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public WorkItem getWorkItem() {
            long id = getLong(getColumnIndexOrThrow(WorkItemsEntry._ID));
            String itemKey = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_ITEMKEY));
            String title = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_TITLE));
            String description = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_DESCRIPTION));
            String status = getString(getColumnIndexOrThrow(WorkItemsEntry.COLUMN_NAME_STATUS));

            return new WorkItem(id, itemKey, title, description, status);
        }

        public WorkItem getFirstWorkItem() {
            moveToFirst();
            return getWorkItem();
        }

    }
}
