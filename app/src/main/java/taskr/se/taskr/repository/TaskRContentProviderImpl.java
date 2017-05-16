package taskr.se.taskr.repository;

import android.content.Context;
import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;

import java.util.ArrayList;
import java.util.List;

import static taskr.se.taskr.home.itemlistfragment.ItemListContract.Presenter;

/**
 * Created by kawi01 on 2017-05-15.
 */

public class TaskRContentProviderImpl implements TaskRContentProvider {

    private final UserRepository userRepository;
    private final WorkItemHttpClient workItemClient;
    private final WorkItemRepository workItemRepository;
    private static TaskRContentProviderImpl instance;
    private List<Presenter> observers;

    public static synchronized TaskRContentProviderImpl getInstance(Context context) {
        if(instance == null) {
            instance = new TaskRContentProviderImpl(context);
        }
        return instance;
    }

    private TaskRContentProviderImpl(Context context) {
        userRepository = UserRepositorySql.getInstance(context);
        workItemRepository = WorkItemRepositorySql.getInstance(context);
        workItemClient = WorkItemHttpClient.getInstance(context);
        observers = new ArrayList<>();
    }

    public void registerObserver(Presenter presenter) {
        observers.add(presenter);
    }

    private void notifyObservers() {
        for(Presenter presenter : observers) {
            presenter.notifyChange();
        }
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User getUser(long id) {
        return null;
    }

    @Override
    public long addOrUpdateUser(User user) {
        return 0;
    }

    @Override
    public void removeUser(User user) {

    }

    @Override
    public List<WorkItem> getWorkItems(final boolean notifyObservers) {
        workItemClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
            @Override
            public void onResult(List<WorkItem> result) {
                if (result != null) {
                    syncWorkItems(result);
                }
                if(notifyObservers) notifyObservers();
            }
        });
        return workItemRepository.getWorkItems(false);
    }

    @Override
    public List<WorkItem> getUnstartedWorkItems(final boolean notifyObservers) {
        workItemClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
            @Override
            public void onResult(List<WorkItem> result) {
                if (result != null) {
                    syncWorkItems(result);
                }
                if(notifyObservers) notifyObservers();
            }
        });
        return workItemRepository.getUnstartedWorkItems(false);
    }

    @Override
    public List<WorkItem> getStartedWorkItems(final boolean notifyObservers) {
        workItemClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
            @Override
            public void onResult(List<WorkItem> result) {
                if (result != null) {
                    syncWorkItems(result);
                }
                if(notifyObservers) notifyObservers();
            }
        });
        return workItemRepository.getStartedWorkItems(false);
    }

    @Override
    public List<WorkItem> getDoneWorkItems(final boolean notifyObservers) {
        workItemClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
            @Override
            public void onResult(List<WorkItem> result) {
                if (result != null) {
                    syncWorkItems(result);
                }
                if(notifyObservers) notifyObservers();
            }
        });
        return workItemRepository.getDoneWorkItems(false);
    }

    @Override
    public List<WorkItem> getMyWorkItems(final boolean notifyObservers) {
        return null;
    }

    @Override
    public List<WorkItem> getWorkItemsByUser(User user) {
        return null;
    }

    @Override
    public List<WorkItem> searchWorkItem(String query) {
        return workItemRepository.searchWorkItem(query);
    }

    @Override
    public WorkItem getWorkItem(long id) {
        return workItemRepository.getWorkItem(id);
    }

    @Override
    public long addOrUpdateWorkItem(final WorkItem workItem) {
        final long id = workItemRepository.addOrUpdateWorkItem(workItem);
        if (workItem.hasBeenSavedToServer()) {
            workItemClient.putWorkItem(workItem);
        } else {
            workItemClient.postWorkItem(workItem, new OnResultEventListener() {
                @Override
                public void onResult(Object generatedKey) {
                    WorkItem _workItem = new WorkItem(id, (String) generatedKey, workItem.getTitle(), workItem.getDescription(), workItem.getStatus());
                    workItemRepository.addOrUpdateWorkItem(workItem);
                }
            });
        }
        return id;
    }

    @Override
    public void removeWorkItem(WorkItem workItem) {
        workItemRepository.removeWorkItem(workItem);
        workItemClient.deleteWorkItem(workItem);
    }

    @Override
    public void assignWorkItem(WorkItem workItem, User user) {

    }

    @Override
    public void unAssignWorkItem(WorkItem workItem, User user) {

    }

    @Override
    public void syncWorkItems(List<WorkItem> workItems) {
        workItemRepository.syncWorkItems(workItems);
    }

    @Override
    public List<Team> getTeams() {
        return null;
    }

    @Override
    public Team getTeam(long id) {
        return null;
    }

    @Override
    public long addOrUpdateTeam(Team team) {
        return 0;
    }

    @Override
    public void deleteTeam(Team team) {

    }

    @Override
    public void addTeamMember(Team team, User user) {

    }

    @Override
    public void removeTeamMember(Team team, User user) {

    }
}
