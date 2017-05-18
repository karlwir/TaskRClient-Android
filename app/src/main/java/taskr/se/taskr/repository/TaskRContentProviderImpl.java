package taskr.se.taskr.repository;

import android.content.Context;
import android.util.Log;

import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static taskr.se.taskr.home.itemlistfragment.ItemListContract.Presenter;

/**
 * Created by kawi01 on 2017-05-15.
 */

public class TaskRContentProviderImpl implements TaskRContentProvider {

    private final UserRepository userRepository;
    private final UserHttpClient userHttpClient;
    private final WorkItemHttpClient workItemHttpClient;
    private final WorkItemRepository workItemRepository;
    private static TaskRContentProviderImpl instance;
    private List<Presenter> observers;
    private Long lastWorkitemSyncTimeStamp = null;

    public static synchronized TaskRContentProviderImpl getInstance(Context context) {
        if(instance == null) {
            instance = new TaskRContentProviderImpl(context);
        }
        return instance;
    }

    private TaskRContentProviderImpl(Context context) {
        userRepository = UserRepositorySql.getInstance(context);
        userHttpClient = UserHttpClient.getInstance();
        workItemRepository = WorkItemRepositorySql.getInstance(context);
        workItemHttpClient = WorkItemHttpClient.getInstance();
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
    public List<User> getUsers(final boolean notifyObservers) {
        userHttpClient.getUsers(new OnResultEventListener<List<User>>() {
            @Override
            public void onResult(List<User> result) {
                if (result != null) {
                    syncUsers(result, true);
                }
            }
        });
        return userRepository.getUsers(notifyObservers);
    }

    @Override
    public User getUser(long id) {
        return userRepository.getUser(id);
    }

    @Override
    public long addOrUpdateUser(final User user) {
        final long id = userRepository.addOrUpdateUser(user);
        if (user.hasBeenSavedToServer()) {
            userHttpClient.putUser(user);
        } else {
            userHttpClient.postUser(user, new OnResultEventListener<String>() {
                @Override
                public void onResult(String generatedKey) {
                    User _user = new User(id, generatedKey, user.getFirstname(), user.getLastname(), user.getUsername() );
                    userRepository.addOrUpdateUser(_user);
                }
            });
        }
        return id;
    }

    @Override
    public void removeUser(User user) {
        userRepository.removeUser(user);
        userHttpClient.deleteUser(user);
    }

    public List<User> syncUsers(List<User> users, boolean removeUnsyncedLocals) {
        return userRepository.syncUsers(users, removeUnsyncedLocals);
    }

    @Override
    public List<WorkItem> getWorkItems(final boolean notifyObservers) {
        workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
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
        workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
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
        workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
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
        workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
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
        return workItemRepository.getWorkItemsByUser(user);
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
            workItemHttpClient.putWorkItem(workItem);
        } else {
            workItemHttpClient.postWorkItem(workItem, new OnResultEventListener() {
                @Override
                public void onResult(Object generatedKey) {
                    WorkItem _workItem = new WorkItem(id, (String) generatedKey, workItem.getTitle(), workItem.getDescription(), workItem.getStatus());
                    workItemRepository.addOrUpdateWorkItem(_workItem);
                }
            });
        }
        return id;
    }

    @Override
    public void removeWorkItem(WorkItem workItem) {
        workItemRepository.removeWorkItem(workItem);
        workItemHttpClient.deleteWorkItem(workItem);
    }

    @Override
    public void assignWorkItem(WorkItem workItem, User user) {

    }

    @Override
    public void unAssignWorkItem(WorkItem workItem, User user) {

    }

    @Override
    public List<WorkItem> syncWorkItems(List<WorkItem> workItems) {
        Long timeStamp = System.currentTimeMillis();

        if ( lastWorkitemSyncTimeStamp == null || timeStamp - lastWorkitemSyncTimeStamp - 5000 > 0) {
            List<Map<String, User>> assignmentsOnServer = new ArrayList<>();
            for (WorkItem workItem : workItems) {
                if (workItem.getUsers().size() > 0) {
                    List<User> syncedUsersWithAssignments = syncUsers(workItem.getUsers(), false);

                    for(int i= 0; i < syncedUsersWithAssignments.size(); i++ ) {
                        Map<String, User> assignment = new HashMap<>();
                        assignment.put(workItem.getItemKey(), syncedUsersWithAssignments.get(i));
                        assignmentsOnServer.add(assignment);
                    }
                }
            }
            workItemRepository.syncWorkItems(workItems);
            syncWorkItemAssignments(assignmentsOnServer);
            lastWorkitemSyncTimeStamp = timeStamp;
        }
        return null;
    }

    @Override
    public void syncWorkItemAssignments(List<Map<String, User>> assignments) {
        workItemRepository.syncWorkItemAssignments(assignments);
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