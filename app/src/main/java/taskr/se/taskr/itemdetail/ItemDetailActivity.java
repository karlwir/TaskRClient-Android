package taskr.se.taskr.itemdetail;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import taskr.se.taskr.R;

public class ItemDetailActivity extends AppCompatActivity {

    public static Intent createIntent(Context context){
        return new Intent(context, ItemDetailActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);



    }
}
