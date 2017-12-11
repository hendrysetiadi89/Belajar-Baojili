package com.combintech.baojili.activity.infostock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.fragment.infostock.InfoStockByItemDetailFragment;
import com.combintech.baojili.model.StockByItem;

public class InfoStockByItemDetailActivity extends BaseActivity {

    public static final String EXTRA_STOCK_BY_ITEM = "x_stock_item";
    private StockByItem stockByItem;

    public static void start(Context context, StockByItem stockByItem) {
        Intent intent = new Intent(context, InfoStockByItemDetailActivity.class);
        intent.putExtra(EXTRA_STOCK_BY_ITEM, stockByItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        stockByItem = getIntent().getParcelableExtra(EXTRA_STOCK_BY_ITEM);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(InfoStockByItemDetailFragment.newInstance(), false, InfoStockByItemDetailFragment.TAG);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(stockByItem.getCode() + " " + stockByItem.getSize() + " - " + stockByItem.getDescription());
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
