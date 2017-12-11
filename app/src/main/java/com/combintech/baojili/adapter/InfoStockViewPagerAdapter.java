package com.combintech.baojili.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.combintech.baojili.R;
import com.combintech.baojili.fragment.infostock.InfoStockByItemFragment;
import com.combintech.baojili.fragment.infostock.InfoStockByLocFragment;

/**
 * Created by Hendry Setiadi
 */

public class InfoStockViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public InfoStockViewPagerAdapter(Context context, FragmentManager manager) {
        super(manager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return InfoStockByItemFragment.newInstance();
            case 1:
                return InfoStockByLocFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.stock_by_item);
            case 1:
                return context.getString(R.string.stock_by_location);
            default:
                return null;
        }
    }
}
