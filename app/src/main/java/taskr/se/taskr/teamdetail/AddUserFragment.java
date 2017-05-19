package taskr.se.taskr.teamdetail;

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
import taskr.se.taskr.R;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by John on 2017-05-16.
 */

public class AddUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(getContext());

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_fragment , container , false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.user_list_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        updateAdapter();
    }

    private void updateAdapter(){
        recyclerView.setAdapter(new TeamDetailFragment.UserListAdapter(contentProvider.getUsers(true)));
    }
}
