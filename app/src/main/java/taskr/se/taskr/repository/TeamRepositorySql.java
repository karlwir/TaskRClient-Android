package taskr.se.taskr.repository;

import java.util.List;

import taskr.se.taskr.model.Team;

/**
 * Created by kawi01 on 2017-05-16.
 */

class TeamRepositorySql implements TeamRepository {
    @Override
    public List<Team> getTeams() {
        return null;
    }

    @Override
    public Team getTeam(long id) {
        return null;
    }

    @Override
    public long addOrUpdateTeam(Team team) {
        return 0;
    }

    @Override
    public void deleteTeam(Team team) {

    }
}
