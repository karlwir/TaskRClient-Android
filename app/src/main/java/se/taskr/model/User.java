package se.taskr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2017-05-11.
 */

public class User extends BaseEntity {

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private List<WorkItem> workItems;
    private List<Team> teams;
    private final boolean active = true;

    public User(long id, String itemKey, String firstname, String lastname, String username, String email) {
        super(id, itemKey);
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.workItems = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.email = email;
    }

    public User(String firstname, String lastname, String username, String email) {
        this(DEFAULT_ID, DEFAULT_ITEMKEY, firstname, lastname, username, email);
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

    public String getEmail() {
        return email;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstname) {
        this.firstname = firstname;
    }

    public void setLastName(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("User: %s, %s, %s, %s, %s, teams:%s, workItems:%s", id, firstname, lastname, username, itemKey, teams.size(), workItems.size());
    }
}
