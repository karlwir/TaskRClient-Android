package taskr.se.taskr.itemdetail;

import android.content.Context;
import android.databinding.ObservableField;

import taskr.se.taskr.model.WorkItem;

/**
 * Created by Kevin on 2017-05-11.
 */

public class ItemDetailViewModel{

    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> status = new ObservableField<>();

    private final Context context;

   public ItemDetailViewModel(Context context){
       this.context = context.getApplicationContext();
       description.set("We need to sort this out right now my friends! We are in such a shitty situation");
       status.set("STARTED");
   }

}
