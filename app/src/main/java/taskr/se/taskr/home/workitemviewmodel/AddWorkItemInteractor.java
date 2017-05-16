package taskr.se.taskr.home.workitemviewmodel;

/**
 * Created by Kevin on 2017-05-16.
 */

public interface AddWorkItemInteractor {

    interface OnWorkItemAddedListener { // TODO: BETTER NAME
        void onTitleError();
        void onDescriptionError();
        void onSuccess();
    }

    void saveWorkItem(String title, String description, OnWorkItemAddedListener listener);
}
