package taskr.se.taskr.model;

import java.util.Set;

/**
 * Created by Kevin on 2017-05-11.
 */

public class User {

    private static final long DEFAULT_ID = -1;
    private static final String DEFAULT_ITEMKEY = null;

    private final long id;
    private final String itemKey;
    private final String firstname;
    private final String lastname;
    private final String username;
    private Set<WorkItem> workItems;

    public User(long id, String itemKey, String firstname, String lastname, String username, Set<WorkItem> workItems) {
        this.id = id;
        this.itemKey = itemKey;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.workItems = workItems;
    }

    public User(String firstname, String lastname, String username, Set<WorkItem> workItems) {
        this(DEFAULT_ID, DEFAULT_ITEMKEY, firstname, lastname, username, workItems);
    }

    public User(String firstname, String lastname, String username) {
        this(DEFAULT_ID, DEFAULT_ITEMKEY, firstname, lastname ,username, null);
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

    public String getItemKey() {
        return itemKey;
    }

    public long getId() {
        return id;
    }

    public boolean hasBeenPersisted() {
        return id != DEFAULT_ID;
    }
}
