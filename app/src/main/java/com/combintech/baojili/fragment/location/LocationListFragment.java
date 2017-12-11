package com.combintech.baojili.fragment.location;

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

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.adapter.LocationListAdapter;
import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.DeleteLocationPresenter;
import com.combintech.baojili.presenter.GetLocationListPresenter;
import com.combintech.baojili.util.ErrorUtil;

import java.util.ArrayList;

public class LocationListFragment extends Fragment
        implements GetLocationListPresenter.GetLocationListView,
        DeleteLocationPresenter.DeleteLocationView, LocationListAdapter.OnAdapterItemClickedListener {

    public static final String TAG = LocationListFragment.class.getSimpleName();

    private GetLocationListPresenter getLocationListPresenter;
    private DeleteLocationPresenter deleteLocationPresenter;

    private View progressBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String SAVED_DATA_LIST = "svd_data_list";

    private LocationListAdapter locationListAdapter;
    private ArrayList<BJLocation> locationArrayList;

    private String locationIDToDelete;
    private boolean needRefreshList;
    private FloatingActionButton fabAdd;

    private OnLocationListFragmentListener onLocationListFragmentListener;
    public interface OnLocationListFragmentListener{
        void onLocationItemClicked(BJLocation location);
        void onFabAddLocationClicked();
    }

    public static LocationListFragment newInstance() {

        Bundle args = new Bundle();

        LocationListFragment fragment = new LocationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocationListPresenter = new GetLocationListPresenter(this);
        deleteLocationPresenter = new DeleteLocationPresenter(this);
        if (savedInstanceState == null) {
            locationArrayList = null;
        } else {
            locationArrayList = savedInstanceState.getParcelableArrayList(SAVED_DATA_LIST);
        }
        locationListAdapter = new LocationListAdapter(getContext(),locationArrayList,this,
                PreferenceLoginHelper.getRole().equals(NetworkConstant.MANAJER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

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
        recyclerView.setAdapter(locationListAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                PreferenceLocationHelper.setExpired();
                getLocationListPresenter.getLocationList("");
            }
        });

        fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLocationListFragmentListener.onFabAddLocationClicked();
            }
        });
        if (PreferenceLoginHelper.getRole().equals(NetworkConstant.MANAJER)) {
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
        } else {
            fabAdd.setVisibility(View.GONE);
        }
        view.requestFocus();

        return view;
    }

    @Override
    public void onErrorGetLocationList(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        locationListAdapter.setLocationList(null);
        locationListAdapter.notifyDataSetChanged();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetLocationList(ArrayList<BJLocation> bjLocationList) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        locationListAdapter.setLocationList(bjLocationList);
        locationListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorDeleteLocation(Throwable throwable) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessDeleteLocation(MessageResponse messageResponse) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        if (!TextUtils.isEmpty(locationIDToDelete)) {
            locationListAdapter.removeLocation(locationIDToDelete);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocationListPresenter.onResume();
        deleteLocationPresenter.onResume();
        if (needRefreshList) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            PreferenceLocationHelper.setExpired();
            getLocationListPresenter.getLocationList("");
            needRefreshList = false;
            return;
        }

        if (locationListAdapter.getLocationArrayList().size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getLocationListPresenter.getLocationList("");
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void setNeedRefreshList(){
        needRefreshList = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        getLocationListPresenter.onPause();
        deleteLocationPresenter.onPause();
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
        onLocationListFragmentListener = (OnLocationListFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_DATA_LIST, locationListAdapter.getLocationArrayList());
    }

    @Override
    public void onAdapterItemClicked(BJLocation bjLocation) {
        onLocationListFragmentListener.onLocationItemClicked(bjLocation);
    }

    @Override
    public void onAdapterItemDeleteClicked(final BJLocation bjLocation) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.delete_location_x, bjLocation.getName()))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((BaseActivity)getActivity()).showProgressDialog();
                        locationIDToDelete = bjLocation.getLocationID();
                        deleteLocationPresenter.deleteLocation(locationIDToDelete);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // no op, just dismiss
                    }
                });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

}
