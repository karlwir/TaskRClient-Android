package taskr.se.taskr.itemdetail;

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
import android.widget.TextView;

import java.util.ArrayList;
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
    private List<User> myUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentItemDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail,container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();
        Long id = bundle.getLong("id");
        WorkItem item = TaskRContentProviderImpl.getInstance(getContext()).getWorkItem(id);
        binding.setWorkitem(item);
        myUsers = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            myUsers.add(new User("Kevin"+i, "Briffa"+i, "KeBr"+i));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.item_detail_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateAdapter();
    }

    private void updateAdapter(){
        recyclerView.setAdapter(new ItemDetailAdapter(myUsers));

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

       // private final TextView tvFirstname;
       // private final TextView tvLastname;
        private final TextView tvUsername;

        public ItemDetailViewHolder(View itemView) {
            super(itemView);

            this.tvUsername = (TextView) itemView.findViewById(R.id.item_detail_username);
        }

        void bindView(User user){
            tvUsername.setText(user.getUsername().toString());
        }

    }
}
