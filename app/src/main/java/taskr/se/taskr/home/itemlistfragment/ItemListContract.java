package taskr.se.taskr.home.itemlistfragment;

import taskr.se.taskr.model.WorkItem;

import java.util.List;

/**
 * Created by Jacob on 2017-05-13.
 */

public interface ItemListContract {
    interface View {
        void navigateToDetailView(String itemKey);
    }
    interface Presenter {
        interface OnItemClickedListener {
            void onItemClicked(int id);
        }
        List<WorkItem> getItems();
    }
}
