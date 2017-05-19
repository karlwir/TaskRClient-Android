package taskr.se.taskr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2017-05-11.
 */

public class Team extends BaseEntity{

    private final String name;
    private final String description;
    private List<User> users;
    private final boolean active = true;

    public Team(long id, String itemKey, String name, String description) {
        super(id, itemKey);
        this.name = name;
        this.description = description;
        this.users = new ArrayList<>();
    }

    public Team(String name, String description) {
        this(DEFAULT_ID, DEFAULT_ITEMKEY, name, description);
    }

    public void addMember(User user){
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
