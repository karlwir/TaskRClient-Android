package taskr.se.taskr.teamdetailviews.detailviewmodel;

/**
 * Created by Kevin on 2017-05-24.
 */

public class TeamDetailEditInteractorImpl implements TeamDetailEditInteractor {


    @Override
    public void saveChanges(String name, String description, OnEditFinishListener listener) {
        boolean error = false;

        if(name.length() < 4 || name.length() >= 15){
            listener.onNameError();
            error = true;
        }
        if(description.length() < 10){
            listener.onDescriptionError();
            error = true;
        }
        if(!error){
           listener.onSuccess();
        }


    }
}
