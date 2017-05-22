package taskr.se.taskr.home.workitemviewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import taskr.se.taskr.home.HomeActivity;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by Kevin on 2017-05-16.
 */

public class AddWorkItemViewModel implements AddWorkItemInteractor.OnWorkItemAddedListener{

    private static boolean finish;
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> titleError = new ObservableField<>();
    public ObservableField<String> descriptionError = new ObservableField<>();

    private final Context context;
    private final AddWorkItemInteractor interactor;

    public AddWorkItemViewModel(Context context) {
        this.context = context.getApplicationContext();
        this.interactor = new AddWorkItemInteractorImpl();
    }

    public void save() {
        String titleString = title.get();
        String descriptionString = description.get();
        if(titleString != null && descriptionString != null) {
           interactor.saveWorkItem(titleString, descriptionString, this);
        }
    }

    @Override
    public void onTitleError() {titleError.set("Invalid name.");

    }

    @Override
    public void onDescriptionError() {descriptionError.set("Invalid description.");

    }

    @Override
    public void onSuccess() {
        finish = true;
        TaskRContentProviderImpl.getInstance(context).addOrUpdateWorkItem(new WorkItem(title.get(), description.get(), "UNSTARTED"));
        context.startActivity(HomeActivity.createIntent(context, null));

    }

    public static boolean isFinished(){
        return finish;
    }

}
