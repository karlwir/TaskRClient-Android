package taskr.se.taskr.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import taskr.se.taskr.R;
import taskr.se.taskr.home.itemlistfragment.ItemListFragment;

/**
 * Created by Jacob on 2017-05-11.
 */

public class ItemListFragmentContainer extends Fragment {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_list_fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pager = (ViewPager) view.findViewById(R.id.view_pager);
        pagerAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), FragmentPagerItems.with(getContext())
                .add(R.string.unstarted, ItemListFragment.class)
                .add(R.string.started, ItemListFragment.class)
                .add(R.string.done, ItemListFragment.class)
                .add(R.string.my_tasks, ItemListFragment.class).create());

        pager.setAdapter(pagerAdapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.view_pager_tab);
        viewPagerTab.setViewPager(pager);
    }
}
