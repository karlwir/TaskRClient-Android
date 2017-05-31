package taskr.se.taskr.repository;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.model.BaseEntity;
import taskr.se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-17.
 */

abstract class BaseHttpClient<T extends BaseEntity> {

    protected static final String WORKITEM_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/workitems";
    protected static final String USER_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/users";
    protected static final String TEAM_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/teams";

    protected MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    protected class GetTask extends AsyncTask<Void, Void, List<T>> {
        private final OnResultEventListener<List<T>> listener;
        private final String url;

        protected GetTask(OnResultEventListener<List<T>> listener, String url) {
            this.listener = listener;
            this.url = url;
        }

        @Override
        protected List<T> doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();
            String responseString = null;

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("api-key", "secretkey")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                responseString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<T> result = deserializeResultList(responseString);

            return result;
        }

        @Override
        protected void onPostExecute(List<T> result) {
            listener.onResult(result);
        }
    }

    protected class PutTask extends AsyncTask<Void, Void, Void> {
        private final T entity;
        private final String url;

        public PutTask(T entity, String url) {
            this.entity = entity;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            String json = serializeEnity(entity);

            RequestBody body = RequestBody.create(mediaType, json);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("api-key", "secretkey")
                    .put(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    protected class PostTask extends AsyncTask<Void, Void, String> {
        private final T entity;
        private final OnResultEventListener listener;
        private final String url;
        private String generatedKey;

        public PostTask(T entity, OnResultEventListener listener, String url) {
            this.entity = entity;
            this.listener = listener;
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            String json = serializeEnity(entity);

            RequestBody body = RequestBody.create(mediaType, json);

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("api-key", "secretkey")
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                generatedKey = response.header("generatedkey");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return generatedKey;
        }

        @Override
        protected void onPostExecute(String generatedKey) {
            if(generatedKey != null) {
                listener.onResult(generatedKey);
            }
        }
    }

    protected class DeleteTask extends AsyncTask<Void, Void, Void> {
        private final String url;

        public DeleteTask(String url) {
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("api-key", "secretkey")
                    .delete()
                    .build();

            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private String serializeEnity(T entity) {
        Gson gson = new Gson();
        String json = gson.toJson(entity);

        return json;
    };


    private List<T> deserializeResultList(String responseString) {
        Gson gson = new Gson();
        List<T> resultList = gson.fromJson(responseString, getCollectionType());

        return resultList;
    };

    protected abstract Type getCollectionType();
}
