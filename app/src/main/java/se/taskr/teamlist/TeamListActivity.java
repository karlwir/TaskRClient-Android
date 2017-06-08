package se.taskr.teamlist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.List;

import se.taskr.R;
import se.taskr.global.GlobalVariables;
import se.taskr.model.Team;
import se.taskr.repository.TaskRContentProvider;
import se.taskr.repository.TaskRContentProviderImpl;
import se.taskr.teamdetail.TeamDetailActivity;
import se.taskr.utils.TeamListAdapter;

public class TeamListActivity extends AppCompatActivity {

    private final TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(this);

    private List<Team> teams;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, TeamListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.choose_team);

        updateTeamList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TeamDetailActivity.createNewTeamIntent(getApplicationContext());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTeamList();
    }

    private void updateTeamList() {
        teams = contentProvider.getTeams(false);
        final ExpandableHeightListView teamListView = (ExpandableHeightListView) findViewById(R.id.team_listview);
        final TeamListAdapter adapter = new TeamListAdapter(this, R.layout.team_list_item, teams, R.drawable.ic_add_orange);
        teamListView.setAdapter(adapter);
        teamListView.setExpanded(true);
        teamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Team team = (Team) teamListView.getItemAtPosition(position);
                new AlertDialog.Builder(TeamListActivity.this)
                        .setTitle(R.string.join_team_dialog_title)
                        .setMessage(String.format(getResources().getString(R.string.join_team_dialog_message), team.getName()))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                contentProvider.addTeamMember(team, GlobalVariables.loggedInUser);
                                team.addMember(GlobalVariables.loggedInUser);

                                setResult(Activity.RESULT_OK,new Intent());
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create()
                        .show();
                updateTeamList();
            }
        });
    }

}
