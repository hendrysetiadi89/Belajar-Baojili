package com.combintech.baojili.fragment.itemout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.adapter.ItemInListAdapter;
import com.combintech.baojili.adapter.ItemOutListAdapter;
import com.combintech.baojili.adapter.arrayadapter.LocationArrayAdapter;
import com.combintech.baojili.apphelper.PreferenceCodeAndSizeHelper;
import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.DeleteItemTrPresenter;
import com.combintech.baojili.presenter.GetItemInListPresenter;
import com.combintech.baojili.presenter.GetItemOutListPresenter;
import com.combintech.baojili.presenter.GetLocationListPresenter;
import com.combintech.baojili.presenter.PrepareCodeSizeLocPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.view.OnScrolledDownRVListener;

import java.util.ArrayList;

public class ItemOutListFragment extends Fragment implements DeleteItemTrPresenter.DeleteItemTrView,
        ItemOutListAdapter.OnAdapterItemClickedListener,
        GetLocationListPresenter.GetLocationListView,
        GetItemOutListPresenter.GetItemOutListView, PrepareCodeSizeLocPresenter.PrepareCodeSizeLocView {

    public static final String TAG = ItemOutListFragment.class.getSimpleName();

    public static final int ROW_PER_PAGE = 10;
    public static final String FABADD_IDENTIFIER = "1";
    private static final String ADAPTER_IDENTIFIER = "2";

    private GetItemOutListPresenter getItemOutListPresenter;
    private DeleteItemTrPresenter deleteItemTrPresenter;
    private GetLocationListPresenter getLocationListPresenter;
    private PrepareCodeSizeLocPresenter prepareCodeSizeLocPresenter;

    private View progressBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String SAVED_DATA_LIST = "svd_data_list";

    private ItemOutListAdapter itemOutListAdapter;
    private ArrayList<ItemInOut> itemInOutArrayList;
    private ArrayList<BJLocation> bjLocationArrayList;

    private OnItemOutListFragmentListener onItemOutListFragmentListener;

    private String itemTrIdToDelete;
    private boolean needRefreshList;
    private FloatingActionButton fabAdd;

    private Spinner spinnerLocation;
    private LocationArrayAdapter locationArrayAdapter;

    String locationIDToSearch = "";
    boolean isDeletedToSearch = false;
    private ItemInOut itemInOutClicked;

    @Override
    public void onErrorPrepareCodeSizeLocView(Throwable throwable) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessPrepareCodeSizeLocView(String callerIdentifier) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        switch (callerIdentifier) {
            case FABADD_IDENTIFIER:
                onItemOutListFragmentListener.onFabAdditemOutClicked();
                break;
            case ADAPTER_IDENTIFIER:
                onItemOutListFragmentListener.onItemOutClicked(itemInOutClicked);
                break;
        }
    }

    public interface OnItemOutListFragmentListener {
        void onItemOutClicked(ItemInOut itemInOut);
        void onFabAdditemOutClicked();
    }

    public static ItemOutListFragment newInstance() {

        Bundle args = new Bundle();

        ItemOutListFragment fragment = new ItemOutListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getItemOutListPresenter = new GetItemOutListPresenter(this);
        deleteItemTrPresenter = new DeleteItemTrPresenter(this);
        getLocationListPresenter = new GetLocationListPresenter(this);
        prepareCodeSizeLocPresenter = new PrepareCodeSizeLocPresenter(this);
        if (savedInstanceState == null) {
            itemInOutArrayList = null;
        } else {
            itemInOutArrayList = savedInstanceState.getParcelableArrayList(SAVED_DATA_LIST);
        }
        itemOutListAdapter = new ItemOutListAdapter(getContext(), itemInOutArrayList, ROW_PER_PAGE, this);

        bjLocationArrayList = PreferenceLocationHelper.getMsLocation();
        locationIDToSearch = PreferenceLocationHelper.getLocationId();
        initFirstPosSpinner();
        locationArrayAdapter = new LocationArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,
                bjLocationArrayList);
    }

    private void initFirstPosSpinner() {
        if (bjLocationArrayList == null || bjLocationArrayList.size() == 0) {
            bjLocationArrayList = new ArrayList<>();
            BJLocation bjLocation = new BJLocation();
            bjLocation.setName(getString(R.string.all_location));
            bjLocation.setLocationID("");
            bjLocationArrayList.add(bjLocation);
        } else if (!bjLocationArrayList.get(0).getLocationID().equals("")) {
            BJLocation bjLocation = new BJLocation();
            bjLocation.setName(getString(R.string.all_location));
            bjLocation.setLocationID("");
            bjLocationArrayList.add(0, bjLocation);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_out_list, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        spinnerLocation = view.findViewById(R.id.spinner_location);
        spinnerLocation.setAdapter(locationArrayAdapter);
        spinnerLocation.setSelection(getSelectedPosByLocID());
        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BJLocation bjLocation = (BJLocation) spinnerLocation.getSelectedItem();
                if (!bjLocation.getLocationID().equals(locationIDToSearch)) {
                    locationIDToSearch = bjLocation.getLocationID();
                    PreferenceLocationHelper.setLocationId(locationIDToSearch);
                    onSearchLocChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                linearLayoutManager.getOrientation()
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(itemOutListAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setEnabled(false);
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                itemOutListAdapter.reset();
                getItemOutListPresenter.getItemOutList(locationIDToSearch, 0, ROW_PER_PAGE, isDeletedToSearch);

                PreferenceLocationHelper.setExpired();
                getLocationListPresenter.getLocationList("");
                PreferenceCodeAndSizeHelper.setExpired();
            }
        });

        fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).showProgressDialog();
                prepareCodeSizeLocPresenter.getItemCodeSizeLocList(FABADD_IDENTIFIER);
            }
        });
        fabAdd.show();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) {
                    if (!fabAdd.isShown()) {
                        fabAdd.show();
                    }
                } else {
                    if (fabAdd.isShown()) {
                        fabAdd.hide();
                    }
                }
            }
        });

        recyclerView.addOnScrollListener(new OnScrolledDownRVListener() {
            @Override
            public void onScrolledDown(RecyclerView recyclerView, int dx, int dy) {
                // going down
                boolean canLoadMore = itemOutListAdapter.canLoadMore(linearLayoutManager);
                if (canLoadMore) {
                    itemOutListAdapter.setLoading(true);
                    swipeRefreshLayout.setEnabled(false);
                    getItemOutListPresenter.getItemOutList(
                            locationIDToSearch,
                            itemOutListAdapter.getDataList().size(),
                            ROW_PER_PAGE,
                            isDeletedToSearch);
                }
            }
        });

        view.requestFocus();

        return view;
    }

    private void onSearchLocChanged(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        itemOutListAdapter.reset();
        getItemOutListPresenter.getItemOutList(locationIDToSearch, 0, ROW_PER_PAGE, isDeletedToSearch);
    }

    private int getSelectedPosByLocID(){
        String locID = PreferenceLocationHelper.getLocationId();
        if (locID.equals("")) {
            return 0;
        }
        for (int i = 0, sizei = bjLocationArrayList.size(); i<sizei; i++) {
            if (bjLocationArrayList.get(i).getLocationID().equals(locID)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onErrorGetItemOutList(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);

        itemOutListAdapter.setDataList(null);
        itemOutListAdapter.notifyDataSetChanged();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetItemOutList(ArrayList<ItemInOut> itemInOutList) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setRefreshing(false);
        itemOutListAdapter.setLoading(false); // in case from load more
        itemOutListAdapter.addDataListAndNotify(itemInOutList);
    }

    @Override
    public void onResume() {
        super.onResume();
        getItemOutListPresenter.onResume();
        deleteItemTrPresenter.onResume();
        getLocationListPresenter.onResume();
        prepareCodeSizeLocPresenter.onResume();
        if (needRefreshList) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            itemOutListAdapter.reset();
            swipeRefreshLayout.setEnabled(false);
            getItemOutListPresenter.getItemOutList(locationIDToSearch, 0, ROW_PER_PAGE, isDeletedToSearch);
            needRefreshList = false;

            PreferenceLocationHelper.setExpired();
            getLocationListPresenter.getLocationList("");
            PreferenceCodeAndSizeHelper.setExpired();
            return;
        }

        if (itemOutListAdapter.getDataList().size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            swipeRefreshLayout.setEnabled(false);
            getItemOutListPresenter.getItemOutList(locationIDToSearch, 0, ROW_PER_PAGE, isDeletedToSearch);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (bjLocationArrayList == null || bjLocationArrayList.size() <= 1 ) { // all location is here
            getLocationListPresenter.getLocationList("");
        }
    }

    public void setNeedRefreshList() {
        needRefreshList = true;
    }

    @Override
    public void onErrorGetLocationList(Throwable throwable) {
        // no op
    }

    @Override
    public void onSuccessGetLocationList(ArrayList<BJLocation> bjLocationList) {
        PreferenceLocationHelper.setMsLocation(bjLocationList);
        this.bjLocationArrayList = bjLocationList;
        initFirstPosSpinner();
        locationArrayAdapter.clear();
        locationArrayAdapter.addAll(this.bjLocationArrayList);
        locationArrayAdapter.notifyDataSetChanged();
        spinnerLocation.setSelection(getSelectedPosByLocID());
    }

    @Override
    public void onPause() {
        super.onPause();
        getItemOutListPresenter.onPause();
        deleteItemTrPresenter.onPause();
        getLocationListPresenter.onPause();
        prepareCodeSizeLocPresenter.onPause();
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
        onItemOutListFragmentListener = (OnItemOutListFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_DATA_LIST, itemOutListAdapter.getDataList());
    }

    @Override
    public void onAdapterItemClicked(ItemInOut itemInOut) {
        itemInOutClicked = itemInOut;
        ((BaseActivity) getActivity()).showProgressDialog();
        prepareCodeSizeLocPresenter.getItemCodeSizeLocList(ADAPTER_IDENTIFIER);
    }

    @Override
    public void onAdapterItemDeleteClicked(final ItemInOut itemInOut) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.delete_transaction))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((BaseActivity) getActivity()).showProgressDialog();
                        itemTrIdToDelete = itemInOut.getTrItemID();
                        deleteItemTrPresenter.deleteItemTr(itemTrIdToDelete);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // no op, just dismiss
                    }
                });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }


    @Override
    public void onErrorDeleteItemTr(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessDeleteItemTr(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        if (!TextUtils.isEmpty(itemTrIdToDelete)) {
            itemOutListAdapter.removeItem(itemTrIdToDelete);
        }
    }
}
