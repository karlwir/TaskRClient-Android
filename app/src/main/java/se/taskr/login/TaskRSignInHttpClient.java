package se.taskr.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.taskr.model.User;
import se.taskr.repository.OnResultEventListener;

/**
 * Created by kawi01 on 2017-06-06.
 */

public class TaskRSignInHttpClient {
    protected static final String SIGNIN_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/signin";
    protected MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    protected Context context;
    private static TaskRSignInHttpClient instance;


    public static synchronized TaskRSignInHttpClient getInstance(Context context) {
        if(instance == null) {
            instance = new TaskRSignInHttpClient(context);
        }
        return instance;
    }

    TaskRSignInHttpClient(Context context) {
        this.context = context;
    }

    void signInByEmail(String email, OnResultEventListener<String> listener) {
        String url = String.format("%s/google-signin", SIGNIN_BASE_URL);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);

        new PostTask(jsonObject, listener, url).execute();
    }

    private class PostTask extends AsyncTask<Void, Void, String> {
        private final JsonObject jsonObject;
        private final OnResultEventListener listener;
        private final String url;
        private Response response = null;
        private String userItemdKey;

        public PostTask(JsonObject jsonObject, OnResultEventListener listener, String url) {
            this.jsonObject = jsonObject;
            this.listener = listener;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            String json = jsonObject.toString();

            RequestBody body = RequestBody.create(mediaType, json);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("api-key", "secretkey")
                    .post(body)
                    .build();

            try {
                response = client.newCall(request).execute();
                userItemdKey = response.header("useritemkey");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return userItemdKey;
        }

        @Override
        protected void onPostExecute(String userItemdKey) {
            listener.onResult(userItemdKey);
        }
    }

}
