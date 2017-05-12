package taskr.se.taskr.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import taskr.se.taskr.R;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.WorkItemRepository;
import taskr.se.taskr.repository.WorkItemRepositorySql;

import java.util.List;

/**
 * Created by Jacob on 2017-05-11.
 */

public class ItemListFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        WorkItemRepository workItemRepository = WorkItemRepositorySql.getInstance(getContext());
        List<WorkItem> items = workItemRepository.getWorkItems();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new ItemListAdapter(items));
    }

    private static class ItemListAdapter extends RecyclerView.Adapter<ItemListViewHolder> {

        private List<WorkItem> items;

        public ItemListAdapter(List<WorkItem> items) {
            this.items = items;
        }

        @Override
        public ItemListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View view = inflater.inflate(R.layout.item_list_workitem, parent, false);

            ItemListViewHolder viewHolder = new ItemListViewHolder(view);
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
    }

    private static class ItemListViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView description;

        public ItemListViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.workitem_title);
            description = (TextView) itemView.findViewById(R.id.workitem_description);
        }

        void bindView(WorkItem item) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());
        }
    }
}
