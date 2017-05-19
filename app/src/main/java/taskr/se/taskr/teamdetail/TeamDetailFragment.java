package taskr.se.taskr.teamdetail;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.TeamDetailFragmentBinding;
import taskr.se.taskr.home.HomeActivity;
import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;


/**
 * Created by John on 2017-05-16.
 */

public class TeamDetailFragment extends Fragment {

    private RecyclerView recyclerView;
    private Team team;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(getContext());
    private Button button;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        TeamDetailFragmentBinding binding = DataBindingUtil.inflate(inflater , R.layout.team_detail_fragment, container , false);
        View view = binding.getRoot();
        team = new Team("Team Taekwando", "We need SVARTBÄLTE");
        contentProvider.addOrUpdateTeam(team);
        for(int i = 0; i < 5; i++){
            team.addMember(new User("John"+i,"Lindström"+i,"JoLind"+i));
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

        updateAdapter();

    }

    private void updateAdapter(){
        recyclerView.setAdapter(new UserListAdapter(team.getMembers()));

    }

    private void navigateToAddUserActivity(View view){
        button = (Button) view.findViewById(R.id.add_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddUserActivity.createIntent(getActivity());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    protected static class UserListAdapter extends RecyclerView.Adapter<UserListViewHolder>{

        private final List<User> users;

        public UserListAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.users_in_detail_view,parent, false);
            return new UserListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserListViewHolder holder, int position) {
            User user = users.get(position);
            holder.bindView(user);
        }

        @Override
        public int getItemCount() {
            return this.users.size();
        }
    }

    protected static class UserListViewHolder extends RecyclerView.ViewHolder{

        private TextView userContent;
        private StringBuilder builder;

        public UserListViewHolder(View itemView) {
            super(itemView);
            this.builder = new StringBuilder();
            this.userContent = (TextView) itemView.findViewById(R.id.user_content);
        }

        void bindView(User user){
            builder.append(user.getFirstname()).append(" ").append(user.getLastname());
            userContent.setText(builder);
        }

    }



}
