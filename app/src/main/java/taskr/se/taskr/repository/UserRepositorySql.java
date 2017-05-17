package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.sql.TaskRDbContract;
import taskr.se.taskr.sql.TaskRDbHelper;
import taskr.se.taskr.sql.TaskRDbContract.UsersEntry;

/**
 * Created by kawi01 on 2017-05-15.
 */

class UserRepositorySql implements UserRepository {

    private final SQLiteDatabase database;
    private static UserRepositorySql instance;

    public static synchronized UserRepositorySql getInstance (Context context) {
        if (instance == null) {
            instance = new UserRepositorySql(context);
        }
        return instance;
    }

    private UserRepositorySql(Context context) {
        database = TaskRDbHelper.getInstance(context).getWritableDatabase();
        fakeData(); // creates som fake data if db is empty
    }

    // This method will be removed later
    private void fakeData() {
        if(getUsers().size() == 0)  {
            for(int i = 0; i < 8; i++) {
                User user = new User("Firstname", "Lastname", "User" + i);
                addOrUpdateUser(user);
            }
        }
    }

    @Override
    public List<User> getUsers() {
        return queryUsers(null, null);
    }

    @Override
    public User getUser(long id) {
        return queryUser(UsersEntry._ID + " = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public long addOrUpdateUser(User user) {
        ContentValues cv = getContentValues(user);

        if(user.hasBeenPersisted()) {
            cv.put(UsersEntry._ID, user.getId());
            database.update(UsersEntry.TABLE_NAME, cv, UsersEntry._ID + " = ?", new String[] { String.valueOf(user.getId()) });

            return user.getId();
        } else {
            return database.insert(UsersEntry.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void removeUser(User user) {
        database.delete(UsersEntry.TABLE_NAME, UsersEntry._ID + " = ?", new String[] { String.valueOf(user.getId()) });
    }

    @Override
    public void syncUsers(List<User> usersServer, boolean removeUnsyncedLocals) {
        List<User> usersLocal = getUsers();
        for(User user : usersServer) {
            User persistedVersion = getByItemKey(user.getItemKey());
            if (persistedVersion == null) {

                Long id = addOrUpdateUser(user);
            } else {
                ContentValues cv = getContentValues(user);
                database.update(UsersEntry.TABLE_NAME, cv, UsersEntry._ID + " = ?", new String[] { String.valueOf(persistedVersion.getId()) });
            }
        }
        if (removeUnsyncedLocals) {
            List<User> dontRemove = new ArrayList<>();
            if (usersLocal.size() > usersServer.size()) {
                for (User userLocal : usersLocal) {
                    for (User userServer : usersServer) {
                        if (userLocal.getItemKey() != null){
                            if (userLocal.getItemKey().equals(userServer.getItemKey())) {
                                dontRemove.add(userLocal);
                            }
                        }
                    }
                }
                usersLocal.removeAll(dontRemove);
                for (User user : usersLocal) {
                    removeUser(user);
                }
            }
        }
    }

    private User getByItemKey(String itemKey) {
        return queryUser(UsersEntry.COLUMN_NAME_ITEMKEY + " = ?", new String[]{itemKey});
    }

    private ContentValues getContentValues(User user) {
        ContentValues cv = new ContentValues();
        cv.put(UsersEntry.COLUMN_NAME_ITEMKEY, user.getItemKey());
        cv.put(UsersEntry.COLUMN_NAME_FIRSTNAME, user.getFirstname());
        cv.put(UsersEntry.COLUMN_NAME_LASTNAME, user.getLastname());
        cv.put(UsersEntry.COLUMN_NAME_USERNAME, user.getUsername());

        return cv;
    }

    private List<User> queryUsers(String where, String[] whereArg) {
        Cursor cursor = database.query(
                UsersEntry.TABLE_NAME,
                null,
                where,
                whereArg,
                null,
                null,
                null
        );

        UserCursorWrapper userCursorWrapper = new UserCursorWrapper(cursor);
        List<User> users = new ArrayList<>();

        if(userCursorWrapper.getCount() > 0) {
            while(cursor.moveToNext()) {
                users.add(userCursorWrapper.getUser());
            }
        }

        userCursorWrapper.close();

        return users;
    }

    private User queryUser(String where, String[] whereArg) {
        Cursor cursor = database.query(
                UsersEntry.TABLE_NAME,
                null,
                where,
                whereArg,
                null,
                null,
                null
        );

        UserCursorWrapper userCursorWrapper = new UserCursorWrapper(cursor);
        User user = null;

        if(userCursorWrapper.getCount() > 0) {
            user = userCursorWrapper.getFirstUser();
        }

        userCursorWrapper.close();

        return user;
    }
}
