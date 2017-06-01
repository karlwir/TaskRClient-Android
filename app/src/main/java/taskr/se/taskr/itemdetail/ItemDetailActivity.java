package taskr.se.taskr.itemdetail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import taskr.se.taskr.R;
import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.teamdetailviews.TeamDetailEditFragment;

public class ItemDetailActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, ItemDetailActivity.class);
    }


    private Menu editMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new ItemDetailFragment();
            fragment.setArguments(bundle());
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
        handleOfflineMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleOfflineMode();
    }

    private void handleOfflineMode() {
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (GlobalVariables.isOnline(this)) {
            ab.setSubtitle(null);
        } else {
            ab.setSubtitle(R.string.offline_mode);
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (GlobalVariables.isOnline(this)) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_edit:
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = new ItemDetailEditFragment();
                fragment.setArguments(bundle());
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                item.setVisible(false);
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private Bundle bundle() {
        return getIntent().getExtras();
    }
}
