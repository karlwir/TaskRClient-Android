package se.taskr.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.HashMap;
import java.util.Map;

import se.taskr.R;
import se.taskr.global.GlobalVariables;
import se.taskr.home.CircleSegment;
import se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by jacoblodenius on 22/05/17.
 */

public class ItemListTabProvider implements SmartTabLayout.TabProvider {

    private Context context;
    private Map<Integer, CircleSegment> tabs;

    public ItemListTabProvider(Context context) {
        this.context = context;
        tabs = new HashMap<>();
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        CircleSegment circleSegment = generateSegmentFromPosition(position);
        tabs.put(position, circleSegment);
        return circleSegment;
    }

    public void setActiveItem(int position) {
        for(int pos : tabs.keySet()) {
            if(pos == position) tabs.get(pos).setActive(true);
            else tabs.get(pos).setActive(false);
        }
    }

    public void redraw() {
        for(int i = 0; i < tabs.size(); i++) {
            tabs.get(i).setAngle(calculateAngle(i));
            tabs.get(i).setText(getItemCountString(i));
            tabs.get(i).invalidate();
        }
    }

    private CircleSegment generateSegmentFromPosition(int position) {
        String title = "N/A";
        int resIdBack = 0;
        int resIdFill = 0;
        switch (position) {
            case 0:
                title = context.getString(R.string.unstarted);
                resIdBack = R.drawable.grey_small1;
                resIdFill = R.drawable.grey_small2;
                break;
            case 1:
                title = context.getString(R.string.started);
                resIdBack = R.drawable.orange_small1;
                resIdFill = R.drawable.orange_small2;
                break;
            case 2:
                title = context.getString(R.string.done);
                resIdBack = R.drawable.green_small1;
                resIdFill = R.drawable.green_small2;
                break;
            case 3:
                title = context.getString(R.string.my_tasks);
                resIdBack = R.drawable.blue_small1;
                resIdFill = R.drawable.blue_small2;
                break;
        }

        int angle = calculateAngle(position);
        String itemCountString = getItemCountString(position);

        CircleSegment circleSegment = new CircleSegment(context, itemCountString, title, angle, resIdBack, resIdFill);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.25f);
        circleSegment.setLayoutParams(param);

        return circleSegment;
    }

    private int calculateAngle(int position) {
        TaskRContentProviderImpl contentProvider = TaskRContentProviderImpl.getInstance(context);
        float maxAngle = 360;
        float maxItems = contentProvider.getWorkItems(false).size();

        float currentItems = 0;
        switch (position) {
            case 0:
                currentItems = contentProvider.getUnstartedWorkItems(false).size();
                break;
            case 1:
                currentItems = contentProvider.getStartedWorkItems(false).size();
                break;
            case 2:
                currentItems = contentProvider.getDoneWorkItems(false).size();
                break;
            case 3:
                currentItems = contentProvider.getWorkItemsByUser(GlobalVariables.loggedInUser).size();
                break;
        }

        float result = (((currentItems / maxAngle) / (maxItems / maxAngle)) * maxAngle);
        return (int) result;
    }

    private String getItemCountString(int position) {
        TaskRContentProviderImpl contentProvider = TaskRContentProviderImpl.getInstance(context);
        int itemCount = 0;
        switch (position) {
            case 0:
                itemCount = contentProvider.getUnstartedWorkItems(false).size();
                break;
            case 1:
                itemCount = contentProvider.getStartedWorkItems(false).size();
                break;
            case 2:
                itemCount = contentProvider.getDoneWorkItems(false).size();
                break;
            case 3:
                itemCount = contentProvider.getWorkItemsByUser(GlobalVariables.loggedInUser).size();
                break;
        }

        return String.valueOf(itemCount);
    }
}