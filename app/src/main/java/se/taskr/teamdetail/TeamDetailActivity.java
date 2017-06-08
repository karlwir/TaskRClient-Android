package se.taskr.teamdetail;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import se.taskr.R;
import se.taskr.databinding.ActivityTeamDetailBinding;
import se.taskr.global.GlobalVariables;
import se.taskr.utils.UserListAdapter;
import se.taskr.model.Team;
import se.taskr.model.User;
import se.taskr.repository.TaskRContentProvider;
import se.taskr.repository.TaskRContentProviderImpl;
import se.taskr.selectuser.SelectUserActivity;

import static se.taskr.selectuser.SelectUserActivity.SELECTED_USER_ID;

public class TeamDetailActivity extends AppCompatActivity {

    private static final String EXTRA_TEAM_ID = "team_id";
    private static final String EXTRA_NEW_TEAM = "new_team";
    private static final int REQUEST_CODE_ADDMEMBER = 1;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(this);
    private Team team;
    private boolean newTeam;

    public static Intent createIntent(Context context, Team team) {
        Intent intent = new Intent(context, TeamDetailActivity.class);
        intent.putExtra(EXTRA_TEAM_ID, team.getId());
        intent.putExtra(EXTRA_NEW_TEAM, false);
        return intent;
    }

    public static Intent createNewTeamIntent(Context context) {
        Intent intent = new Intent(context, TeamDetailActivity.class);
        intent.putExtra(EXTRA_NEW_TEAM, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTeamDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_team_detail);

        Intent startingIntent = getIntent();
        newTeam = startingIntent.getBooleanExtra(EXTRA_NEW_TEAM, true);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (newTeam) {
            ab.setTitle(R.string.new_team);
            team = new Team("", "");
        } else {
            ab.setTitle(R.string.team_detail);
            Long teamId = startingIntent.getLongExtra(EXTRA_TEAM_ID, 0);
            team = contentProvider.getTeam(teamId);
        }

        if (team != null) {
            final TeamDetailViewModel viewModel = new TeamDetailViewModel(this, team, newTeam, this);
            binding.setTeamDetailViewModel(viewModel);

            if (!newTeam) {
                final EditText teamNameEditText = (EditText) findViewById(R.id.edittext_team_name);
                final EditText teamDescriptionEditText = (EditText) findViewById(R.id.edittext_team_description);

                setEditTextListeners(teamNameEditText, viewModel, team);
                setEditTextListeners(teamDescriptionEditText, viewModel, team);

                updateMemberList(GlobalVariables.isOnline(this));

                final Button addMemberButton = (Button) findViewById(R.id.add_member_btn);
                addMemberButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = SelectUserActivity.createIntent(getApplicationContext(), team.getUsers());
                        startActivityForResult(intent, REQUEST_CODE_ADDMEMBER);
                    }
                });
            }
            handleOfflineMode();
        } else {
            finish();
        }
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

        if (GlobalVariables.isOnline(this) && !newTeam) {
            ab.setSubtitle(null);
            addMemberButton.setVisibility(View.VISIBLE);
            teamNameEditText.setEnabled(true);
            teamDescriptionEditText.setEnabled(true);
            teamNameEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_orange, 0);
            teamDescriptionEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_orange, 0);
            updateMemberList(true);
        } else if (!GlobalVariables.isOnline(this) && !newTeam){
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
                    new AlertDialog.Builder(TeamDetailActivity.this)
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (GlobalVariables.isOnline(this) && !newTeam) {
            getMenuInflater().inflate(R.menu.delete_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                new AlertDialog.Builder(TeamDetailActivity.this)
                        .setTitle(R.string.remove_team_dialog_title)
                        .setMessage(String.format(getResources().getString(R.string.remove_team_dialog_message), team.getName()))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                contentProvider.removeTeam(team);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create()
                        .show();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
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
