package taskr.se.taskr.repository;

import java.util.List;

import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-11.
 */

public interface WorkItemRepository {
    List<WorkItem> getWorkItems();
    List<WorkItem> getUnstartedWorkItems();
    List<WorkItem> getStartedWorkItems();
    List<WorkItem> getDoneWorkItems();
    List<WorkItem> getMyWorkItems();
    List<WorkItem> getWorkItemsByUser(User user);
    WorkItem getWorkItem(long id);
    long addOrUpdateWorkItem(WorkItem workItem);
    void removeWorkItem(WorkItem workItem);
    void assignWorkItem(WorkItem workItem, User user);
    void unAssignWorkItem(WorkItem workItem, User user);
    void syncWorkItems(List<WorkItem> workItems);
}