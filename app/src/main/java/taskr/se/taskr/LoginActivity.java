package taskr.se.taskr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.common.SignInButton;

import taskr.se.taskr.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        SignInButton button = (SignInButton) findViewById(R.id.sign_in_button);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = HomeActivity.createIntent(getApplicationContext());
                startActivity(intent);
                finish();
            }
        });
    }
}