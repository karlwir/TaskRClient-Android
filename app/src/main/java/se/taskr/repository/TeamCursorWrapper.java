package se.taskr.repository;

import android.database.Cursor;
import android.database.CursorWrapper;

import se.taskr.model.Team;
import se.taskr.sql.TaskRDbContract;

/**
 * Created by kawi01 on 2017-05-18.
 */

public class TeamCursorWrapper extends CursorWrapper {

    TeamCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    Team getTeam() {
        long id = getLong(getColumnIndexOrThrow(TaskRDbContract.TeamsEntry._ID));
        String itemKey = getString(getColumnIndexOrThrow(TaskRDbContract.TeamsEntry.COLUMN_NAME_ITEMKEY));
        String name = getString(getColumnIndexOrThrow(TaskRDbContract.TeamsEntry.COLUMN_NAME_NAME));
        String description = getString(getColumnIndexOrThrow(TaskRDbContract.TeamsEntry.COLUMN_NAME_DESCRIPTION));

        return new Team(id, itemKey, name, description);
    }

    Team getFirstTeam() {
        moveToFirst();
        return getTeam();
    }
}
