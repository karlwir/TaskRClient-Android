package taskr.se.taskr.teamdetail;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.TeamDetailFragmentBinding;
import taskr.se.taskr.model.Team;


/**
 * Created by John on 2017-05-16.
 */

public class TeamDetailFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        TeamDetailFragmentBinding binding = DataBindingUtil.inflate(inflater , R.layout.team_detail_fragment, container , false);
        final View view = binding.getRoot();

       final Team team = new Team("Johns awesome team", "Teamet är AWESOME");
        binding.setTeam(team);

        Button btn = (Button) view.findViewById(R.id.add_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity() , "HELLO", Toast.LENGTH_SHORT).show();

                 Intent intent = AddUserActivity.createIntent(getContext());
                intent.putExtra("id", team.getId());
                 startActivity(intent);
            }
        });

        return view;
    }

}
