package se.taskr.repository;

import android.content.Intent;

/**
 * Created by kawi01 on 2017-05-15.
 */

public interface TaskRContentProvider extends UserRepository, WorkItemRepository, TeamRepository {

    void initData(OnResultEventListener listener);
}
