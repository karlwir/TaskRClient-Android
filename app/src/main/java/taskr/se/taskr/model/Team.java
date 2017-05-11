package taskr.se.taskr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2017-05-11.
 */

public class Team {

    private final String name;
    private final String description;
    private List<User> members;

    public Team(String name, String description) {
        this.name = name;
        this.description = description;
        this.members = new ArrayList<>();
    }

    public void addMember(User user){
        members.add(user);
    }

    public List<User> getMembers() {
        return members;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
