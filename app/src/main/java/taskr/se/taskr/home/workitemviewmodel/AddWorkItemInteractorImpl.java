package taskr.se.taskr.home.workitemviewmodel;

/**
 * Created by Kevin on 2017-05-16.
 */

public class AddWorkItemInteractorImpl implements AddWorkItemInteractor {

    @Override
    public void saveWorkItem(String title, String description, OnWorkItemAddedListener listener) {
        boolean error = false;

            if (title.length() < 4 || title.length() >= 15) {
                listener.onTitleError();
                error = true;
            }
            if (description.length() < 10) {
                listener.onDescriptionError();
                error = true;
            }
            if (!error) {
                listener.onSuccess();
            }
        }
    }

