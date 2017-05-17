package taskr.se.taskr.repository;

import android.content.Context;
import android.os.AsyncTask;

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
import okio.BufferedSink;
import taskr.se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-15.
 *
 * This class is still quite experimental and far from done
 */

class WorkItemHttpClient extends BaseHttpClient<WorkItem> {

    private static final String BASE_URL = "http://kw-taskmanager-api.herokuapp.com/workitems/";

    public static synchronized WorkItemHttpClient getInstance(Context context) {
        return new WorkItemHttpClient();
    }

    public void getWorkItems(OnResultEventListener<List<WorkItem>> listener) {
        new GetTask(listener, BASE_URL).execute();
    }

    public void postWorkItem(WorkItem workItem, OnResultEventListener listner) {
        new PostTask(workItem, listner, BASE_URL).execute();
    }

    public void putWorkItem(WorkItem workItem) {
        String url = String.format("%s/%s", BASE_URL, workItem.getItemKey());
        new PutTask(workItem, url).execute();
    }

    public void deleteWorkItem(WorkItem workItem) {
        new DeleteTask(workItem).execute();
    }

    @Override
    protected List<WorkItem> deserializeResultList(String responseString) {
        Gson gson = new Gson();

        Type collectionType = new TypeToken<Collection<WorkItem>>(){}.getType();
        List<WorkItem> result = gson.fromJson(responseString, collectionType);

        return result;
    }

//    private static class GetTask extends AsyncTask<Void, Void, List<WorkItem>> {
//        private final OnResultEventListener<List<WorkItem>> listener;
//
//        private GetTask(OnResultEventListener<List<WorkItem>> listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        protected List<WorkItem> doInBackground(Void... voids) {
//            OkHttpClient client = new OkHttpClient();
//            String responseString = null;
//
//            Request request = new Request.Builder()
//                    .url(BASE_URL)
//                    .addHeader("api-key", "secretkey")
//                    .build();
//
//            try (Response response = client.newCall(request).execute()) {
//                responseString = response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            Gson gson = new Gson();
//
//            Type collectionType = new TypeToken<Collection<WorkItem>>(){}.getType();
//            List<WorkItem> workItems = gson.fromJson(responseString, collectionType);
//
//            return workItems;
//        }
//
//        @Override
//        protected void onPostExecute(List<WorkItem> result) {
//            listener.onResult(result);
//        }
//    }

//    private static class PutTask extends AsyncTask<Void, Void, Void> {
//        private WorkItem workItem;
//
//        public PutTask(WorkItem workItem) {
//            this.workItem = workItem;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            OkHttpClient client = new OkHttpClient();
//            Gson gson = new Gson();
//            String json = gson.toJson(workItem);
//            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
//            RequestBody body = RequestBody.create(mediaType, json);
//
//            Request request = new Request.Builder()
//                    .url(BASE_URL + "/" + workItem.getItemKey())
//                    .addHeader("api-key", "secretkey")
//                    .put(body)
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }

//    private static class PostTask extends AsyncTask<Void, Void, String> {
//        private WorkItem workItem;
//        private OnResultEventListener listener;
//        private String generatedKey;
//
//        public PostTask(WorkItem workItem, OnResultEventListener listener) {
//            this.workItem = workItem;
//            this.listener = listener;
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            OkHttpClient client = new OkHttpClient();
//            Gson gson = new Gson();
//            String json = gson.toJson(workItem);
//            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            RequestBody body = RequestBody.create(JSON, json);
//
//            Request request = new Request.Builder()
//                    .url(BASE_URL)
//                    .addHeader("api-key", "secretkey")
//                    .post(body)
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                generatedKey = response.header("generatedkey");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return generatedKey;
//        }
//
//        @Override
//        protected void onPostExecute(String generatedKey) {
//            listener.onResult(generatedKey);
//        }
//    }

    private static class DeleteTask extends AsyncTask<Void, Void, Void> {
        private WorkItem workItem;

        public DeleteTask(WorkItem workItem) {
            this.workItem = workItem;
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(BASE_URL + "/" + workItem.getItemKey())
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
}
