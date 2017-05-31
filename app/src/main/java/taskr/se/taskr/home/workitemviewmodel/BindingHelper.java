package taskr.se.taskr.home.workitemviewmodel;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Kevin on 2017-05-16.
 */

public class BindingHelper {


    @BindingAdapter("app:setVisible")
    public static void setVisible(View view, boolean visible){
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter(value = {"bind:selectedValue", "bind:selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(AppCompatSpinner pAppCompatSpinner, String newSelectedValue, final InverseBindingListener newTextAttrChanged) {
        pAppCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newTextAttrChanged.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (newSelectedValue != null) {
            int pos = ((ArrayAdapter<String>) pAppCompatSpinner.getAdapter()).getPosition(newSelectedValue);
            pAppCompatSpinner.setSelection(pos, true);
        }
    }
    @InverseBindingAdapter(attribute = "bind:selectedValue", event =
            "bind:selectedValueAttrChanged")
    public static String captureSelectedValue(AppCompatSpinner pAppCompatSpinner) {
        return (String) pAppCompatSpinner.getSelectedItem();
    }



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
