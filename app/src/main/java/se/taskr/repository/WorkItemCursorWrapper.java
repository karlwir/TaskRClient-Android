package se.taskr.repository;

import android.database.Cursor;
import android.database.CursorWrapper;

import se.taskr.model.WorkItem;
import se.taskr.sql.TaskRDbContract;

/**
 * Created by kawi01 on 2017-05-16.
 */

class WorkItemCursorWrapper extends CursorWrapper {

    WorkItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    WorkItem getWorkItem() {
        long id = getLong(getColumnIndexOrThrow(TaskRDbContract.WorkItemsEntry._ID));
        String itemKey = getString(getColumnIndexOrThrow(TaskRDbContract.WorkItemsEntry.COLUMN_NAME_ITEMKEY));
        String title = getString(getColumnIndexOrThrow(TaskRDbContract.WorkItemsEntry.COLUMN_NAME_TITLE));
        String description = getString(getColumnIndexOrThrow(TaskRDbContract.WorkItemsEntry.COLUMN_NAME_DESCRIPTION));
        String status = getString(getColumnIndexOrThrow(TaskRDbContract.WorkItemsEntry.COLUMN_NAME_STATUS));

        WorkItem workitem = new WorkItem(id, itemKey, title, description, status);

        return workitem;
    }

    WorkItem getFirstWorkItem() {
        moveToFirst();
        return getWorkItem();
    }

}
