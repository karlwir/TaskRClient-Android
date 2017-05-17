package taskr.se.taskr.repository;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import taskr.se.taskr.model.WorkItem;

/**
 * Created by kawi01 on 2017-05-15.
 *
 * This class is still quite experimental and far from done
 */

class WorkItemHttpClient extends BaseHttpClient<WorkItem> {

    private static final String BASE_URL = "http://kw-taskmanager-api.herokuapp.com/workitems";

    static synchronized WorkItemHttpClient getInstance() {
        return new WorkItemHttpClient();
    }

    void getWorkItems(OnResultEventListener<List<WorkItem>> listener) {
        new GetTask(listener, BASE_URL).execute();
    }

    void postWorkItem(WorkItem workItem, OnResultEventListener listener) {
        new PostTask(workItem, listener, BASE_URL).execute();
    }

    void putWorkItem(WorkItem workItem) {
        String url = String.format("%s/%s", BASE_URL, workItem.getItemKey());
        new PutTask(workItem, url).execute();
    }

    void deleteWorkItem(WorkItem workItem) {
        String url = String.format("%s/%s", BASE_URL, workItem.getItemKey());
        new DeleteTask(workItem, url).execute();
    }

    @Override
    protected Type getCollectionType() {
        return new TypeToken<Collection<WorkItem>>(){}.getType();
    }

}
