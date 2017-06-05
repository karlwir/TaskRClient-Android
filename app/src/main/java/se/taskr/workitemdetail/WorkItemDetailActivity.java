package se.taskr.workitemdetail;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
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
import se.taskr.databinding.ActivityWorkItemDetailBinding;
import se.taskr.global.GlobalVariables;
import se.taskr.model.User;
import se.taskr.model.WorkItem;
import se.taskr.repository.TaskRContentProvider;
import se.taskr.repository.TaskRContentProviderImpl;
import se.taskr.selectuser.SelectUserActivity;
import se.taskr.utils.UserListAdapter;

import static se.taskr.selectuser.SelectUserActivity.SELECTED_USER_ID;

public class WorkItemDetailActivity extends AppCompatActivity {

    private static final String EXTRA_WORK_ITEM_ID = "team_id";
    private static final String EXTRA_NEW_WORK_ITEM = "new_work_item";
    private static final int REQUEST_CODE_ASSIGNUSER = 1;
    private TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(this);
    private WorkItem workItem;
    private boolean newWorkItem;

    public static Intent createIntent(Context context, WorkItem workItem) {
        Intent intent = new Intent(context, WorkItemDetailActivity.class);
        intent.putExtra(EXTRA_WORK_ITEM_ID, workItem.getId());
        intent.putExtra(EXTRA_NEW_WORK_ITEM, false);
        return intent;
    }

    public static Intent createNewWorkItemIntent(Context context) {
        Intent intent = new Intent(context, WorkItemDetailActivity.class);
        intent.putExtra(EXTRA_NEW_WORK_ITEM, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityWorkItemDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_work_item_detail);

        Intent startingIntent = getIntent();
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        newWorkItem = startingIntent.getBooleanExtra(EXTRA_NEW_WORK_ITEM, true);
        if (newWorkItem) {
            ab.setTitle(R.string.new_work_item);
            workItem = new WorkItem("", "", "UNSTARTED");
        } else {
            ab.setTitle(R.string.work_item);
            Long workItemId = startingIntent.getLongExtra(EXTRA_WORK_ITEM_ID, 0);
            workItem = contentProvider.getWorkItem(workItemId);

        }
        if (workItem != null) {
            final WorkItemDetailViewModel viewModel = new WorkItemDetailViewModel(this, workItem, newWorkItem, this);
            binding.setWorkItemDetailViewModel(viewModel);

            if (!newWorkItem) {
                final EditText workItemTitleEditText = (EditText) findViewById(R.id.edittext_work_item_title);
                final EditText workItemDescriptionEditText = (EditText) findViewById(R.id.edittext_work_item_description);

                setEditTextListeners(workItemTitleEditText, viewModel, workItem);
                setEditTextListeners(workItemDescriptionEditText, viewModel, workItem);

                updateAssigneeList(GlobalVariables.isOnline(this));

                final Button addUserButton = (Button) findViewById(R.id.add_assignee);
                addUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = SelectUserActivity.createIntent(getApplicationContext(), workItem.getUsers());
                        startActivityForResult(intent, REQUEST_CODE_ASSIGNUSER);
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
        final EditText workItemTitleEditText = (EditText) findViewById(R.id.edittext_work_item_title);
        final EditText workItemDescriptionEditText = (EditText) findViewById(R.id.edittext_work_item_description);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        final Button addUserButton = (Button) findViewById(R.id.add_assignee);
        final AppCompatSpinner statusSpinner = (AppCompatSpinner) findViewById(R.id.work_item_status_select);

        if (GlobalVariables.isOnline(this) && !newWorkItem) {
            ab.setSubtitle(null);
            addUserButton.setEnabled(true);
            addUserButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            workItemTitleEditText.setEnabled(true);
            workItemDescriptionEditText.setEnabled(true);
            workItemTitleEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_orange, 0);
            workItemDescriptionEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_orange, 0);
            statusSpinner.setEnabled(true);
            updateAssigneeList(true);

        } else if (!GlobalVariables.isOnline(this) && !newWorkItem) {
            ab.setSubtitle(R.string.offline_mode);
            addUserButton.setEnabled(false);
            addUserButton.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            workItemTitleEditText.setEnabled(false);
            workItemDescriptionEditText.setEnabled(false);
            workItemTitleEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_gray, 0);
            workItemDescriptionEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit_pen_gray, 0);
            statusSpinner.setEnabled(false);
            updateAssigneeList(false);
        }
            invalidateOptionsMenu();
    }

    private void updateAssigneeList(final boolean clickable) {
        final ExpandableHeightListView workItemAssigneesListView = (ExpandableHeightListView) findViewById(R.id.work_item_assignees_listview);
        if (clickable) {
            final UserListAdapter adapter = new UserListAdapter(this, R.layout.user_list_item, workItem.getUsers(), R.drawable.ic_clear_orange);
            workItemAssigneesListView.setAdapter(adapter);
        } else {
            final UserListAdapter adapter = new UserListAdapter(this, R.layout.user_list_item, workItem.getUsers(), R.drawable.ic_clear_gray);
            workItemAssigneesListView.setAdapter(adapter);
        }
        workItemAssigneesListView.setExpanded(true);
        if (clickable) {
            workItemAssigneesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final User user = (User) workItemAssigneesListView.getItemAtPosition(position);
                    new AlertDialog.Builder(WorkItemDetailActivity.this)
                            .setTitle(R.string.remove_assignee_dialog_title)
                            .setMessage(String.format(getResources().getString(R.string.remove_assignee_dialog_message), user.getUsername(), workItem.getTitle()))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    contentProvider.unAssignWorkItem(workItem, user);
                                    workItem.removeUser(user);
                                    updateAssigneeList(clickable);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create()
                            .show();
                }
            });
        }
        workItemAssigneesListView.setEnabled(clickable);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == REQUEST_CODE_ASSIGNUSER) {
            if(resultCode == RESULT_OK) {
                Long id = (Long) intent.getExtras().get(SELECTED_USER_ID);
                User newAssignee = contentProvider.getUser(id);
                workItem.addUser(newAssignee);
                contentProvider.assignWorkItem(workItem, newAssignee);
                updateAssigneeList(GlobalVariables.isOnline(this));
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (GlobalVariables.isOnline(this) && !newWorkItem) {
            getMenuInflater().inflate(R.menu.delete_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_delete:
                new AlertDialog.Builder(WorkItemDetailActivity.this)
                        .setTitle(R.string.remove_work_item_dialog_title)
                        .setMessage(String.format(getResources().getString(R.string.remove_work_item_dialog_message), workItem.getTitle()))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                contentProvider.removeWorkItem(workItem);
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

    private void setEditTextListeners(EditText editText, final WorkItemDetailViewModel viewModel, final WorkItem workItem) {
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
                if (!hasFocus && !workItem.getTitle().equals(viewModel.title.get())) {
                    viewModel.save();
                } else if (!hasFocus && !workItem.getDescription().equals(viewModel.description.get())) {
                    viewModel.save();
                }
            }
        });
    }
}
