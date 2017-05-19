package taskr.se.taskr.home.workitemviewmodel;

import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.support.v7.widget.AppCompatEditText;


/**
 * Created by Kevin on 2017-05-16.
 */

public class BindingHelper {

    @BindingAdapter("app:error")
    public static void setError(EditText editText, String errorMessage) {
        editText.setError(errorMessage);
    }

    @BindingAdapter("app:limiter")
    public static void setTextLimit(final EditText editText, String message) {

        editText.addTextChangedListener(new TextWatcher() {
            int maxLines = 2;
            String lastValue = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastValue = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (editText.getLineCount() > maxLines) {
                    int selectionStart = editText.getSelectionStart() - 1;
                    editText.setText(lastValue);
                    if (selectionStart >= editText.length()) {
                        selectionStart = editText.length();
                    }
                    editText.setSelection(selectionStart);
                }
            }
        });


    }
}
