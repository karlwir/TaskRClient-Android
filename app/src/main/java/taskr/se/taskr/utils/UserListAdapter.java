package taskr.se.taskr.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import taskr.se.taskr.R;
import taskr.se.taskr.model.User;

/**
 * Created by kawi01 on 2017-06-02.
 */
public class UserListAdapter extends ArrayAdapter<User> {

    private int listicon;

    public UserListAdapter(Context context, int resource, List<User> members, Integer listIcon) {
        super(context, resource, members);
        this.listicon = listIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.user_list_item, null);
        }

        User user = getItem(position);

        if (user != null) {
            TextView name = (TextView) view.findViewById(R.id.team_member_name);
            TextView username = (TextView) view.findViewById(R.id.team_member_username);
            ImageView icon = (ImageView) view.findViewById(R.id.user_list_icon);

            name.setText(user.getFirstname() + " " + user.getLastname());
            username.setText("@" + user.getUsername());
            icon.setImageResource(listicon);
        }

        return view;
    }
}