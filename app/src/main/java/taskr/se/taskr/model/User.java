package taskr.se.taskr.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Kevin on 2017-05-11.
 */

public class User extends  BaseEntity {

    private final String firstname;
    private final String lastname;
    private final String username;
    private List<WorkItem> workItems;
    private List<Team> teams;
    private final boolean active = true;

    public User(long id, String itemKey, String firstname, String lastname, String username) {
        super(id, itemKey);
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.workItems = new ArrayList<>();
        this.teams = new ArrayList<>();
    }

    public User(String firstname, String lastname, String username) {
        this(DEFAULT_ID, DEFAULT_ITEMKEY, firstname, lastname, username);
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void addWorkItem(WorkItem workItem) {
        workItems.add(workItem);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public boolean hasTeam() {
        return teams.size() > 0;
    }

    @Override
    public String toString() {
        return String.format("User: %s, %s, %s, %s, %s, teams:%s, workItems:%s", id, firstname, lastname, username, itemKey, teams.size(), workItems.size());
    }
}
