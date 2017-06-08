package se.taskr.createaccount;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.widget.Toast;

import se.taskr.R;
import se.taskr.model.User;
import se.taskr.repository.OnResultEventListener;
import se.taskr.repository.TaskRContentProvider;
import se.taskr.repository.TaskRContentProviderImpl;

import static se.taskr.createaccount.CreateAccountActivity.CREATED_ACCOUNT_ITEMKEY;

/**
 * Created by kawi01 on 2017-06-08.
 */

public class CreateAccountViewModel {
    public ObservableField<String> firstname = new ObservableField<>();
    public ObservableField<String> lastname = new ObservableField<>();
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> firstNameError = new ObservableField<>();
    public ObservableField<String> lastNameError = new ObservableField<>();

    private final CreateAccountActivity activity;
    private Context context;
    private TaskRContentProvider contentProvider;
    private User user;

    public CreateAccountViewModel(Context context, User user, CreateAccountActivity activity) {
        contentProvider = TaskRContentProviderImpl.getInstance(context);
        firstname.set(user.getFirstname());
        lastname.set(user.getLastname());
        username.set(user.getUsername());
        email.set(user.getEmail());

        this.context = context;
        this.user = user;
        this.activity = activity;
    }

    public void save() {
        if (firstname.get().length() < 2) {
            firstNameError.set("Firstname error");
        }
        else if (lastname.get().length() < 2) {
            lastNameError.set("Lastname error");
        } else {
            user.setFirstName(firstname.get());
            user.setLastName(lastname.get());
            user.setUsername(username.get());

            contentProvider.createUserAccount(user, new OnResultEventListener<String>() {
                @Override
                public void onResult(String result) {
                    Intent returnIntent = new Intent();
                    if (result != null) {
                        returnIntent.putExtra(CREATED_ACCOUNT_ITEMKEY, result);
                        activity.setResult(Activity.RESULT_OK, returnIntent);
                        activity.finish();
                    } else {
                        activity.setResult(Activity.RESULT_CANCELED, returnIntent);
                        activity.finish();
                    }
                }
            });
        }
    }
}
