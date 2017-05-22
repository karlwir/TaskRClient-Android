package taskr.se.taskr.itemdetail;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import taskr.se.taskr.R;

public class ItemDetailActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, ItemDetailActivity.class);
    }

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

    }

    private Bundle bundle() {
        return getIntent().getExtras();
    }
}
