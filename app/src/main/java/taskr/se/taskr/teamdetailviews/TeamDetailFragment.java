package taskr.se.taskr.teamdetailviews;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.TeamDetailFragmentBinding;
import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;


/**
 * Created by Kevin on 2017-05-16.
 */

public class TeamDetailFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(getContext());
    private Team team = new Team("Team", "team");

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        TeamDetailFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.team_detail_fragment, container, false);
        View view = binding.getRoot();
        Team loggedInUserTeam = GlobalVariables.loggedInUser.getTeams().get(0);
        if (loggedInUserTeam != null) {
            team = contentProvider.getTeam(loggedInUserTeam.getId());
        }
        binding.setTeam(team);
        navigateToAddUserActivity(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.user_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getSentExtras();
        updateAdapter();

    }

    public void updateAdapter() {
        recyclerView.setAdapter(new UserListAdapter(team.getUsers()));
    }

    private void navigateToAddUserActivity(View view) {
        Button button = (Button) view.findViewById(R.id.add_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.createIntent(getActivity());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void getSentExtras() {
        if (getArguments() != null) {
            long[] userIds = getArguments().getLongArray("members");
            for (Long id : userIds) {
                User user = contentProvider.getUser(id);
                contentProvider.addTeamMember(team, user);
                updateAdapter();
            }
        }

    }

    private static class UserListAdapter extends RecyclerView.Adapter<UserListViewHolder> {

        private final List<User> users;

        UserListAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.users_in_detail_view, parent, false);
            return new UserListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserListViewHolder holder, int position) {
            holder.bindView(users.get(position));
        }

        @Override
        public int getItemCount() {
            return this.users.size();
        }
    }

    private static class UserListViewHolder extends RecyclerView.ViewHolder {

        private TextView userContent;
        private StringBuilder builder;

        UserListViewHolder(View itemView) {
            super(itemView);
            this.builder = new StringBuilder();
            this.userContent = (TextView) itemView.findViewById(R.id.user_content);
        }

        void bindView(User user) {
            builder.append(user.getFirstname()).append(" ").append(user.getLastname());
            userContent.setText(builder);
        }

    }


}
