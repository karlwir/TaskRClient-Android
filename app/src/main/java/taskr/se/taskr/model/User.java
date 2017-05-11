package taskr.se.taskr.model;

/**
 * Created by Kevin on 2017-05-11.
 */

public class User {

    private final String firstname;
    private final String lastname;
    private final String username;

    public User(String firstname, String lastname, String username) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
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
}
