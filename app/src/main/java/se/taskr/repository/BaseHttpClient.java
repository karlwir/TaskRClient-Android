package se.taskr.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import se.taskr.R;
import se.taskr.model.BaseEntity;

import static okhttp3.Protocol.HTTP_1_1;

/**
 * Created by kawi01 on 2017-05-17.
 */

abstract class BaseHttpClient<T extends BaseEntity> {

    protected static final String WORKITEM_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/workitems";
    protected static final String USER_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/users";
    protected static final String TEAM_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/teams";
    protected MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    protected Context context;

    BaseHttpClient(Context context) {
        this.context = context;
    }

    private static final String TAG = "BaseHttpClient";

    protected class GetTask extends AsyncTask<Void, Void, List<T>> {

        private final OnResultEventListener<List<T>> listener;
        private final String url;
        private Response response = null;
        private List<T> result = null;

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

            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    responseString = response.body().string();
                    result = deserializeResultList(responseString);
                }
            } catch (IOException e) {
                response = noResponse(request);
            }

            return result;
        }

        @Override
        protected void onPostExecute(List<T> result) {
            if (response != null && !response.isSuccessful()) {
                handleResponseError(response);
            }
            listener.onResult(result);
        }
    }

    protected class PutTask extends AsyncTask<Void, Void, Void> {
        private final T entity;
        private final String url;
        private Response response;
        private final OnResultEventListener listener;
        public PutTask(T entity, String url, OnResultEventListener listener) {
            this.entity = entity;
            this.url = url;
            this.listener = listener;
        }

        public PutTask(T entity, String url) {
            this(entity,url, null);
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
                response = client.newCall(request).execute();
            } catch (IOException e) {
                response = noResponse(request);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (response != null && !response.isSuccessful()) {
                handleResponseError(response);
            }
            if (listener != null) {
                listener.onResult(response);
            }
        }
    }

    protected class PostTask extends AsyncTask<Void, Void, String> {
        private final T entity;
        private final OnResultEventListener listener;
        private final String url;
        private Response response = null;
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
                response = client.newCall(request).execute();
                generatedKey = response.header("generatedkey");
            } catch (IOException e) {
                response = noResponse(request);
            }
            return generatedKey;
        }

        @Override
        protected void onPostExecute(String generatedKey) {
            if (response != null && !response.isSuccessful()) {
                handleResponseError(response);
            }
            if(generatedKey != null) {
                listener.onResult(generatedKey);
            }
        }
    }

    protected class DeleteTask extends AsyncTask<Void, Void, Void> {
        private final String url;
        private Response response = null;
        private final OnResultEventListener listener;

        public DeleteTask(String url, OnResultEventListener listener) {
            this.url = url;
            this.listener = listener;
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
                response = client.newCall(request).execute();
            } catch (IOException e) {
                response = noResponse(request);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (response != null && !response.isSuccessful()) {
                handleResponseError(response);
            }
            if (listener != null) {
                listener.onResult(response);
            }
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

    private Response noResponse(Request request) {
        return new Response.Builder().code(0).request(request).protocol(HTTP_1_1).build();
    }

    private void handleResponseError(Response response) {
        int responseCode = response.code();
        if (responseCode == 0) {
            toastResponseError(context.getResources().getString(R.string.connection_error_no_response));
        }
        else if (responseCode == 400 || responseCode == 405) {
            toastResponseError(context.getResources().getString(R.string.connection_error_bad_request));
        }
        else if (responseCode == 401 || responseCode == 403) {
            toastResponseError(context.getResources().getString(R.string.connection_error_unauthorized));
        }
        else if (responseCode == 404) {
            toastResponseError(context.getResources().getString(R.string.connection_error_not_found));
        }
        else if (responseCode == 500) {
            toastResponseError(context.getResources().getString(R.string.connection_error_server_error));
        }
    }

    private void toastResponseError(String message) {
        Log.e(TAG, message);
    }

    protected abstract Type getCollectionType();
}
