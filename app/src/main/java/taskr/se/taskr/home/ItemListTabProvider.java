package taskr.se.taskr.home;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.HashMap;
import java.util.Map;

import taskr.se.taskr.R;
import taskr.se.taskr.repository.TaskRContentProviderImpl;

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
                title = "UNSTARTED";
                resIdBack = R.drawable.grey1;
                resIdFill = R.drawable.grey2;
                break;
            case 1:
                title = "STARTED";
                resIdBack = R.drawable.orange1;
                resIdFill = R.drawable.orange2;
                break;
            case 2:
                title = "DONE";
                resIdBack = R.drawable.green1;
                resIdFill = R.drawable.green2;
                break;
            case 3:
                title = "MY ITEMS";
                resIdBack = R.drawable.blue1;
                resIdFill = R.drawable.blue2;
                break;
        }

        int angle = calculateAngle(position);
        String itemCountString = getItemCountString(position);

        return new CircleSegment(context, itemCountString, title, angle, resIdBack, resIdFill);
    }

    private int calculateAngle(int position) {
        TaskRContentProviderImpl contentProvider = TaskRContentProviderImpl.getInstance(context);
        float maxAngle = 360;
        float maxItems = contentProvider.getUnstartedWorkItems(false).size()
                         + contentProvider.getStartedWorkItems(false).size()
                         + contentProvider.getDoneWorkItems(false).size();

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
        }
        return String.valueOf(itemCount);
    }
}