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
import com.combintech.baojili.adapter.InfoStockByLocAdapter;
import com.combintech.baojili.model.StockByLocation;
import com.combintech.baojili.presenter.GetStockListByLocPresenter;
import com.combintech.baojili.util.ErrorUtil;

import java.util.ArrayList;

public class InfoStockByLocFragment extends Fragment implements GetStockListByLocPresenter.GetStockListByLocView, InfoStockByLocAdapter.OnAdapterItemClickedListener {

    public static final String TAG = InfoStockByLocFragment.class.getSimpleName();

    public static final String SAVED_DATA_LIST = "svd_data_list";

    private GetStockListByLocPresenter getStockListByLocPresenter;
    private View progressBar;
    private RecyclerView recyclerView;

    private InfoStockByLocAdapter infoStockByLocAdapter;
    private ArrayList<StockByLocation> stockByLocationList;
    private SwipeRefreshLayout swipeRefreshLayout;

    OnInfoStockByLocFragmentListener onInfoStockByLocFragmentListener;
    public interface OnInfoStockByLocFragmentListener{
        void onDetailStockByLocationClicked(StockByLocation stockByLocation);
    }

    public static InfoStockByLocFragment newInstance() {

        Bundle args = new Bundle();

        InfoStockByLocFragment fragment = new InfoStockByLocFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getStockListByLocPresenter = new GetStockListByLocPresenter(this);
        if (savedInstanceState == null) {
            stockByLocationList = null;
        } else {
            stockByLocationList = savedInstanceState.getParcelableArrayList(SAVED_DATA_LIST);
        }
        infoStockByLocAdapter = new InfoStockByLocAdapter(getContext(),stockByLocationList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_stock_by_loc, container, false);

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
        recyclerView.setAdapter(infoStockByLocAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                getStockListByLocPresenter.getStockListByLoc();
            }
        });
        view.requestFocus();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getStockListByLocPresenter.onResume();
        if (infoStockByLocAdapter.getStockByLocationList().size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getStockListByLocPresenter.getStockListByLoc();
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getStockListByLocPresenter.onPause();
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
        onInfoStockByLocFragmentListener = (OnInfoStockByLocFragmentListener) context;
    }

    @Override
    public void onErrorGetStockList(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        infoStockByLocAdapter.setStockByLocationList(null);
        infoStockByLocAdapter.notifyDataSetChanged();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetStockList(ArrayList<StockByLocation> stockByLocationList) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        infoStockByLocAdapter.setStockByLocationList(stockByLocationList);
        infoStockByLocAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_DATA_LIST, infoStockByLocAdapter.getStockByLocationList());
    }

    @Override
    public void onAdapterItemClicked(StockByLocation stockByLocation) {
        onInfoStockByLocFragmentListener.onDetailStockByLocationClicked(stockByLocation);
    }
}
