package taskr.se.taskr.model;

import java.util.Set;

/**
 * Created by Kevin on 2017-05-11.
 */

public class User extends  BaseEntity {

    private final String firstname;
    private final String lastname;
    private final String username;

    public User(long id, String itemKey, String firstname, String lastname, String username) {
        super(id, itemKey);
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
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

    public boolean hasBeenPersisted() {
        return id != DEFAULT_ID;
    }

    @Override
    public String toString() {
        return String.format("User: %s, %s, %s, %s, %s", id, firstname, lastname, username, itemKey);
    }
}
