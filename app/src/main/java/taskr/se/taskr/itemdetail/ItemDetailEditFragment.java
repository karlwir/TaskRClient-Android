package taskr.se.taskr.itemdetail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.FragmentAddWorkitemBinding;
import taskr.se.taskr.home.workitemviewmodel.AddWorkItemViewModel;

/**
 * Created by Kevin on 2017-05-26.
 */

public class ItemDetailEditFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAddWorkitemBinding binding = DataBindingUtil.inflate(inflater, R.layout
                .fragment_add_workitem, container, false);

        View view = binding.getRoot();
        binding.setViewModel(new AddWorkItemViewModel(this));

        return view;
    }
}
