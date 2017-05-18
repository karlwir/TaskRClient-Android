package taskr.se.taskr.repository;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.AbstractMap;
import java.util.Map.Entry;

import taskr.se.taskr.sql.TaskRDbContract;

/**
 * Created by kawi01 on 2017-05-17.
 */

public class UserWorkItemCursorWrapper extends CursorWrapper {

    public UserWorkItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    Entry<Long, Long> getEntry() {
        Long workItemId = getLong(getColumnIndexOrThrow(TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_WORKITEMID));
        Long userId = getLong(getColumnIndexOrThrow(TaskRDbContract.UserWorkItemEntry.COLUMN_NAME_USERID));

        return new AbstractMap.SimpleImmutableEntry(workItemId, userId);
    }
}
