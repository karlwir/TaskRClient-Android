package se.taskr.repository;

import android.content.Context;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import se.taskr.model.User;
import se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-15.
 *
 */

class WorkItemHttpClient extends BaseHttpClient<WorkItem> {

    static synchronized WorkItemHttpClient getInstance(Context context) {
        return new WorkItemHttpClient(context);
    }

    private WorkItemHttpClient(Context context) {
        super(context);
    }

    void getWorkItems(OnResultEventListener<List<WorkItem>> listener) {
        new GetTask(listener, WORKITEM_BASE_URL).execute();
    }

    void postWorkItem(WorkItem workItem, OnResultEventListener<String> listener) {
        new PostTask(workItem, listener, WORKITEM_BASE_URL).execute();
    }

    void putWorkItem(WorkItem workItem) {
        String url = String.format("%s/%s", WORKITEM_BASE_URL, workItem.getItemKey());
        new PutTask(workItem, url).execute();
    }

    void deleteWorkItem(WorkItem workItem, OnResultEventListener listener) {
        String url = String.format("%s/%s", WORKITEM_BASE_URL, workItem.getItemKey());
        new DeleteTask(url, listener).execute();
    }

    void assignWorkItem(WorkItem workItem, User user, OnResultEventListener listener) {
        String url = String.format("%s/%s/workitems/%s", USER_BASE_URL, user.getItemKey(), workItem.getItemKey());
        new PutTask(null, url, listener).execute();
    }

    void unAssignWorkItem(WorkItem workItem, User user, OnResultEventListener listener) {
        String url = String.format("%s/%s/workitems/%s", USER_BASE_URL, user.getItemKey(), workItem.getItemKey());
        new DeleteTask(url, listener).execute();
    }

    @Override
    protected Type getCollectionType() {
        return new TypeToken<Collection<WorkItem>>(){}.getType();
    }
}
