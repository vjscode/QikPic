package com.tuts.vijay.qikpic.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tuts.vijay.qikpic.R;
import com.tuts.vijay.qikpic.fragment.QikPicGridFragment;
import com.tuts.vijay.qikpic.fragment.QikPicListFragment;

/**
 * Created by vijay on 11/13/15.
 */
public class FeedAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private int[] imageResId = {
            R.drawable.ic_grid_on_black_24dp,
            R.drawable.ic_list_black_24dp
    };
    private Context context;

    public FeedAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new QikPicGridFragment();
        } else {
            return new QikPicListFragment();
        }
    }
}