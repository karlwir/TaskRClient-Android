package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.sql.TaskRDbContract;
import taskr.se.taskr.sql.TaskRDbHelper;
import taskr.se.taskr.sql.TaskRDbContract.*;

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
    }

    @Override
    public List<User> getUsers(boolean notifyObservers) {
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
    public List<User> syncUsers(List<User> usersFromServer, boolean removeUnsyncedLocals) {
        List<User> localUnsyncedUsers = getUsers(false);
        List<User> syncedPersistedUsers = new ArrayList<>();
        for(User user : usersFromServer) {
            Long id;
            User persistedVersion = getByItemKey(user.getItemKey());
            if (persistedVersion == null) {
                id = addOrUpdateUser(user);
            } else {
                id = persistedVersion.getId();
                ContentValues cv = getContentValues(user);
                database.update(UsersEntry.TABLE_NAME, cv, UsersEntry._ID + " = ?", new String[] { String.valueOf(id) });
            }
            syncedPersistedUsers.add(getUser(id));
        }
        if (removeUnsyncedLocals && localUnsyncedUsers.size() > syncedPersistedUsers.size()) {
            localUnsyncedUsers.removeAll(syncedPersistedUsers);
            for (User user : localUnsyncedUsers) {
                if (user.hasBeenSavedToServer()) {
                    removeUser(user);
                }
            }
        }

        return syncedPersistedUsers;
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
                User user = userCursorWrapper.getUser();
                joinTeams(user);
                joinWorkItems(user);
                users.add(user);
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
            joinTeams(user);
            joinWorkItems(user);
        }

        userCursorWrapper.close();

        return user;
    }

    private void joinTeams(User user) {
        String query =
                "SELECT * FROM " + TeamsEntry.TABLE_NAME + " INNER JOIN " +
                        UserTeamEntry.TABLE_NAME + " ON " +
                        TeamsEntry.TABLE_NAME + "." + TeamsEntry._ID + "="  + UserTeamEntry.TABLE_NAME + "." + UserTeamEntry.COLUMN_NAME_TEAMID +
                        " WHERE " + UserTeamEntry.TABLE_NAME + "." + UserTeamEntry.COLUMN_NAME_USERID + "=" + String.valueOf(user.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        TeamCursorWrapper cursorWrapper = new TeamCursorWrapper(cursor);
        List<Team> teams = new ArrayList<>();

        if (cursorWrapper.getCount() > 0) {
            while(cursorWrapper.moveToNext()) {
                Team team = cursorWrapper.getTeam();
                user.addTeam(team);
            }
        }
        cursorWrapper.close();
    }

    private void joinWorkItems(User user) {
        String query =
                "SELECT * FROM " + WorkItemsEntry.TABLE_NAME + " INNER JOIN " +
                        UserWorkItemEntry.TABLE_NAME + " ON " +
                        WorkItemsEntry.TABLE_NAME + "." + WorkItemsEntry._ID + "="  + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_WORKITEMID +
                        " WHERE " + UserWorkItemEntry.TABLE_NAME + "." + UserWorkItemEntry.COLUMN_NAME_USERID + "=" + String.valueOf(user.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        WorkItemCursorWrapper cursorWrapper = new WorkItemCursorWrapper(cursor);
        List<WorkItem> workItems = new ArrayList<>();

        if (cursorWrapper.getCount() > 0) {
            while(cursorWrapper.moveToNext()) {
                WorkItem workItem = cursorWrapper.getWorkItem();
                user.addWorkItem(workItem);
            }
        }
        cursorWrapper.close();
    }
}
