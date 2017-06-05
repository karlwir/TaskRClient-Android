package se.taskr.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import se.taskr.R;
import se.taskr.global.GlobalVariables;
import se.taskr.home.HomeActivity;
import se.taskr.model.User;
import se.taskr.repository.OnResultEventListener;
import se.taskr.repository.TaskRContentProvider;
import se.taskr.repository.TaskRContentProviderImpl;

public class LoginActivity extends AppCompatActivity {


    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        prepareLoginScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareLoginScreen();
    }

    private void prepareLoginScreen() {

        final TaskRContentProvider provider = TaskRContentProviderImpl.getInstance(this);
        final SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.shared_prefs), MODE_PRIVATE);

        boolean autoLogin = preferences.getBoolean(getResources().getString(R.string.prefs_auto_login), false);

        final SignInButton googleButton = (SignInButton) findViewById(R.id.sign_in_button);
        final Button continueButton = (Button) findViewById(R.id.continue_button);
        final TextView offlineText = (TextView) findViewById(R.id.offline_text);
        final TextView changeUser = (TextView) findViewById(R.id.change_user);
        changeUser.setPaintFlags(changeUser.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        changeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables.loggedInUser = null;
                preferences
                        .edit()
                        .putLong(getResources().getString(R.string.prefs_last_user_id), -1L)
                        .apply();
                prepareLoginScreen();
            }
        });

        changeUser.setVisibility(View.INVISIBLE);
        offlineText.setVisibility(View.INVISIBLE);
        continueButton.setVisibility(View.INVISIBLE);
        googleButton.setVisibility(View.INVISIBLE);


        User lastLoggedInUser = provider.getUser(preferences.getLong(getResources().getString(R.string.prefs_last_user_id), -1));
        if (lastLoggedInUser != null) {
            GlobalVariables.loggedInUser = lastLoggedInUser;

            if (autoLogin) {
                final Intent intent = HomeActivity.createIntent(getApplicationContext());
                startActivity(intent);
                finish();
            }
        }

        if (GlobalVariables.isOnline(this)) {
            if (lastLoggedInUser != null) {
                final Intent intent = HomeActivity.createIntent(getApplicationContext());
                continueButton.setText(getResources().getString(R.string.continue_as) + " @" + GlobalVariables.loggedInUser.getUsername());
                continueButton.setVisibility(View.VISIBLE);

                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                        finish();
                    }
                });

                changeUser.setVisibility(View.VISIBLE);

            } else {
                googleButton.setVisibility(View.VISIBLE);
                changeUser.setVisibility(View.INVISIBLE);
                googleButton.setEnabled(false);
                final Intent intent = HomeActivity.createInitIntent(getApplicationContext(), new OnResultEventListener<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        if (result) {
                            googleButton.setEnabled(true);
                        }
                    }
                });
                googleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(intent);
                        finish();
                    }
                });
            }
        } else if (lastLoggedInUser != null)  {
            final Intent intent = HomeActivity.createIntent(getApplicationContext());

            continueButton.setText(getResources().getString(R.string.use_offline_mode) + " @" + GlobalVariables.loggedInUser.getUsername());
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(intent);
                    finish();
                }
            });
            continueButton.setVisibility(View.VISIBLE);
            offlineText.setVisibility(View.VISIBLE);
            changeUser.setVisibility(View.VISIBLE);

        } else {
            offlineText.setVisibility(View.VISIBLE);
        }
    }
}