package com.combintech.baojili.fragment.infostock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.adapter.InfoStockByLocItemAdapter;
import com.combintech.baojili.model.StockByLocation;

import static com.combintech.baojili.activity.infostock.InfoStockByLocDetailActivity.EXTRA_STOCK_BY_LOC;

public class InfoStockByLocDetailFragment extends Fragment {

    public static final String TAG = InfoStockByLocDetailFragment.class.getSimpleName();

    private StockByLocation stockByLocation;

    private InfoStockByLocItemAdapter infoStockByLocItemAdapter;

    public static InfoStockByLocDetailFragment newInstance() {

        Bundle args = new Bundle();

        InfoStockByLocDetailFragment fragment = new InfoStockByLocDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stockByLocation = getActivity().getIntent().getParcelableExtra(EXTRA_STOCK_BY_LOC);

        infoStockByLocItemAdapter = new InfoStockByLocItemAdapter(getContext(), stockByLocation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_stock_by_loc_detail, container, false);

        TextView tvTotalStock = view.findViewById(R.id.tv_total_stock);
        tvTotalStock.setText(stockByLocation.getTotalStock());

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(infoStockByLocItemAdapter);

        view.requestFocus();

        return view;
    }

    @TargetApi(23)
    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
//        onSignInFragmentListener = (OnSignInFragmentListener) context;
    }
}
