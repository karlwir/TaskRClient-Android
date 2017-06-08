package se.taskr.createaccount;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import se.taskr.R;
import se.taskr.databinding.ActivityCreateAccountBinding;
import se.taskr.model.User;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String NEW_ACCOUNT_FIRSTNAME = "new_account_firstname";
    private static final String NEW_ACCOUNT_LASTNAME = "new_account_lastname";
    private static final String NEW_ACCOUNT_EMAIL = "new_account_email";
    public static final String CREATED_ACCOUNT_ITEMKEY = "created_account_itemkey";

    public static Intent createIntentWIthGoogleAccount(Context context, GoogleSignInAccount acct) {
        Intent intent = new Intent(context, CreateAccountActivity.class);
        intent.putExtra(NEW_ACCOUNT_EMAIL, acct.getEmail());
        intent.putExtra(NEW_ACCOUNT_FIRSTNAME, acct.getGivenName());
        intent.putExtra(NEW_ACCOUNT_LASTNAME, acct.getFamilyName());
        Log.d("ACCDATA: ", acct.getEmail() + " : " + acct.getGivenName() + " : " + acct.getFamilyName());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.create_account);


        Intent startingIntent = getIntent();
        String firstname = startingIntent.getStringExtra(NEW_ACCOUNT_FIRSTNAME);
        String lastname = startingIntent.getStringExtra(NEW_ACCOUNT_LASTNAME);
        String email = startingIntent.getStringExtra(NEW_ACCOUNT_EMAIL);

        User newUser = new User(firstname, lastname, "", email);

        ActivityCreateAccountBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account);

        final CreateAccountViewModel viewModel = new CreateAccountViewModel(this, newUser, this);
        binding.setCreateAccountDetailViewModel(viewModel);
    }
}
