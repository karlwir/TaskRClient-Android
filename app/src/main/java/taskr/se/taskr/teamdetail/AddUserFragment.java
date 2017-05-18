package taskr.se.taskr.teamdetail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import taskr.se.taskr.R;
import taskr.se.taskr.databinding.TeamDetailFragmentBinding;
import taskr.se.taskr.repository.TaskRContentProvider;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by John on 2017-05-16.
 */

public class AddUserFragment extends Fragment {

     String firstName;
     String lastName;
     String userName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_fragment , container , false);
        final EditText etFirstname = (EditText) view.findViewById(R.id.et_firstname);
        final EditText etLastname = (EditText) view.findViewById(R.id.et_lastname);
        final EditText etUsername = (EditText) view.findViewById(R.id.et_username);
        Button saveBtn = (Button) view.findViewById(R.id.save_btn);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = etFirstname.getText().toString();
                lastName = etLastname.getText().toString();
                userName = etUsername.getText().toString();

                TaskRContentProviderImpl.getInstance(getContext());
            }
        });




        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
}
