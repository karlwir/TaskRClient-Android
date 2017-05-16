package taskr.se.taskr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import taskr.se.taskr.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }


    public void loginButton(View view) {
        startActivity(HomeActivity.createIntent(this));
    }
}

