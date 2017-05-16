package taskr.se.taskr.home.itemlistfragment;

import android.content.Intent;
import android.os.Bundle;
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
import taskr.se.taskr.model.WorkItem;

import java.util.List;

/**
 * Created by Jacob on 2017-05-11.
 */

public class ItemListFragment extends Fragment implements ItemListContract.View {

    private RecyclerView recyclerView;
    private Presenter presenter;

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
        presenter = new ItemListPresenterImpl(this, position);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateAdapter();
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
        private Presenter.OnItemClickedListener onItemClickedListener;

        public ItemListViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.workitem_title);
            description = (TextView) itemView.findViewById(R.id.workitem_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickedListener != null) onItemClickedListener.onItemClicked(getAdapterPosition());
                }
            });
        }

        void bindView(WorkItem item) {
            title.setText(item.getTitle());
            description.setText(item.getDescription());
        }

        public void setOnItemClickedListener(Presenter.OnItemClickedListener onItemClickedListener) {
            this.onItemClickedListener = onItemClickedListener;
        }
    }
}
