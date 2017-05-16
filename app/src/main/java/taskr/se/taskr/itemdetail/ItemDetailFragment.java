package taskr.se.taskr.itemdetail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.FragmentItemDetailBinding;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by Kevin on 2017-05-11.
 */


public class ItemDetailFragment extends Fragment{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentItemDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_detail,container, false);
        View view = binding.getRoot();
        Bundle bundle = getArguments();
        Long id = bundle.getLong("id");
        Log.d(id.toString(), "onCreateView: ");
        WorkItem item = TaskRContentProviderImpl.getInstance(getContext()).getWorkItem(id);
        binding.setWorkitem(item);
        return view;


    }
}
