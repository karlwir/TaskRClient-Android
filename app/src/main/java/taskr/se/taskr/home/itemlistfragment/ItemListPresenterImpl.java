package taskr.se.taskr.home.itemlistfragment;

import taskr.se.taskr.home.ItemListFragmentContainer;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 2017-05-13.
 */

public class ItemListPresenterImpl implements ItemListContract.Presenter {

    private List<WorkItem> items;
    private final TaskRContentProviderImpl taskRContentProvider;
    private final ItemListFragment view;
    private final int position;

    public ItemListPresenterImpl(final ItemListFragment view, final int position) {
        taskRContentProvider = TaskRContentProviderImpl.getInstance(view.getContext());
        this.view = view;
        this.position = position;
        setTabPosition(true);
        taskRContentProvider.registerObserver(this);
    }

    @Override
    public List<WorkItem> getItems() {
        if(items != null) return new ArrayList<>(items);
        return new ArrayList<>();
    }

    @Override
    public void setTabPosition(boolean notifyObservers) {
        switch (position) {
            case 0:
                items = taskRContentProvider.getUnstartedWorkItems(notifyObservers);
                break;
            case 1:
                items = taskRContentProvider.getStartedWorkItems(notifyObservers);
                break;
            case 2:
                items = taskRContentProvider.getDoneWorkItems(notifyObservers);
                break;
            case 3:
                items = taskRContentProvider.getMyWorkItems(notifyObservers);
                break;
            default:
                items = taskRContentProvider.getWorkItems(notifyObservers);
                break;
        }
    }

    @Override
    public void notifyChange() {
        setTabPosition(false);
        view.updateAdapter();
    }
}
