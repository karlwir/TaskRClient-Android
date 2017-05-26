package taskr.se.taskr.teamdetailviews.detailviewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.model.Team;
import taskr.se.taskr.model.User;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;
import taskr.se.taskr.teamdetailviews.TeamDetailActivity;

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
    private TaskRContentProvider contentProvider;
    private User user = GlobalVariables.loggedInUser;

   public TeamDetailEditViewModel(Context context){
        this.context = context;
        this.interactor = new TeamDetailEditInteractorImpl();
        this.contentProvider = TaskRContentProviderImpl.getInstance(context);

    }

    public void save(){

        String nameString = name.get();
        String descriptionString = description.get();

        if(nameString != null && descriptionString != null){
            interactor.saveChanges(nameString, descriptionString, this);
        }

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
        Team team = contentProvider.getTeam(user.getTeams().get(0).getId());
        team.setName(name.get());
        team.setDescription(description.get());
        contentProvider.addOrUpdateTeam(team);
        context.startActivity(TeamDetailActivity.createIntent(context));
    }
    public static boolean isFinished(){
        return finish;
    }
}
