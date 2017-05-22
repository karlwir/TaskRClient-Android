package taskr.se.taskr.home.itemlistfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import taskr.se.taskr.R;
import taskr.se.taskr.home.itemlistfragment.ItemListContract.Presenter;
import taskr.se.taskr.itemdetail.ItemDetailActivity;
import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 2017-05-11.
 */

public class ItemListFragment extends Fragment implements ItemListContract.View {

    private RecyclerView recyclerView;
    private Presenter presenter;

    public static ItemListFragment newInstance() {
        Bundle args = new Bundle();
        args.putInt("position", -1);
        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        int position = FragmentPagerItem.getPosition(getArguments());
        if(getArguments().getInt("position") == -1) position = -1;
        presenter = new ItemListPresenterImpl(this, position);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateAdapter();
    }

    public void onEditSearchInput(String searchQuery) {
        List<WorkItem> items = presenter.getItems();
        List<WorkItem> filteredItems = filterItems(items, searchQuery);
        updateAdapter(filteredItems);
    }

    private List<WorkItem> filterItems(List<WorkItem> items, String searchQuery) {
        List<WorkItem> result = new ArrayList<>();
        for(WorkItem item : items) {
            String title = item.getTitle().toLowerCase();
            String description = item.getDescription().toLowerCase();
            searchQuery = searchQuery.toLowerCase();
            if(title.contains(searchQuery) || description.contains(searchQuery)) {
                result.add(item);
            }
        }
        return result;
    }

    private void updateAdapter(final List<WorkItem> items) {
        ItemListAdapter adapter = new ItemListAdapter(items);
        adapter.setOnItemClickedListener(new Presenter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int id) {
                long itemId = items.get(id).getId();
                navigateToDetailView(itemId);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void updateAdapter() {
        ItemListAdapter adapter = new ItemListAdapter(presenter.getItems());
        adapter.setOnItemClickedListener(new Presenter.OnItemClickedListener() {
            @Override
            public void onItemClicked(int id) {
                long itemId = presenter.getItems().get(id).getId();
                navigateToDetailView(itemId);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void navigateToDetailView(long id) {
        Intent intent = ItemDetailActivity.createIntent(getContext());
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private static class ItemListAdapter extends RecyclerView.Adapter<ItemListViewHolder> {

        private List<WorkItem> items;
        private Presenter.OnItemClickedListener onItemClickedListener;

        public ItemListAdapter(List<WorkItem> items) {
            this.items = items;
        }

        @Override
        public ItemListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View view = inflater.inflate(R.layout.item_list_workitem, parent, false);

            final ItemListViewHolder viewHolder = new ItemListViewHolder(view);
            viewHolder.setOnItemClickedListener(new Presenter.OnItemClickedListener() {
                @Override
                public void onItemClicked(int id) {
                    onItemClickedListener.onItemClicked(id);
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ItemListViewHolder holder, int position) {
            holder.bindView(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void setOnItemClickedListener(Presenter.OnItemClickedListener onItemClickedListener) {
            this.onItemClickedListener = onItemClickedListener;
        }
    }

    private static class ItemListViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;
        private final TextView assignedUser;
        private final TextView statusOnWorkItem;
        private Presenter.OnItemClickedListener onItemClickedListener;

        public ItemListViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.workitem_title);
            description = (TextView) itemView.findViewById(R.id.workitem_description);
            assignedUser = (TextView) itemView.findViewById(R.id.assigned_user);
            statusOnWorkItem = (TextView) itemView.findViewById(R.id.workitem_statusbar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickedListener != null) onItemClickedListener.onItemClicked(getAdapterPosition());
                }
            });
        }

        void bindView(WorkItem item) {
            List<User> assignedUsers = item.getUsers();
            title.setText(item.getTitle());
            description.setText(item.getDescription());
            statusOnWorkItem.setText(item.getStatus());


             switch (item.getStatus().toUpperCase()) {
                 case "UNSTARTED":
                     statusOnWorkItem.setBackgroundColor(Color.parseColor("#a6a6a6"));
                    break;
                 case  "STARTED":
                     statusOnWorkItem.setBackgroundColor(Color.parseColor("#f5a623"));
                    break;
                 case  "DONE":
                     statusOnWorkItem.setBackgroundColor(Color.parseColor("#7ed321"));
                    break;
                 case  "ARCHIVED":
                     statusOnWorkItem.setBackgroundColor(Color.parseColor("#9a844f"));
                    break;


            }


            if (assignedUsers.size() > 0) {
                String usernames = "";
                for (User user : assignedUsers) {
                    usernames = usernames + " @" + user.getUsername() + ",";
                }
                usernames = usernames.substring(0, usernames.length()-1);
                assignedUser.setText(usernames);
            }
        }

        public void setOnItemClickedListener(Presenter.OnItemClickedListener onItemClickedListener) {
            this.onItemClickedListener = onItemClickedListener;
        }
    }
}
