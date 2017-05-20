package taskr.se.taskr.teamdetailviews;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import taskr.se.taskr.R;

public class AddUserActivity extends AppCompatActivity {

    private Bundle bundle;

    public static Intent createIntent (Context context) {
        return new Intent(context, AddUserActivity.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);


        bundle = getIntent().getExtras();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_user_fragment_container);

        if(fragment == null){
            fragment = new AddUserFragment();
            if(bundle != null){fragment.setArguments(bundle);}
            fm.beginTransaction().add(R.id.add_user_fragment_container , fragment).commit();
        }
    }
}
