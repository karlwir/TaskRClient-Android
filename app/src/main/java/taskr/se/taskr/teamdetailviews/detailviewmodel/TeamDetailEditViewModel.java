package taskr.se.taskr.teamdetailviews.detailviewmodel;

import android.content.Context;
import android.databinding.ObservableField;

import taskr.se.taskr.model.Team;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by Kevin on 2017-05-24.
 */

public class TeamDetailEditViewModel implements TeamDetailEditInteractor.OnEditFinishListener{


    private static boolean finish = false;
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> nameError = new ObservableField<>();
    public ObservableField<String> descriptionError = new ObservableField<>();

    private Context context;
    private TeamDetailEditInteractor interactor;

    TeamDetailEditViewModel(Context context){
        this.context = context;
        this.interactor = new TeamDetailEditInteractorImpl();
    }


    @Override
    public void onNameError() {
        nameError.set("Invalid name.");
    }

    @Override
    public void onDescriptionError() {
        descriptionError.set("Invalid Description");
    }

    @Override
    public void onSuccess() {
        finish = true;
        TaskRContentProviderImpl.getInstance(context).addOrUpdateTeam(new Team(name.get(),
                description.get()));

    }
}
