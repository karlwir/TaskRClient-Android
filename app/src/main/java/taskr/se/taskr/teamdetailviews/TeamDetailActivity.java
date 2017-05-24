package taskr.se.taskr.teamdetailviews;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import taskr.se.taskr.R;
import taskr.se.taskr.teamdetailviews.detailviewmodel.TeamDetailEditViewModel;

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
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            fm.beginTransaction().replace(R.id.team_fragment_container, fragment).commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                    FragmentManager fm = getSupportFragmentManager();
                     Fragment fragment = new TeamDetailEditFragment();
                    fm.beginTransaction().replace(R.id.team_fragment_container, fragment)
                            .commit();
                    item.setVisible(false);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(TeamDetailEditViewModel.isFinished()) {
            finish();
        }
    }
}
