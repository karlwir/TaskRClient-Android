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
import taskr.se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-17.
 */

abstract class BaseHttpClient<T> {

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

            for (Object o : result) {
                Log.d("TAG", o.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<T> result) {
            listener.onResult(result);
        }
    }

    protected abstract List<T> deserializeResultList(String responseString);
}
