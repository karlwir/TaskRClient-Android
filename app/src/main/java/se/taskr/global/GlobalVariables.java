package se.taskr.global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import se.taskr.model.User;

/**
 * Created by Kevin on 2017-05-20.
 */

public class GlobalVariables {

    public static User loggedInUser = null;

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
