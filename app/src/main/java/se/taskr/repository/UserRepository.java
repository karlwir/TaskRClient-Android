package se.taskr.repository;

import java.util.List;

import se.taskr.model.User;

/**
 * Created by kawi01 on 2017-05-11.
 */

public interface UserRepository {
    List<User> getUsers(boolean notifyObservers);
    User getUser(long id);
    long addOrUpdateUser(User user);
    void removeUser(User user);
    List<User> syncUsers(List<User> users, boolean removeUnsyncedLocals);
}
