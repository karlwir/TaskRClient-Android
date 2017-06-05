package se.taskr.repository;

import java.util.List;
import java.util.Map;

import se.taskr.model.User;
import se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-11.
 */

public interface WorkItemRepository {
    List<WorkItem> getWorkItems(boolean notifyObservers);
    List<WorkItem> getUnstartedWorkItems(boolean notifyObservers);
    List<WorkItem> getStartedWorkItems(boolean notifyObservers);
    List<WorkItem> getDoneWorkItems(boolean notifyObservers);
    List<WorkItem> getWorkItemsByUser(User user);
    WorkItem getWorkItem(long id);
    long addOrUpdateWorkItem(WorkItem workItem);
    void removeWorkItem(WorkItem workItem);
    void assignWorkItem(WorkItem workItem, User user);
    void unAssignWorkItem(WorkItem workItem, User user);
    List<WorkItem> syncWorkItems(List<WorkItem> workItems);
    void syncWorkItemAssignments(List<Map.Entry<String, User>> assignments);
}