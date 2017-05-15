package taskr.se.taskr.home.itemlistfragment;

import android.content.Context;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.WorkItemRepository;
import taskr.se.taskr.repository.WorkItemRepositorySql;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 2017-05-13.
 */

public class ItemListPresenterImpl implements ItemListContract.Presenter {

    private List<WorkItem> items;

    public ItemListPresenterImpl(Context context) {
        WorkItemRepository workItemRepository = WorkItemRepositorySql.getInstance(context);
        items = workItemRepository.getWorkItems();
    }

    @Override
    public List<WorkItem> getItems() {
        return new ArrayList<>(items);
    }
}
