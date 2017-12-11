package com.combintech.baojili.activity.infostock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.fragment.infostock.InfoStockByLocDetailFragment;
import com.combintech.baojili.model.StockByLocation;

public class InfoStockByLocDetailActivity extends BaseActivity {

    public static final String EXTRA_STOCK_BY_LOC = "x_stock_loc";
    private StockByLocation stockByLocation;

    public static void start(Context context, StockByLocation stockByLocation) {
        Intent intent = new Intent(context, InfoStockByLocDetailActivity.class);
        intent.putExtra(EXTRA_STOCK_BY_LOC, stockByLocation);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stockByLocation = getIntent().getParcelableExtra(EXTRA_STOCK_BY_LOC);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(InfoStockByLocDetailFragment.newInstance(), false, InfoStockByLocDetailFragment.TAG);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(stockByLocation.getLocationName());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
