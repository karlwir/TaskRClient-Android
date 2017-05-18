package taskr.se.taskr.home;

import android.content.Context;
import android.content.Intent;
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
import taskr.se.taskr.R;
import taskr.se.taskr.home.itemlistfragment.ItemListFragment;
import taskr.se.taskr.home.workitemviewmodel.AddWorkItemActivity;
import taskr.se.taskr.teamdetail.TeamDetailActivity;

public class HomeActivity extends AppCompatActivity {

    private ItemListFragment searchResultFragment;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AddWorkItemActivity.createIntent(getApplicationContext());
                startActivity(intent);
                finish();

            }
        });

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_list_container);
        if(fragment == null){
            fragment = new ItemListFragmentContainer();
            fm.beginTransaction().add(R.id.fragment_list_container,fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_options);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
               Intent intent =  TeamDetailActivity.createIntent(getApplicationContext());
                startActivity(intent);
                finish();
                return false;
            }
        });

        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d("Förstorad", "onMenuItemActionExpand: ");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d("Kollapsad", "onMenuItemActionCollapse: ");
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
                Log.d("OnQueryTextChange", "onQueryTextChange: ");
                if(newText != null && searchResultFragment != null) searchResultFragment.onEditSearchInput(newText);
                return true;
            }
        });

        return true;
    }
}
