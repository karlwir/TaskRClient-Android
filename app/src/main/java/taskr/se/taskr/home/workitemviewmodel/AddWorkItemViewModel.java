package taskr.se.taskr.home.workitemviewmodel;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import taskr.se.taskr.R;
import taskr.se.taskr.global.GlobalVariables;
import taskr.se.taskr.home.HomeActivity;
import taskr.se.taskr.itemdetail.ItemDetailEditFragment;
import taskr.se.taskr.model.User;
import taskr.se.taskr.model.WorkItem;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by Kevin on 2017-05-16.
 */

public class AddWorkItemViewModel implements AddWorkItemInteractor.OnWorkItemAddedListener {

    private static boolean finish;
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> status = new ObservableField<>();
    public ObservableField<String> titleError = new ObservableField<>();
    public ObservableField<String> descriptionError = new ObservableField<>();
    public ObservableBoolean visible = new ObservableBoolean();

    private final Fragment fragment;
    private final AddWorkItemInteractor interactor;
    private final TaskRContentProvider contentProvider;

    public AddWorkItemViewModel(Fragment fragment) {
        this.fragment = fragment;
        this.interactor = new AddWorkItemInteractorImpl();
        this.contentProvider = TaskRContentProviderImpl.getInstance(fragment.getContext());

        if (fragment instanceof AddWorkItemFragment) {
            visible.set(false);
        } else {
            visible.set(true);
        }

        if (fragment.getArguments() != null) {
            WorkItem workitem = contentProvider.getWorkItem(fragment.getArguments().getLong("id"));
            title.set(workitem.getTitle());
            description.set(workitem.getDescription());
            status.set(workitem.getStatus());
        }
    }


    public void save() {
        String titleString = title.get();
        String descriptionString = description.get();
        if (titleString != null && descriptionString != null) {
            interactor.saveWorkItem(titleString, descriptionString, this);
        }
    }

    @Override
    public void onTitleError() {
        titleError.set("Invalid name.");
    }

    @Override
    public void onDescriptionError() {
        descriptionError.set("Invalid description.");
    }

    @Override
    public void onSuccess() {
        finish = true;
        if (fragment instanceof ItemDetailEditFragment) {
            WorkItem item = contentProvider.getWorkItem(fragment.getArguments().getLong("id"));
            if (item != null) {
                item.setTitle(title.get());
                item.setDescription(description.get());
                item.setStatus(status.get());
                contentProvider.addOrUpdateWorkItem(item);
            } else {
                contentProvider.addOrUpdateWorkItem(new WorkItem(title.get(), description.get(),
                        fragment.getString(R.string.unstarted)));
            }
        }
        fragment.startActivity(HomeActivity.createIntent(fragment.getContext(), null));

    }

    public static boolean isFinished() {
        return finish;
    }

}
