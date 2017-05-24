package taskr.se.taskr.teamdetailviews;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.EditTeamDetailFragmentBinding;
import taskr.se.taskr.databinding.TeamDetailFragmentBinding;
import taskr.se.taskr.teamdetailviews.detailviewmodel.TeamDetailEditViewModel;

/**
 * Created by John on 2017-05-24.
 */

public class TeamDetailEditFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EditTeamDetailFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.edit_team_detail_fragment,
                container, false);
        View view = binding.getRoot();
        binding.setEditViewModel(new TeamDetailEditViewModel(getContext()));
        return view;
    }
}
