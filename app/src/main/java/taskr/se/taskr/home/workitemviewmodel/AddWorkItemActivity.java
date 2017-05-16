package taskr.se.taskr.home.workitemviewmodel;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import taskr.se.taskr.R;

public class AddWorkItemActivity extends AppCompatActivity {

    public static Intent createIntent(Context context){
        return new Intent(context, AddWorkItemActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work_item);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_workitem_fragment_container);
        if(fragment == null){
            fragment = new AddWorkItemFragment();
            fm.beginTransaction().add(R.id.add_workitem_fragment_container, fragment)
                    .commit();
        }


    }
}
