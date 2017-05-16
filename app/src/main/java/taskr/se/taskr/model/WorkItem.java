package taskr.se.taskr.model;

import java.util.Set;

/**
 * Created by Kevin on 2017-05-11.
 */

public class WorkItem {
    private static final long DEFAULT_ID = 0;
    private static final String DEFAULT_ITEMKEY = null;

    private final long id;
    private final String itemKey;
    private final String title;
    private final String description;
    private String status;
    private User user;

    public WorkItem(long id, String itemKey, String title, String description, String status) {
        this.id = id;
        this.itemKey = itemKey;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public WorkItem(String title, String description, String status) {
        this(DEFAULT_ID, DEFAULT_ITEMKEY, title, description, status);
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format("Workitem: %s, %s, %s, %s, %s", id, title, description, status, itemKey);
    }
}
