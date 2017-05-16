package taskr.se.taskr.home.workitemviewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.util.Log;
import android.widget.Button;

import taskr.se.taskr.home.HomeActivity;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by Kevin on 2017-05-16.
 */

public class AddWorkItemViewModel implements AddWorkItemInteractor.OnWorkItemAddedListener{

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
        Log.d("SAVE METHOD", "save: ");
        if(titleString != null && descriptionString != null) {
           interactor.saveWorkItem(titleString, descriptionString, this);
            Log.d("SAVE METHOD", "save: IN IF");
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
       // TaskRContentProviderImpl.getInstance(context).addOrUpdateWorkItem(new WorkItem(title.get(), description.get(), "UNSTARTED"));
        context.startActivity(HomeActivity.createIntent(context));
    }
}
