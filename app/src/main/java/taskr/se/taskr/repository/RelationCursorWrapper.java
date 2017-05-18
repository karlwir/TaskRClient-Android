package taskr.se.taskr.repository;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.AbstractMap;
import java.util.Map.Entry;

import taskr.se.taskr.sql.TaskRDbContract;

/**
 * Created by kawi01 on 2017-05-17.
 */

public class RelationCursorWrapper extends CursorWrapper {

    private final String firstColumn;
    private final String secondColumn;

    RelationCursorWrapper(Cursor cursor, String firstColumn, String secondColumn) {
        super(cursor);
        this.firstColumn = firstColumn;
        this.secondColumn = secondColumn;
    }

    Entry<Long, Long> getEntry() {
        Long firstId = getLong(getColumnIndexOrThrow(firstColumn));
        Long secondId = getLong(getColumnIndexOrThrow(secondColumn));

        return new AbstractMap.SimpleImmutableEntry(firstId, secondId);
    }
}
