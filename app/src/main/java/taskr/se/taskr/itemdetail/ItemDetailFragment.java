package taskr.se.taskr.itemdetail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import taskr.se.taskr.R;
import taskr.se.taskr.databinding.FragmentItemDetailBinding;
import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by Kevin on 2017-05-11.
 */


public class ItemDetailFragment extends Fragment{

    private RecyclerView recyclerView;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(getContext());
    private WorkItem item;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentItemDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail,container, false);
        View view = binding.getRoot();
        item = contentProvider.getWorkItem(getExtrasId());
        for(int i = 0; i < 5; i++){
            item.addUser(new User("Kevin"+i,"Briffa"+i, "Kebr"+i));
        }
        binding.setWorkitem(item);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.user_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateAdapter();
    }

    private void updateAdapter(){
        recyclerView.setAdapter(new ItemDetailAdapter(item.getUsers()));

    }

    private Long getExtrasId(){
        Bundle bundle = getArguments();
        Long id = bundle.getLong("id");
        return id;
    }

    private static class ItemDetailAdapter extends RecyclerView.Adapter<ItemDetailViewHolder>{

        private final List<User> users;

        public ItemDetailAdapter(List<User> users){
            this.users = users;
        }

        @Override
        public ItemDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v  = inflater.inflate(R.layout.users_in_detail_view, parent, false);

            return new ItemDetailViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ItemDetailViewHolder holder, int position) {
            User user = users.get(position);
            holder.bindView(user);
        }

        @Override
        public int getItemCount() {
            return this.users.size();
        }
    }

    private static class ItemDetailViewHolder extends RecyclerView.ViewHolder{

        private final TextView userContent;

        public ItemDetailViewHolder(View itemView) {
            super(itemView);

            this.userContent = (TextView) itemView.findViewById(R.id.user_content);
        }

        void bindView(User user){
            userContent.setText("@"+user.getUsername().toString());
        }

    }
}
