package se.taskr.home.itemlistfragment;

import se.taskr.model.WorkItem;

import java.util.List;

/**
 * Created by Jacob on 2017-05-13.
 */

public interface ItemListContract {
    interface View {
        void navigateToDetailView(WorkItem workItem);
        void updateAdapter();
    }

    interface Presenter {
        interface OnItemClickedListener {
            void onItemClicked(int id);
        }
        List<WorkItem> getItems();
        void setTabPosition(boolean notifyObservers);
        void notifyChange();
    }
}
