package taskr.se.taskr.home.workitemviewmodel;

import android.databinding.BindingAdapter;
import android.widget.EditText;

/**
 * Created by Kevin on 2017-05-16.
 */

public class BindingHelper {

    @BindingAdapter("bind:error")
    public static void setError(EditText editText, String errorMessage){
        editText.setText(errorMessage);
    }
}
