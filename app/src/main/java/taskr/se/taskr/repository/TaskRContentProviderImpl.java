package taskr.se.taskr.repository;

import android.content.Context;
import android.util.Log;

import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;

import java.util.AbstractMap;
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
    private final TeamRepository teamRepository;
    private final TeamHttpClient teamHttpClient;
    private final Context context;
    private static TaskRContentProviderImpl instance;
    private List<Presenter> observers;
    private static final long SYNC_TIMEOUT = 5000;
    private Long lastWorkitemSyncTimeStamp;
    private Long lastTeamSyncTimeStamp;

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
        teamRepository = TeamRepositorySql.getInstance(context);
        teamHttpClient = TeamHttpClient.getInstance();
        this.context = context;
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
    public void initData(final OnResultEventListener<Boolean> listener) {
        if (GlobalVariables.isOnline(context)) {
            userHttpClient.getUsers(new OnResultEventListener<List<User>>() {
                @Override
                public void onResult(List<User> result) {
                    if (result != null) {
                        syncUsers(result, true);
                    }
                    workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
                        @Override
                        public void onResult(List<WorkItem> result) {
                            if (result != null) {
                                syncWorkItems(result);
                            }
                            teamHttpClient.getTeams(new OnResultEventListener<List<Team>>() {
                                @Override
                                public void onResult(List<Team> result) {
                                    if (result != null) {
                                        syncTeams(result);
                                    }
                                    listener.onResult(true);
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public List<User> getUsers(final boolean notifyObservers) {
        if (GlobalVariables.isOnline(context)) {
            userHttpClient.getUsers(new OnResultEventListener<List<User>>() {
                @Override
                public void onResult(List<User> result) {
                    if (result != null) {
                        syncUsers(result, true);
                    }

                }
            });
        }
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

    @Override
    public List<User> syncUsers(List<User> users, boolean removeUnsyncedLocals) {
        return userRepository.syncUsers(users, removeUnsyncedLocals);
    }

    @Override
    public List<WorkItem> getWorkItems(final boolean notifyObservers) {
        if (GlobalVariables.isOnline(context)) {
            workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
                @Override
                public void onResult(List<WorkItem> result) {
                    if (result != null) {
                        syncWorkItems(result);
                    }
                    if (notifyObservers) notifyObservers();
                }
            });
        }
        return workItemRepository.getWorkItems(false);
    }

    @Override
    public List<WorkItem> getUnstartedWorkItems(final boolean notifyObservers) {
        if (GlobalVariables.isOnline(context)) {
            workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
                @Override
                public void onResult(List<WorkItem> result) {
                    if (result != null) {
                        syncWorkItems(result);
                    }
                    if (notifyObservers) notifyObservers();
                }
            });
        }
        return workItemRepository.getUnstartedWorkItems(false);
    }

    @Override
    public List<WorkItem> getStartedWorkItems(final boolean notifyObservers) {
        if (GlobalVariables.isOnline(context)) {

            workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
                @Override
                public void onResult(List<WorkItem> result) {
                    if (result != null) {
                        syncWorkItems(result);
                    }
                    if (notifyObservers) notifyObservers();
                }
            });
        }
        return workItemRepository.getStartedWorkItems(false);
    }

    @Override
    public List<WorkItem> getDoneWorkItems(final boolean notifyObservers) {
        if (GlobalVariables.isOnline(context)) {

            workItemHttpClient.getWorkItems(new OnResultEventListener<List<WorkItem>>() {
                @Override
                public void onResult(List<WorkItem> result) {
                    if (result != null) {
                        syncWorkItems(result);
                    }
                    if (notifyObservers) notifyObservers();
                }
            });
        }
        return workItemRepository.getDoneWorkItems(false);
    }

    @Override
    public List<WorkItem> getWorkItemsByUser(User user) {
        return workItemRepository.getWorkItemsByUser(user);
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
            workItemHttpClient.postWorkItem(workItem, new OnResultEventListener<String>() {
                @Override
                public void onResult(String generatedKey) {
                    WorkItem _workItem = new WorkItem(id, generatedKey, workItem.getTitle(), workItem.getDescription(), workItem.getStatus());
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
        workItemRepository.assignWorkItem(workItem, user);
        workItemHttpClient.assignWorkItem(workItem, user);
    }

    @Override
    public void unAssignWorkItem(WorkItem workItem, User user) {
        workItemRepository.unAssignWorkItem(workItem, user);
        workItemHttpClient.unAssignWorkItem(workItem, user);
    }

    @Override
    public List<WorkItem> syncWorkItems(List<WorkItem> workItems) {
        Long timeStamp = System.currentTimeMillis();

        if ( lastWorkitemSyncTimeStamp == null || timeStamp - lastWorkitemSyncTimeStamp - SYNC_TIMEOUT > 0) {
            List<Map.Entry<String, User>> assignmentsOnServer = new ArrayList<>();
            for (WorkItem workItem : workItems) {
                if (workItem.getUsers().size() > 0) {
                    List<User> syncedUsersWithAssignments = syncUsers(workItem.getUsers(), false);

                    for(User user : syncedUsersWithAssignments) {
                        Map.Entry<String, User> assignment = new AbstractMap.SimpleEntry<>(workItem.getItemKey(), user);
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
    public void syncWorkItemAssignments(List<Map.Entry<String, User>> assignments) {
        workItemRepository.syncWorkItemAssignments(assignments);
    }

    @Override
    public List<Team> getTeams(final boolean notifyObservers) {
        if (GlobalVariables.isOnline(context)) {

            teamHttpClient.getTeams(new OnResultEventListener<List<Team>>() {
                @Override
                public void onResult(List<Team> result) {
                    if (result != null) {
                        syncTeams(result);
                    }
                    if (notifyObservers) notifyObservers();
                }
            });
        }
        return teamRepository.getTeams(false);
    }

    @Override
    public Team getTeam(long id) {
        return teamRepository.getTeam(id);
    }

    @Override
    public long addOrUpdateTeam(final Team team) {
        final long id = teamRepository.addOrUpdateTeam(team);
        if(team.hasBeenSavedToServer()) {
            teamHttpClient.putTeam(team);
        } else {
            teamHttpClient.postTeam(team, new OnResultEventListener<String>() {
                @Override
                public void onResult(String generatedKey) {
                    Team _team = new Team(id, generatedKey, team.getName(), team.getDescription());
                    teamRepository.addOrUpdateTeam(_team);
                }
            });
        }
        return id;
    }

    @Override
    public void removeTeam(Team team) {
        teamRepository.removeTeam(team);
        teamHttpClient.deleteTeam(team);
    }

    @Override
    public void addTeamMember(Team team, User user) {
        teamRepository.addTeamMember(team, user);
        teamHttpClient.addTeamMember(team, user);
    }

    @Override
    public void removeTeamMember(Team team, User user) {
        teamRepository.removeTeamMember(team, user);
        teamHttpClient.removeTeamMember(team, user);
    }

    @Override
    public void syncTeams(List<Team> teams) {
        Long timeStamp = System.currentTimeMillis();
        if ( lastTeamSyncTimeStamp == null || timeStamp - lastTeamSyncTimeStamp - SYNC_TIMEOUT > 0) {
            List<Map.Entry<String, User>> mebershipsOnServer = new ArrayList<>();

            for (Team team : teams) {
                if(team.getUsers().size() > 0) {
                    List<User> syncedMembers = syncUsers(team.getUsers(), false);
                    for (User user : syncedMembers) {
                        Map.Entry<String, User> membership = new AbstractMap.SimpleEntry<>(team.getItemKey(), user);
                        mebershipsOnServer.add(membership);
                    }
                }
            }
            teamRepository.syncTeams(teams);
            syncTeamMemberships(mebershipsOnServer);
            lastTeamSyncTimeStamp = timeStamp;
        }
    }

    public void syncTeamMemberships(List<Map.Entry<String, User>> mebershipsOnServer) {
        teamRepository.syncTeamMemberships(mebershipsOnServer);
    }
}