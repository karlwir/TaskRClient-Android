package taskr.se.taskr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.home.HomeActivity;
import taskr.se.taskr.repository.OnResultEventListener;

public class LoginActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Log.d("TAG", String.valueOf(GlobalVariables.isOnline(this)));

        if (GlobalVariables.isOnline(this)) {

            final SignInButton button = (SignInButton) findViewById(R.id.sign_in_button);
            button.setVisibility(View.VISIBLE);
            button.setEnabled(false);
            final Intent intent = HomeActivity.createIntent(getApplicationContext(), new OnResultEventListener<Boolean>() {
                @Override
                public void onResult(Boolean result) {
                    if (result) {
                        button.setEnabled(true);
                    }
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
            final Button button = (Button) findViewById(R.id.offline_button);
            button.setVisibility(View.VISIBLE);

            final Intent intent = HomeActivity.createOfflineIntent(getApplicationContext());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intent);
                    finish();
                }
            });
        }


    }
}