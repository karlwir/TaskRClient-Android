package taskr.se.taskr.home;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.List;
import java.util.Random;

import taskr.se.taskr.BuildConfig;
import taskr.se.taskr.LoginActivity;
import taskr.se.taskr.R;
import taskr.se.taskr.datadebug.DataDebugActivity;
import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.home.itemlistfragment.ItemListFragment;
import taskr.se.taskr.home.workitemviewmodel.AddWorkItemActivity;
import taskr.se.taskr.model.User;
import taskr.se.taskr.repository.OnResultEventListener;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;
import taskr.se.taskr.teamdetailviews.TeamDetailActivity;

public class HomeActivity extends AppCompatActivity {

    private ItemListFragment searchResultFragment;


    public static Intent createInitIntent(Context context, final OnResultEventListener<Boolean> listener) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (listener != null) {
            final TaskRContentProvider provider = TaskRContentProviderImpl.getInstance(context);
            provider.initData(new OnResultEventListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    List<User> users = provider.getUsers(false);
                    Random random = new Random();
                    int randomIndex = random.nextInt(users.size() - 1 + 1);
                    GlobalVariables.loggedInUser = users.get(randomIndex);
                    listener.onResult(result);
                }
            });
        }
        return intent;
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.shared_prefs), MODE_PRIVATE);
        preferences
                .edit()
                .putLong(getResources().getString(R.string.prefs_last_user_id), GlobalVariables.loggedInUser.getId())
                .putBoolean(getResources().getString(R.string.prefs_auto_login), true)
                .apply();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.title_activity_home);

        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddWorkItemActivity.createIntent(getApplicationContext());
                startActivity(intent);

            }
        });

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_list_container);
        if (fragment == null) {
            fragment = new ItemListFragmentContainer();
            fm.beginTransaction().add(R.id.fragment_list_container, fragment)
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
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (GlobalVariables.isOnline(this)) {
            fab.setVisibility(View.VISIBLE);
            ab.setSubtitle(null);
        } else {
            fab.setVisibility(View.INVISIBLE);
            ab.setSubtitle(R.string.offline_mode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Intent intent;

        switch (menuItem.getItemId()) {
            case R.id.open_team_detail:
                intent = TeamDetailActivity.createIntent(getApplicationContext());
                startActivity(intent);
                break;
            case R.id.sign_out:
                final SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.shared_prefs), MODE_PRIVATE);
                preferences
                        .edit()
                        .putBoolean(getResources().getString(R.string.prefs_auto_login), false)
                        .apply();

                intent = LoginActivity.createIntent(getApplicationContext());
                startActivity(intent);
                finish();
                break;
            case R.id.data_debug:
                intent = DataDebugActivity.createIntent(getApplicationContext());
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = new ItemListFragmentContainer();
                fm.beginTransaction().replace(R.id.fragment_list_container, fragment).commit();
                return true;
            }
        });

        searchView.setOnSearchClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                searchResultFragment = ItemListFragment.newInstance();
                fm.beginTransaction().replace(R.id.fragment_list_container, searchResultFragment)
                        .commit();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && searchResultFragment != null)
                    searchResultFragment.onEditSearchInput(newText);
                return true;
            }
        });

        String logOutTitle = getResources().getString(R.string.log_out) + " @" + GlobalVariables.loggedInUser.getUsername();
        MenuItem item = menu.findItem(R.id.sign_out);
        item.setTitle(logOutTitle);

        if (!GlobalVariables.loggedInUser.hasTeam()) {
            MenuItem openTeam = menu.findItem(R.id.open_team_detail);
            openTeam.setTitle(R.string.has_no_team);
            openTeam.setEnabled(false);
        }

        if (BuildConfig.DEBUG) {
            MenuItem dataDebug = menu.findItem(R.id.data_debug);
            dataDebug.setVisible(true);
        }

        return true;
    }
}
