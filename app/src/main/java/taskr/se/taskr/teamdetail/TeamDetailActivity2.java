package taskr.se.taskr.teamdetail;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.ActivityTeamDetail2Binding;
import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.utils.UserListAdapter;
import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;
import taskr.se.taskr.selectuser.SelectUserActivity;

import static taskr.se.taskr.selectuser.SelectUserActivity.SELECTED_USER_ID;

public class TeamDetailActivity2 extends AppCompatActivity {

    private static final String EXTRA_TEAM_ID = "team_id";
    private static final int REQUEST_CODE_ADDMEMBER = 1;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(this);
    private Team team;

    public static Intent createIntent(Context context, Team team) {
        Intent intent = new Intent(context, TeamDetailActivity2.class);
        intent.putExtra(EXTRA_TEAM_ID, team.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTeamDetail2Binding binding = DataBindingUtil.setContentView(this, R.layout.activity_team_detail2);

        Intent startingIntent = getIntent();
        Long teamId = startingIntent.getLongExtra(EXTRA_TEAM_ID, 0);
        team = contentProvider.getTeam(teamId);
        final TeamDetailViewModel viewModel = new TeamDetailViewModel(this, team);
        binding.setTeamDetailViewModel(viewModel);

        final EditText teamNameEditText = (EditText) findViewById(R.id.edittext_team_name);
        final EditText teamDescriptionEditText = (EditText) findViewById(R.id.edittext_team_description);

        setEditTextListeners(teamNameEditText, viewModel, team);
        setEditTextListeners(teamDescriptionEditText, viewModel, team);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.team_detail);

        updateMemberList(GlobalVariables.isOnline(this));

        final Button addMemberButton = (Button) findViewById(R.id.add_member_btn);
        addMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = SelectUserActivity.createIntent(getApplicationContext(), team.getUsers());
                startActivityForResult(intent, REQUEST_CODE_ADDMEMBER);
            }
        });
        handleOfflineMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleOfflineMode();
    }

    private void handleOfflineMode() {
        final Button addMemberButton = (Button) findViewById(R.id.add_member_btn);
        final EditText teamNameEditText = (EditText) findViewById(R.id.edittext_team_name);
        final EditText teamDescriptionEditText = (EditText) findViewById(R.id.edittext_team_description);
        android.support.v7.app.ActionBar ab = getSupportActionBar();

        if (GlobalVariables.isOnline(this)) {
            ab.setSubtitle(null);
            addMemberButton.setVisibility(View.VISIBLE);
            teamNameEditText.setEnabled(true);
            teamDescriptionEditText.setEnabled(true);
            teamNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_orange, 0);
            teamDescriptionEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_orange, 0);
            updateMemberList(true);
        } else {
            ab.setSubtitle(R.string.offline_mode);
            addMemberButton.setVisibility(View.INVISIBLE);
            teamNameEditText.setEnabled(false);
            teamDescriptionEditText.setEnabled(false);
            teamNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_gray, 0);
            teamDescriptionEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_gray, 0);
            updateMemberList(false);
        }
        invalidateOptionsMenu();
    }

    private void updateMemberList(final boolean clickable) {
        final ExpandableHeightListView teamMembersListView = (ExpandableHeightListView) findViewById(R.id.team_members_listview);
        if (clickable) {
            final UserListAdapter adapter = new UserListAdapter(this, R.layout.user_list_item, team.getUsers(), R.drawable.ic_clear_orange);
            teamMembersListView.setAdapter(adapter);
        } else {
            final UserListAdapter adapter = new UserListAdapter(this, R.layout.user_list_item, team.getUsers(), R.drawable.ic_clear_gray);
            teamMembersListView.setAdapter(adapter);
        }
        teamMembersListView.setExpanded(true);
        if (clickable) {
            teamMembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final User user = (User) teamMembersListView.getItemAtPosition(position);
                    new AlertDialog.Builder(TeamDetailActivity2.this)
                            .setTitle(R.string.remove_team_member_dialog_title)
                            .setMessage(String.format(getResources().getString(R.string.remove_team_member_dialog_message), user.getUsername(), team.getName()))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    contentProvider.removeTeamMember(team, user);
                                    team.removeMember(user);
                                    updateMemberList(clickable);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create()
                            .show();
                }
            });
        }
        teamMembersListView.setEnabled(clickable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == REQUEST_CODE_ADDMEMBER) {
            if(resultCode == RESULT_OK) {
                Long id = (Long) intent.getExtras().get(SELECTED_USER_ID);
                User newMember = contentProvider.getUser(id);
                team.addMember(newMember);
                contentProvider.addTeamMember(team, newMember);
                updateMemberList(GlobalVariables.isOnline(this));
            }
        }
    }

    private void setEditTextListeners(EditText editText, final TeamDetailViewModel viewModel, final Team team) {
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    view.clearFocus();
                    return true;
                }
                return false;
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !team.getName().equals(viewModel.name.get())) {
                    viewModel.save();
                } else if (!hasFocus && !team.getDescription().equals(viewModel.description.get())) {
                    viewModel.save();
                }
            }
        });
    }

}
