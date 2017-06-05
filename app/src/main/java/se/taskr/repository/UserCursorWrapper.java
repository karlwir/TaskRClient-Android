package se.taskr.repository;

import android.database.Cursor;
import android.database.CursorWrapper;

import se.taskr.model.User;
import se.taskr.sql.TaskRDbContract;

/**
 * Created by kawi01 on 2017-05-16.
 */

class UserCursorWrapper extends CursorWrapper {

    UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    User getUser() {
        long id = getLong(getColumnIndexOrThrow(TaskRDbContract.UsersEntry._ID));
        String itemKey = getString(getColumnIndexOrThrow(TaskRDbContract.UsersEntry.COLUMN_NAME_ITEMKEY));
        String firstname = getString(getColumnIndexOrThrow(TaskRDbContract.UsersEntry.COLUMN_NAME_FIRSTNAME));
        String lastname = getString(getColumnIndexOrThrow(TaskRDbContract.UsersEntry.COLUMN_NAME_LASTNAME));
        String username = getString(getColumnIndexOrThrow(TaskRDbContract.UsersEntry.COLUMN_NAME_USERNAME));

        return new User(id, itemKey, firstname, lastname, username);
    }

    User getFirstUser() {
        moveToFirst();
        return getUser();
    }
}
