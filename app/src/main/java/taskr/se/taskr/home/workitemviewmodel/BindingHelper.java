package taskr.se.taskr.home.workitemviewmodel;

import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Kevin on 2017-05-16.
 */

public class BindingHelper {

    @BindingAdapter("app:error")
    public static void setError(EditText editText, String errorMessage){
        editText.setError(errorMessage);
    }

}
