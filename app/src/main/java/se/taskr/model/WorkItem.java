package se.taskr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2017-05-11.
 */

public class WorkItem extends BaseEntity {
    private String title;
    private String description;
    private String status;
    private long priority = 0;
    private List<User> users = new ArrayList<>();

    public WorkItem(long id, String itemKey, String title, String description, String status) {
        super(id, itemKey);
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

    public List<User> getUsers() {
        return users;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    @Override
    public String toString() {
        return String.format("Workitem: %s, %s, %s, %s, %s, %s", id, title, description, status, itemKey, users.size());
    }
}
