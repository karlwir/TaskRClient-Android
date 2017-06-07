package se.taskr.workitemdetail;

import android.content.Context;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import se.taskr.R;
import se.taskr.model.WorkItem;
import se.taskr.repository.TaskRContentProvider;
import se.taskr.repository.TaskRContentProviderImpl;

import static se.taskr.model.BaseEntity.DEFAULT_ID;

/**
 * Created by kawi01 on 2017-06-04.
 */

public class WorkItemDetailViewModel {
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> status = new ObservableField<>();
    public ObservableField<Boolean> newWorkItem = new ObservableField<>();
    public ObservableField<String> titleError = new ObservableField<>();
    public ObservableField<String> descriptionError = new ObservableField<>();

    private Context context;
    private TaskRContentProvider contentProvider;
    private WorkItem workItem;
    private WorkItemDetailActivity activity;

    public WorkItemDetailViewModel(Context context, final WorkItem workItem, boolean newWorkItem, WorkItemDetailActivity activity) {
        contentProvider = TaskRContentProviderImpl.getInstance(context);
        title.set(workItem.getTitle());
        description.set(workItem.getDescription());
        status.set(workItem.getStatus());
        this.workItem = workItem;
        this.newWorkItem.set(newWorkItem);
        this.activity = activity;
        this.context = context;

        status.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (!workItem.getStatus().equals(status.get())) {
                    save();
                }
            }
        });
    }

    public void save() {
        if (title.get().length() < 5) {
            titleError.set("Title Error");
        } else if (description.get().length() < 10) {
            descriptionError.set("Description error");
        } else {
            workItem.setTitle(title.get());
            workItem.setDescription(description.get());
            workItem.setStatus(status.get());
            Long id = contentProvider.addOrUpdateWorkItem(workItem);

            if (id != DEFAULT_ID) {
                Toast.makeText(context, R.string.team_updated, Toast.LENGTH_LONG).show();
            }
            if (newWorkItem.get()) {
                activity.finish();
            }
        }
    }

}
