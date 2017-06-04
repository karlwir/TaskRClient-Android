package taskr.se.taskr.selectuser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;

import java.util.List;

import taskr.se.taskr.R;
import taskr.se.taskr.utils.UserListAdapter;
import taskr.se.taskr.model.User;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

public class SelectUserActivity extends AppCompatActivity {

    public static final String SELECTED_USER_ID = "selected_user_id";
    private static final String EXTRA_IDS_TO_DISABLE = "ids_to_disable";
    private final TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(this);

    public static Intent createIntent(Context context, List<User> usersToDisable) {
        Intent intent = new Intent(context, SelectUserActivity.class);

        int length = usersToDisable.size();
        long[] longIdArray = new long[length];
        for (int i = 0; i < length; i++) {
            longIdArray[i] = ((Number) (usersToDisable.get(i).getId())).longValue();
        }

        intent.putExtra(EXTRA_IDS_TO_DISABLE, longIdArray);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.select_user);

        Intent startingIntent = getIntent();
        long[] idsToDisableArray = startingIntent.getLongArrayExtra(EXTRA_IDS_TO_DISABLE);

        List<User> availableUsers = contentProvider.getUsers(false);

        if (idsToDisableArray != null) {
            for (long id : idsToDisableArray) {
                User user = contentProvider.getUser(id);
                availableUsers.remove(user);
            }
        }

        final ExpandableHeightListView availableUsersListView = (ExpandableHeightListView) findViewById(R.id.select_user_listview);
        final UserListAdapter adapter = new UserListAdapter(this, R.layout.user_list_item, availableUsers, R.drawable.ic_add_orange);

        availableUsersListView.setAdapter(adapter);
        availableUsersListView.setExpanded(true);
        availableUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) availableUsersListView.getItemAtPosition(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra(SELECTED_USER_ID, user.getId());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }
}
