package se.taskr.repository;

import android.content.Intent;

import java.util.List;

import se.taskr.model.User;

/**
 * Created by kawi01 on 2017-05-15.
 */

public interface TaskRContentProvider extends UserRepository, WorkItemRepository, TeamRepository {

    void initData(OnResultEventListener listener);

    long createUserAccount(User user, OnResultEventListener listener);

    List<User> getUsers(OnResultEventListener listener);

}
