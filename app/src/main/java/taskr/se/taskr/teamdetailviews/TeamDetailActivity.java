package taskr.se.taskr.teamdetailviews;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import taskr.se.taskr.R;

public class TeamDetailActivity extends AppCompatActivity {

    private Bundle bundle;

    public static Intent createIntent(Context context) {
        return new Intent(context, TeamDetailActivity.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);


        bundle = getIntent().getExtras();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.team_fragment_container);
        if (fragment == null) {
            fragment = new TeamDetailFragment();
            if(bundle != null) {
                fragment.setArguments(bundle);
            }
            fm.beginTransaction().add(R.id.team_fragment_container, fragment).commit();
        }


    }
}
