package taskr.se.taskr.teamdetailviews.detailviewmodel;

/**
 * Created by Kevin on 2017-05-24.
 */

public interface TeamDetailEditInteractor {

    interface OnEditFinishListener{
        void onNameError();
        void onDescriptionError();
        void onSuccess();
    }

    void saveChanges(String name, String description, OnEditFinishListener listener);
}
