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
    private final WorkItemRepository workItemRepository;

    public ItemListPresenterImpl(Context context, int position) {
        workItemRepository = WorkItemRepositorySql.getInstance(context);
        onPositionChange(position);
    }

    @Override
    public List<WorkItem> getItems() {
        if(items != null) return new ArrayList<>(items);
        return new ArrayList<>();
    }

    @Override
    public void onPositionChange(int position) {
        switch (position) {
            case 0:
                items = workItemRepository.getUnstartedWorkItems();
                break;
            case 1:
                items = workItemRepository.getStartedWorkItems();
                break;
            case 2:
                items = workItemRepository.getDoneWorkItems();
                break;
            case 3:
                items = workItemRepository.getMyWorkItems();
                break;
            default:
                items = workItemRepository.getWorkItems();
                break;
        }
    }
}
