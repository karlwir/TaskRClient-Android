package taskr.se.taskr.home;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import taskr.se.taskr.LoginActivity;
import taskr.se.taskr.R;
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

    public static Intent createIntent(Context context, final OnResultEventListener<Boolean> listener) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (listener != null) {
            final TaskRContentProvider provider = TaskRContentProviderImpl.getInstance(context);
            provider.initData(new OnResultEventListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    GlobalVariables.loggedInUser = provider.getUsers(false).get(1);
                    listener.onResult(result);
                }
            });
        }
        return intent;
    }

    public static Intent createOfflineIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        GlobalVariables.loggedInUser = new User("Offline", "User", "offlineUser");
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("TaskR");

        if (GlobalVariables.isOnline(this)) {
            fab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = AddWorkItemActivity.createIntent(getApplicationContext());
                    startActivity(intent);

                }
            });
        } else {
            fab.setVisibility(View.INVISIBLE);
            ab.setSubtitle(R.string.offline_mode);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_list_container);
        if (fragment == null) {
            fragment = new ItemListFragmentContainer();
            fm.beginTransaction().add(R.id.fragment_list_container, fragment)
                    .commit();
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
                intent = LoginActivity.createIntent(getApplicationContext());
                startActivity(intent);
                finish();
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

        if (GlobalVariables.isOnline(this)) {
            String logOutTitle = getResources().getString(R.string.log_out) + " @" + GlobalVariables.loggedInUser.getUsername();
            MenuItem item = menu.findItem(R.id.sign_out);
            item.setTitle(logOutTitle);
        } else {
            MenuItem item = menu.findItem(R.id.open_team_detail);
            item.setVisible(false);
        }

        return true;
    }
}
