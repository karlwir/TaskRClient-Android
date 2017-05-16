package taskr.se.taskr.home.itemlistfragment;

import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 2017-05-13.
 */

public class ItemListPresenterImpl implements ItemListContract.Presenter {

    private List<WorkItem> items;
    private final TaskRContentProvider taskRContentProvider;

    public ItemListPresenterImpl(final ItemListFragment view, int position) {
        taskRContentProvider = TaskRContentProviderImpl.getInstance(view.getContext(), new RefreshItemsListener() {
            @Override
            public void refreshItems() {
                view.updateAdapter();

            }
        });
        setTabPosition(position);
    }

    @Override
    public List<WorkItem> getItems() {
        if(items != null) return new ArrayList<>(items);
        return new ArrayList<>();
    }

    @Override
    public void setTabPosition(int position) {
        switch (position) {
            case 0:
                items = taskRContentProvider.getUnstartedWorkItems();
                break;
            case 1:
                items = taskRContentProvider.getStartedWorkItems();
                break;
            case 2:
                items = taskRContentProvider.getDoneWorkItems();
                break;
            case 3:
                items = taskRContentProvider.getMyWorkItems();
                break;
            default:
                items = taskRContentProvider.getWorkItems();
                break;
        }
    }
}
