package taskr.se.taskr.repository;

import java.util.List;

import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;

/**
 * Created by kawi01 on 2017-05-11.
 */

public interface TeamRepository {
    List<Team> getTeams(boolean notifyObservers);
    Team getTeam(long id);
    long addOrUpdateTeam(Team team);
    void removeTeam(Team team);
    void addTeamMember(Team team, User user);
    void removeTeamMember(Team team, User user);
    void syncTeams(List<Team> teams);
}
