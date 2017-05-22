package taskr.se.taskr.teamdetailviews;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import taskr.se.taskr.R;

public class AddUserActivity extends AppCompatActivity {

    private Bundle bundle;
    private AddUserFragment fragment;

    public static Intent createIntent(Context context) {
        return new Intent(context, AddUserActivity.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);


        bundle = getIntent().getExtras();

        FragmentManager fm = getSupportFragmentManager();
        fragment = (AddUserFragment) fm.findFragmentById(R.id.add_user_fragment_container);

        if (fragment == null) {
            fragment = new AddUserFragment();
            if (bundle != null) {
                fragment.setArguments(bundle);
            }
            fm.beginTransaction().add(R.id.add_user_fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.open_team_detail);
        menuItem.setVisible(false);
        menuItem = menu.findItem(R.id.sign_out);
        menuItem.setVisible(false);

        final MenuItem searchMenuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
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
                fragment.onEditSearchInput(newText);
                return true;
            }
        });

        return true;
    }
}
