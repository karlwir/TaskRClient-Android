package taskr.se.taskr.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import taskr.se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-15.
 *
 * This class is still quite experimental and far from done
 */

class WorkItemHttpClient {

    private static final String WORKITEMS_BASE_URL = "http://kw-taskmanager-api.herokuapp.com/workitems";

    public static synchronized WorkItemHttpClient getInstance(Context context) {
        return new WorkItemHttpClient();
    }

    public void getWorkItems(OnResultEventListener<List<WorkItem>> listener) {
        new GetTask<List<WorkItem>>(listener).execute();
    }

    private static class GetTask<T> extends AsyncTask<Void, Void, T> {
        private final OnResultEventListener<T> listener;

        private GetTask(OnResultEventListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected T doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            String responseString = null;

            Request request = new Request.Builder()
                    .url(WORKITEMS_BASE_URL)
                    .addHeader("api-key", "secretkey")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                responseString = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();

            Type collectionType = new TypeToken<Collection<WorkItem>>(){}.getType();
            T workItems = gson.fromJson(responseString, collectionType);

            return workItems;
        }

        @Override
        protected void onPostExecute(T result) {
            listener.onResult(result);
        }
    }
}
