package se.taskr.repository;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import se.taskr.model.User;

/**
 * Created by kawi01 on 2017-05-17.
 */

class UserHttpClient extends BaseHttpClient<User> {

    static synchronized UserHttpClient getInstance(Context context) {
        return new UserHttpClient(context);
    }

    private UserHttpClient(Context context) {
        super(context);
    }

    void getUsers(OnResultEventListener<List<User>> listener) {
        new GetTask(listener, USER_BASE_URL).execute();
    }

    void postUser(User user, OnResultEventListener<String> listener) {
        new PostTask(user, listener, USER_BASE_URL).execute();
    }

    void putUser(User user) {
        String url = String.format("%s/%s", USER_BASE_URL, user.getItemKey());
        new PutTask(user, url).execute();
    }

    void deleteUser(User user) {
        String url = String.format("%s/%s", USER_BASE_URL, user.getItemKey());
        new DeleteTask(url, null).execute();
    }

    @Override
    protected Type getCollectionType() {
        return new TypeToken<Collection<User>>(){}.getType();
    }

}
