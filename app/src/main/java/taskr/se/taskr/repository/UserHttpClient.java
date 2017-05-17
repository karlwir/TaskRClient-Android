package taskr.se.taskr.repository;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import taskr.se.taskr.model.User;

/**
 * Created by kawi01 on 2017-05-17.
 */

class UserHttpClient extends BaseHttpClient<User> {

    private static final String BASE_URL = "http://kw-taskmanager-api.herokuapp.com/users";

    static synchronized UserHttpClient getInstance() {
        return new UserHttpClient();
    }

    void getUsers(OnResultEventListener<List<User>> listener) {
        new GetTask(listener, BASE_URL).execute();
    }

    void postUser(User user, OnResultEventListener listener) {
        new PostTask(user, listener, BASE_URL).execute();
    }

    void putUser(User user) {
        String url = String.format("%s/%s", BASE_URL, user.getItemKey());
        new PutTask(user, url).execute();
    }

    void deleteUser(User user) {
        String url = String.format("%s/%s", BASE_URL, user.getItemKey());
        new DeleteTask(user, url).execute();
    }

    @Override
    protected Type getCollectionType() {
        return new TypeToken<Collection<User>>(){}.getType();
    }

}
