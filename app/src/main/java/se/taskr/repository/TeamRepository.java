package se.taskr.repository;

import java.util.List;
import java.util.Map;

import se.taskr.model.Team;
import se.taskr.model.User;

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
    void syncTeamMemberships(List<Map.Entry<String, User>> mebershipsOnServer);
}
