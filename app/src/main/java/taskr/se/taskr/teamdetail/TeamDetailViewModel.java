package taskr.se.taskr.teamdetail;

import android.content.Context;
import android.databinding.ObservableField;
import android.widget.Toast;

import taskr.se.taskr.R;
import taskr.se.taskr.model.Team;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by kawi01 on 2017-06-01.
 */

public class TeamDetailViewModel {
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();

    private Context context;
    private TaskRContentProvider contentProvider;
    private Team team;

    public TeamDetailViewModel(Context context, Team team) {
        this.context = context;
        contentProvider = TaskRContentProviderImpl.getInstance(context);
        name.set(team.getName());
        description.set(team.getDescription());
        this.team = team;
    }

    public void save() {
        team.setName(name.get());
        team.setDescription(description.get());
        Long id = contentProvider.addOrUpdateTeam(team);
        if (id != null) {
            Toast.makeText(context, R.string.team_updated, Toast.LENGTH_LONG).show();

        }
    }
}
