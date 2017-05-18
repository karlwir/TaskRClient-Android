package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.sql.TaskRDbContract;
import taskr.se.taskr.sql.TaskRDbContract.TeamsEntry;
import taskr.se.taskr.sql.TaskRDbContract.UserTeamEntry;
import taskr.se.taskr.sql.TaskRDbHelper;

/**
 * Created by kawi01 on 2017-05-16.
 */

class TeamRepositorySql implements TeamRepository {

    private final SQLiteDatabase database;
    private static TeamRepositorySql instance;

    static synchronized TeamRepositorySql getInstance(Context context) {
        if (instance == null) {
            instance = new TeamRepositorySql(context);
        }
        return instance;
    }

    private TeamRepositorySql(Context context) {
        database = TaskRDbHelper.getInstance(context).getWritableDatabase();
    }

    @Override
    public List<Team> getTeams(boolean notifyObservers) {
        return queryTeams(null, null);
    }

    @Override
    public Team getTeam(long id) {
        return queryTeam(TeamsEntry._ID + " = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public long addOrUpdateTeam(Team team) {
        ContentValues cv = getContentValues(team);

        if(team.hasBeenPersisted()) {
            cv.put(TeamsEntry._ID, team.getId());
            database.update(TeamsEntry.TABLE_NAME, cv, TeamsEntry._ID + " = ?", new String[] { String.valueOf(team.getId()) });

            return team.getId();
        } else {
            return database.insert(TeamsEntry.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void removeTeam(Team team) {
        database.delete(TeamsEntry.TABLE_NAME, TeamsEntry._ID + " = ?", new String[] { String.valueOf(team.getId()) });
    }

    @Override
    public void addTeamMember(Team team, User user) {
        if (team.hasBeenPersisted() && user.hasBeenPersisted() && !membershipPersisted(team, user)) {
            addTeamMember(team.getId(), user.getId());
        }
    }

    private void addTeamMember(Long teamId, Long userId) {
        ContentValues cv = new ContentValues();
        cv.put(UserTeamEntry.COLUMN_NAME_TEAMID, teamId);
        cv.put(UserTeamEntry.COLUMN_NAME_USERID, userId);
        database.insert(UserTeamEntry.TABLE_NAME, null, cv);
    }

    @Override
    public void removeTeamMember(Team team, User user) {
        if (team.hasBeenPersisted() && user.hasBeenPersisted()) {
            removeTeamMember(team.getId(), user.getId());
        }
    }

    private void removeTeamMember(Long teamId, Long userId) {
        database.delete(UserTeamEntry.TABLE_NAME, UserTeamEntry.COLUMN_NAME_TEAMID +
                " = ? AND " + UserTeamEntry.COLUMN_NAME_USERID + "= ?", new String[] { String.valueOf(teamId), String.valueOf(userId) });
    }

    @Override
    public void syncTeams(List<Team> teamsServer) {
        List<Team> localUnscyncedTeams = getTeams(false);
        List<Team> syncedPersistedTeams = new ArrayList<>();
        for (Team team : teamsServer) {
            Long id;
            Team persistedVersion = getByItemKey(team.getItemKey());
            if(persistedVersion == null) {
                id = addOrUpdateTeam(team);
            } else {
                id = team.getId();
                ContentValues cv = getContentValues(team);
                database.update(TeamsEntry.TABLE_NAME, cv, TeamsEntry._ID + " = ?", new String[] { String.valueOf(id) });
            }
            syncedPersistedTeams.add(getTeam(id));
        }
        if (localUnscyncedTeams.size() > teamsServer.size()) {
            localUnscyncedTeams.removeAll(syncedPersistedTeams);
            for (Team oldTeam : localUnscyncedTeams) {
                removeTeam(oldTeam);
            }
        }
    }

    @Override
    public void syncTeamMemberships(List<Map.Entry<String, User>> mebershipsOnServer) {
        List<Map.Entry<Long, Long>> syncedPersistedMemberships = new ArrayList<>();
        List<Map.Entry<Long, Long>> localUnsyncedMemberships = getAllMemberships();

        for (Map.Entry<String, User> membership : mebershipsOnServer) {
            Team team = getByItemKey(membership.getKey());
            User user = membership.getValue();
            addTeamMember(team, user);
            Map.Entry<Long, Long> syncedPersistedMembership = new AbstractMap.SimpleEntry<>(team.getId(), user.getId());
            syncedPersistedMemberships.add(syncedPersistedMembership);
        }
        if (localUnsyncedMemberships.size() > syncedPersistedMemberships.size()) {
            localUnsyncedMemberships.removeAll(syncedPersistedMemberships);
            for (Map.Entry<Long, Long> oldMembership :localUnsyncedMemberships) {
                removeTeamMember(oldMembership.getKey(), oldMembership.getValue());
            }
        }

    }

    private List<Map.Entry<Long,Long>> getAllMemberships() {
        String query = "SELECT * FROM " + UserTeamEntry.TABLE_NAME;

        Cursor cursor = database.rawQuery(query, null);
        RelationCursorWrapper cursorWrapper = new RelationCursorWrapper(cursor, UserTeamEntry.COLUMN_NAME_TEAMID, UserTeamEntry.COLUMN_NAME_USERID);
        List<Map.Entry<Long,Long>> memberships = new ArrayList<>();

        if (cursorWrapper.getCount() > 0) {
            while(cursor.moveToNext()) {
                Map.Entry<Long, Long> entry = cursorWrapper.getEntry();
                memberships.add(entry);
            }
        }
        cursorWrapper.close();

        return memberships;
    }

    private List<Team> queryTeams(String where, String[] whereArg) {
        Cursor cursor = database.query(
                TeamsEntry.TABLE_NAME,
                null,
                where,
                whereArg,
                null,
                null,
                null
        );

        TeamCursorWrapper teamCursorWrapper = new TeamCursorWrapper(cursor);
        List<Team> teams = new ArrayList<>();

        if(teamCursorWrapper.getCount() > 0) {
            while(cursor.moveToNext()) {
                Team team = teamCursorWrapper.getTeam();
                addTeamMembers(team);
                teams.add(team);
            }
        }
        teamCursorWrapper.close();

        return teams;
    }

    private Team queryTeam(String where, String[] whereArg) {
        Cursor cursor = database.query(
                TeamsEntry.TABLE_NAME,
                null,
                where,
                whereArg,
                null,
                null,
                null
        );

        TeamCursorWrapper teamCursorWrapper = new TeamCursorWrapper(cursor);
        Team team = null;

        if(teamCursorWrapper.getCount() > 0) {
            team = teamCursorWrapper.getFirstTeam();
            addTeamMembers(team);
        }
        teamCursorWrapper.close();

        return team;
    }

    private Team getByItemKey(String itemKey) {
        return queryTeam(TeamsEntry.COLUMN_NAME_ITEMKEY + " = ?", new String[]{itemKey});
    }

    private ContentValues getContentValues(Team team) {
        ContentValues cv = new ContentValues();
        cv.put(TeamsEntry.COLUMN_NAME_ITEMKEY, team.getItemKey());
        cv.put(TeamsEntry.COLUMN_NAME_NAME, team.getName());
        cv.put(TeamsEntry.COLUMN_NAME_DESCRIPTION, team.getDescription());

        return cv;
    }

    private Team addTeamMembers(Team team) {
        String query =
                "SELECT * FROM " + TaskRDbContract.UsersEntry.TABLE_NAME + " INNER JOIN " +
                        TaskRDbContract.UserTeamEntry.TABLE_NAME + " ON " +
                        TaskRDbContract.UsersEntry.TABLE_NAME + "." + TaskRDbContract.UsersEntry._ID + "="  + TaskRDbContract.UserTeamEntry.TABLE_NAME + "." + TaskRDbContract.UserTeamEntry.COLUMN_NAME_USERID +
                        " WHERE " + TaskRDbContract.UserTeamEntry.TABLE_NAME + "." + TaskRDbContract.UserTeamEntry.COLUMN_NAME_TEAMID + "=" + String.valueOf(team.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        UserCursorWrapper userCursorWrapper = new UserCursorWrapper(cursor);
        List<User> users = new ArrayList<>();

        if(userCursorWrapper.getCount() > 0) {
            while(userCursorWrapper.moveToNext()) {
                User user = userCursorWrapper.getUser();
                team.addMember(user);
            }
        }
        userCursorWrapper.close();

        return team;
    }

    private boolean membershipPersisted(Team team, User user) {
        String query = "SELECT * FROM " + UserTeamEntry.TABLE_NAME + " WHERE "
                + UserTeamEntry.COLUMN_NAME_TEAMID + "=" + String.valueOf(team.getId()) + " AND "
                + UserTeamEntry.COLUMN_NAME_USERID + "=" + String.valueOf(user.getId()) + ";";

        Cursor cursor = database.rawQuery(query, null);
        RelationCursorWrapper cursorWrapper = new RelationCursorWrapper(cursor, UserTeamEntry.COLUMN_NAME_TEAMID, UserTeamEntry.COLUMN_NAME_USERID);

        if (cursorWrapper.getCount() > 0) {
            cursorWrapper.close();
            return true;
        }
        cursorWrapper.close();
        return false;
    }
}
