package se.taskr.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import se.taskr.R;
import se.taskr.createaccount.CreateAccountActivity;
import se.taskr.global.GlobalVariables;
import se.taskr.home.HomeActivity;
import se.taskr.model.User;
import se.taskr.repository.OnResultEventListener;
import se.taskr.repository.TaskRContentProvider;
import se.taskr.repository.TaskRContentProviderImpl;

import static se.taskr.createaccount.CreateAccountActivity.CREATED_ACCOUNT_ITEMKEY;

public class LoginActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 1;
    private static final int RC_CREATE_ACCOUNT = 2;
    private static GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

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
        killGoogleApiClient();
        prepareLoginScreen();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            createGoogleApiClient();
        }
    }

    private void prepareLoginScreen() {
        hideProgressDialog();

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
                googleSignOut();
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
                changeUser.setVisibility(View.VISIBLE);

                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDialog();
                        startActivity(intent);
                        finish();
                    }
                });

            } else {
                createGoogleApiClient();
                googleButton.setVisibility(View.VISIBLE);
                changeUser.setVisibility(View.INVISIBLE);
                googleButton.setEnabled(true);
                googleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showProgressDialog();
                        googleSignIn();
                    }
                });
            }
        } else if (lastLoggedInUser != null)  {
            final Intent intent = HomeActivity.createIntent(getApplicationContext());

            continueButton.setText(getResources().getString(R.string.use_offline_mode) + " @" + GlobalVariables.loggedInUser.getUsername());
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showProgressDialog();
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

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                prepareLoginScreen();
                killGoogleApiClient();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } else {
                prepareLoginScreen();
            }
        } else if (requestCode == RC_CREATE_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                final TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(this);
                final String newAccountItemkey = data.getStringExtra(CREATED_ACCOUNT_ITEMKEY);
                contentProvider.initData(new OnResultEventListener() {
                    @Override
                    public void onResult(Object result) {
                        showProgressDialog();
                        User user = contentProvider.getUserByItemKey(newAccountItemkey);
                        GlobalVariables.loggedInUser = user;
                        Intent homeIntent = HomeActivity.createIntent(getApplicationContext());
                        startActivity(homeIntent);
                        finish();
                    }
                });
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        killGoogleApiClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            createGoogleApiClient();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        killGoogleApiClient();
    }

    private void killGoogleApiClient() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    private void createGoogleApiClient() {
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, null)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();
            String signInEmail = acct.getEmail();
            final Intent homeIntent = HomeActivity.createIntent(getApplicationContext());

            TaskRSignInHttpClient signInClient = TaskRSignInHttpClient.getInstance(getApplicationContext());
            signInClient.signInByEmail(signInEmail, new OnResultEventListener<String>() {
                @Override
                public void onResult(final String userItemKey) {
                    if (userItemKey != null) {
                        final TaskRContentProvider contentProvider = TaskRContentProviderImpl.getInstance(getApplicationContext());
                        contentProvider.initData(new OnResultEventListener() {
                            @Override
                            public void onResult(Object result) {
                                User user = contentProvider.getUserByItemKey(userItemKey);
                                GlobalVariables.loggedInUser = user;
                                killGoogleApiClient();
                                startActivity(homeIntent);
                                finish();
                            }
                        });

                    } else {
                        killGoogleApiClient();
                        final Intent createAccountIntent = CreateAccountActivity.createIntentWIthGoogleAccount(getApplicationContext(), acct);
                        startActivityForResult(createAccountIntent, RC_CREATE_ACCOUNT);
                    }

                }
            });

        } else {
            // TODO: Signed out, show unauthenticated UI.

        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.singning_in));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

}