package taskr.se.taskr.teamdetailviews;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.R;
import taskr.se.taskr.model.User;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by John on 2017-05-16.
 */

public class AddUserFragment extends Fragment {

    interface OnItemMarkedListener {
        void onItemMarked(View v, Long id);
    }

    interface OnItemPositionListener {
        void onPositionMarked(View v, int id);
    }

    private RecyclerView recyclerView;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(getContext());
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private List<Long> userIds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_fragment, container, false);

        userIds = new ArrayList<>();
        navigateToTeamDetailView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = (RecyclerView) view.findViewById(R.id.user_list_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        updateAdapter();
    }

    private void updateAdapter() {
        UserListAdapter adapter = new UserListAdapter(contentProvider.getUsers(false));
        adapter.setOnItemMarkedListener(new OnItemMarkedListener() {
            @Override
            public void onItemMarked(View v, Long id) {
                if (userIds.contains(id)) {
                    userIds.remove(id);
                    v.setBackgroundColor(Color.parseColor("#FAFAFA"));
                    v.setSelected(false);
                } else {
                    userIds.add(id);
                    v.setBackgroundColor(Color.parseColor("#E3E3E3"));
                    v.setSelected(true);

                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void navigateToTeamDetailView(View view) {
        Button button = (Button) view.findViewById(R.id.savebtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = TeamDetailActivity.createIntent(getContext());
                Bundle bundle = new Bundle();
                bundle.putLongArray("members", allUserIds());
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private long[] allUserIds() {
        long[] allUserIds = new long[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            allUserIds[i] = userIds.get(i);
        }
        return allUserIds;
    }


    private static class UserListAdapter extends RecyclerView.Adapter<UserListViewHolder> {

        private final List<User> users;
        private OnItemMarkedListener onItemMarkedListener;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        UserListAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View view = inflater.inflate(R.layout.users_in_detail_view, parent, false);

            final UserListViewHolder viewHolder = new UserListViewHolder(view);
            viewHolder.setOnItemPositionListener(new OnItemPositionListener() {
                @Override
                public void onPositionMarked(View v, int id) {
                    onItemMarkedListener.onItemMarked(v, users.get(id).getId());
                }
            });

            return viewHolder;

        }

        @Override
        public void onBindViewHolder(UserListViewHolder holder, int position) {
            holder.bindView(users.get(position));

        }

        @Override
        public int getItemCount() {
            return users.size();

        }

        public void setOnItemMarkedListener(OnItemMarkedListener onItemMarkedListener) {
            this.onItemMarkedListener = onItemMarkedListener;
        }
    }

    private static class UserListViewHolder extends RecyclerView.ViewHolder {

        private TextView userContent;
        private StringBuilder builder;
        private OnItemPositionListener onItemPositionListener;

        public UserListViewHolder(final View itemView) {
            super(itemView);
            this.builder = new StringBuilder();
            this.userContent = (TextView) itemView.findViewById(R.id.user_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemPositionListener.onPositionMarked(v, getAdapterPosition());
                }
            });
        }

        void bindView(User user) {
            builder.append(user.getFirstname()).append(" ").append(user.getLastname());
            userContent.setText(builder);

        }

        public void setOnItemPositionListener(OnItemPositionListener onItemPositionListener) {
            this.onItemPositionListener = onItemPositionListener;
        }
    }
}