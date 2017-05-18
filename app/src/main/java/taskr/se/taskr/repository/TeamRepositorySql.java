package taskr.se.taskr.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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
        if (team.hasBeenPersisted() && user.hasBeenPersisted()) {
            ContentValues cv = new ContentValues();
            cv.put(UserTeamEntry.COLUMN_NAME_TEAMID, team.getId());
            cv.put(UserTeamEntry.COLUMN_NAME_USERID, user.getId());
            database.insert(UserTeamEntry.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void removeTeamMember(Team team, User user) {
        if (team.hasBeenPersisted() && user.hasBeenPersisted() && membershipPersisted(team, user)) {
            ContentValues cv = new ContentValues();
            cv.put(UserTeamEntry.COLUMN_NAME_TEAMID, team.getId());
            cv.put(UserTeamEntry.COLUMN_NAME_USERID, user.getId());
            database.insert(UserTeamEntry.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void syncTeams(List<Team> teamsServer) {
        List<Team> localUnscyncedTeams = getTeams(false);
        for (Team team : teamsServer) {
            Team persistedVersion = getByItemKey(team.getItemKey());
            if(persistedVersion == null) {
                long id = addOrUpdateTeam(team);
            } else {
                ContentValues cv = getContentValues(team);
                database.update(TeamsEntry.TABLE_NAME, cv, TeamsEntry._ID + " = ?", new String[] { String.valueOf(team.getId()) });
            }
        }
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
                //  addTeamMemers(team);
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
            //  addTeamMemers(team);
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
