package taskr.se.taskr.datadebug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

import taskr.se.taskr.R;

public class DataDebugActivity extends AppCompatActivity {

    SharedPreferences preferences;

    public static Intent createIntent(Context context) {
        return new Intent(context, DataDebugActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_debug);
        preferences = getSharedPreferences(getResources().getString(R.string.shared_prefs), MODE_PRIVATE);

        Long workItemTimestamp = preferences.getLong(getResources().getString(R.string.prefs_last_workitem_sync_timestamp), 0);
        Long usersTimestamp = preferences.getLong(getResources().getString(R.string.prefs_last_user_sync_timestamp), 0);
        Long teamsTimestamp = preferences.getLong(getResources().getString(R.string.prefs_last_team_sync_timestamp), 0);

        TextView debug_workitems_timestamp = (TextView) findViewById(R.id.debug_workitems_timestamp);
        TextView debug_users_timestamp = (TextView) findViewById(R.id.debug_users_timestamp);
        TextView debug_teams_timestamp = (TextView) findViewById(R.id.debug_teams_timestamp);

//        debug_workitems_timestamp.setText(workItemTimestamp.toString());
//        debug_users_timestamp.setText(usersTimestamp.toString());
//        debug_teams_timestamp.setText(teamsTimestamp.toString());

        debug_workitems_timestamp.setText(new Date(workItemTimestamp).toString());
        debug_users_timestamp.setText(new Date(usersTimestamp).toString());
        debug_teams_timestamp.setText(new Date(teamsTimestamp).toString());
    }
}
