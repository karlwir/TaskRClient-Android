package se.taskr.home;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.LinearLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.HashMap;
import java.util.Map;

import se.taskr.R;
import se.taskr.global.GlobalVariables;
import se.taskr.repository.TaskRContentProviderImpl;

/**
 * Created by jacoblodenius on 22/05/17.
 */

public class ItemListTabProvider implements SmartTabLayout.TabProvider {

    private Context context;
    private Map<Integer, VectorCircleSegment> tabs;

    public ItemListTabProvider(Context context) {
        this.context = context;
        tabs = new HashMap<>();
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        VectorCircleSegment circleSegment = generateVectorSegmentFromPosition(position);
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

    private VectorCircleSegment generateVectorSegmentFromPosition(int position) {
        String title = "N/A";
        int backColor = 0;
        int fillColor = 0;
        switch (position) {
            case 0:
                title = context.getString(R.string.unstarted);
                backColor = R.color.colorCircleGrayBack;
                fillColor = R.color.colorCircleGrayFill;
                break;
            case 1:
                title = context.getString(R.string.started);
                backColor = R.color.colorCircleOrangeBack;
                fillColor = R.color.colorCircleOrangeFill;
                break;
            case 2:
                title = context.getString(R.string.done);
                backColor = R.color.colorCircleGreenBack;
                fillColor = R.color.colorCircleGreenFill;
                break;
            case 3:
                title = context.getString(R.string.my_tasks);
                backColor = R.color.colorCircleBlueBack;
                fillColor = R.color.colorCircleBlueFill;
                break;
        }

        float angle = calculateAngle(position);
        String itemCountString = getItemCountString(position);

        float radius = getSrceenWidth() / 8;

        VectorCircleSegment circleSegment = new VectorCircleSegment(context, null, itemCountString, title, radius, angle, backColor, fillColor);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.25f);
        param.gravity = Gravity.END;
        circleSegment.setLayoutParams(param);

        return circleSegment;
    }

    private int getSrceenWidth() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
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