package taskr.se.taskr.repository;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;

/**
 * Created by kawi01 on 2017-05-18.
 */

class TeamHttpClient extends BaseHttpClient<Team> {

    static synchronized TeamHttpClient getInstance(Context context) {
        return new TeamHttpClient(context);
    }

    private TeamHttpClient(Context context) {
        super(context);
    }

    void getTeams(OnResultEventListener<List<Team>> listener) {
        new GetTask(listener, TEAM_BASE_URL).execute();
    }

    void postTeam(Team team, OnResultEventListener<String> listener) {
        new PostTask(team, listener, TEAM_BASE_URL).execute();
    }

    void putTeam(Team team) {
        String url = String.format("%s/%s", TEAM_BASE_URL, team.getItemKey());
        new PutTask(team, url).execute();
    }

    void deleteTeam(Team team) {
        String url = String.format("%s/%s", TEAM_BASE_URL, team.getItemKey());
        new DeleteTask(url).execute();
    }

    void addTeamMember(Team team, User user) {
        String url = String.format("%s/%s/users", TEAM_BASE_URL, team.getItemKey());
        new PutTask(null, url).execute();
    }

    void removeTeamMember(Team team, User user) {
        String url = String.format("%s/%s/users", TEAM_BASE_URL, team.getItemKey());
        new DeleteTask(url).execute();
    }

    @Override
    protected Type getCollectionType() {
        return new TypeToken<Collection<Team>>(){}.getType();
    }
}
