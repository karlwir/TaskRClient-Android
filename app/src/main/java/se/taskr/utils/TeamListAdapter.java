package se.taskr.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import se.taskr.R;
import se.taskr.model.Team;

/**
 * Created by kawi01 on 2017-06-02.
 */
public class TeamListAdapter extends ArrayAdapter<Team> {

    private int listicon;

    public TeamListAdapter(Context context, int resource, List<Team> teams, Integer listIcon) {
        super(context, resource, teams);
        this.listicon = listIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.team_list_item, null);
        }

        Team team = getItem(position);

        if (team != null) {
            TextView name = (TextView) view.findViewById(R.id.team_name);
            TextView description = (TextView) view.findViewById(R.id.team_description);
            TextView members = (TextView) view.findViewById(R.id.team_members);
            ImageView icon = (ImageView) view.findViewById(R.id.user_list_icon);

            name.setText(team.getName());
            description.setText(team.getDescription());
            members.setText(String.valueOf(team.getUsers().size()) + " members");
            icon.setImageResource(listicon);
        }

        return view;
    }
}