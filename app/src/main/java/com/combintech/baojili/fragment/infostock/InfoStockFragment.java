package com.combintech.baojili.fragment.infostock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.combintech.baojili.R;
import com.combintech.baojili.adapter.InfoStockViewPagerAdapter;

public class InfoStockFragment extends Fragment {

    public static final String TAG = InfoStockFragment.class.getSimpleName();

    public static InfoStockFragment newInstance() {

        Bundle args = new Bundle();

        InfoStockFragment fragment = new InfoStockFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_stock, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager = view.findViewById(R.id.view_pager);

        InfoStockViewPagerAdapter adapter = new InfoStockViewPagerAdapter(getContext(), getChildFragmentManager());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        view.requestFocus();

        return view;
    }

}
