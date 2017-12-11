package com.combintech.baojili.fragment.infostock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.combintech.baojili.R;
import com.combintech.baojili.adapter.InfoStockByItemAdapter;
import com.combintech.baojili.model.StockByItem;
import com.combintech.baojili.presenter.GetStockListByItemPresenter;
import com.combintech.baojili.util.ErrorUtil;

import java.util.ArrayList;

public class InfoStockByItemFragment extends Fragment
        implements GetStockListByItemPresenter.GetStockListByItemView,
        InfoStockByItemAdapter.OnAdapterItemClickedListener {

    public static final String TAG = InfoStockByItemFragment.class.getSimpleName();

    public static final String SAVED_DATA_LIST = "svd_data_list";


    private GetStockListByItemPresenter getStockListByItemPresenter;

    private View progressBar;
    private RecyclerView recyclerView;

    private InfoStockByItemAdapter infoStockByItemAdapter;
    private ArrayList<StockByItem> stockByItemArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;

    OnInfoStockByItemFragmentListener onInfoStockByItemFragmentListener;

    public interface OnInfoStockByItemFragmentListener{
        void onDetailStockByItemClicked(StockByItem stockByItem);
    }

    public static InfoStockByItemFragment newInstance() {

        Bundle args = new Bundle();

        InfoStockByItemFragment fragment = new InfoStockByItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStockListByItemPresenter = new GetStockListByItemPresenter(this);
        if (savedInstanceState == null) {
            stockByItemArrayList = null;
        } else {
            stockByItemArrayList = savedInstanceState.getParcelableArrayList(SAVED_DATA_LIST);
        }
        infoStockByItemAdapter = new InfoStockByItemAdapter(getContext(),stockByItemArrayList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_stock_by_item, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(infoStockByItemAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                getStockListByItemPresenter.getStockListByItem();
            }
        });
        view.requestFocus();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getStockListByItemPresenter.onResume();
        if (infoStockByItemAdapter.getStockByItemArrayList().size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getStockListByItemPresenter.getStockListByItem();
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getStockListByItemPresenter.onPause();
    }

    @Override
    public void onErrorGetStockListByItem(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        infoStockByItemAdapter.setStockByItemList(null);
        infoStockByItemAdapter.notifyDataSetChanged();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetStockListByItem(ArrayList<StockByItem> stockByItemList) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        infoStockByItemAdapter.setStockByItemList(stockByItemList);
        infoStockByItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAdapterItemClicked(StockByItem stockByItem) {
        onInfoStockByItemFragmentListener.onDetailStockByItemClicked(stockByItem);
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
        onInfoStockByItemFragmentListener = (OnInfoStockByItemFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_DATA_LIST, infoStockByItemAdapter.getStockByItemArrayList());
    }
}
