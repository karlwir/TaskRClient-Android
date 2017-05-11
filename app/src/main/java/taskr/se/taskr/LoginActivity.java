package taskr.se.taskr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import taskr.se.taskr.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startActivity(HomeActivity.createIntent(this));
    }
}
