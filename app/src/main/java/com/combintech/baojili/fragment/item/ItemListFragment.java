package com.combintech.baojili.fragment.item;

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
import com.combintech.baojili.adapter.ItemListAdapter;
import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.apphelper.PreferenceMsItemHelper;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.DeleteItemPresenter;
import com.combintech.baojili.presenter.GetItemListPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.view.SearchView;

import java.util.ArrayList;

public class ItemListFragment extends Fragment implements GetItemListPresenter.GetItemListView, ItemListAdapter.OnAdapterItemClickedListener, DeleteItemPresenter.DeleteItemView {

    public static final String TAG = ItemListFragment.class.getSimpleName();

    private GetItemListPresenter getItemListPresenter;
    private DeleteItemPresenter deleteItemPresenter;

    private View progressBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String SAVED_DATA_LIST = "svd_data_list";
    public static final String SAVED_KEYWORD = "svd_keyword";

    private ItemListAdapter itemListAdapter;
    private ArrayList<BJItem> bjItemArrayList;

    private OnItemListFragmentListener onItemListFragmentListener;
    private String itemIDToDelete;
    private boolean needRefreshList;
    private FloatingActionButton fabAdd;
    private SearchView searchView;

    public interface OnItemListFragmentListener {
        void onProductItemClicked(BJItem bjItem);

        void onFabAddProductClicked();
    }

    public static ItemListFragment newInstance() {

        Bundle args = new Bundle();

        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getItemListPresenter = new GetItemListPresenter(this);
        deleteItemPresenter = new DeleteItemPresenter(this);
        String keyword;
        if (savedInstanceState == null) {
            bjItemArrayList = null;
            keyword = "";
        } else {
            bjItemArrayList = savedInstanceState.getParcelableArrayList(SAVED_DATA_LIST);
            keyword = savedInstanceState.getString(SAVED_KEYWORD, "");
        }
        itemListAdapter = new ItemListAdapter(getContext(), bjItemArrayList, this,
                PreferenceLoginHelper.getRole().equals(NetworkConstant.MANAJER), keyword);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        searchView = view.findViewById(R.id.search_view);
        searchView.setOnSearchViewTextChangedListener(new SearchView.OnSearchViewTextChangedListener() {
            @Override
            public void afterSearchTextChanged(String text) {
                itemListAdapter.setQuery(text);
            }
        });
        if (itemListAdapter.getBjItemArrayList().size() == 0) {
            searchView.setVisibility(View.GONE);
        }

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
        recyclerView.setAdapter(itemListAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                PreferenceMsItemHelper.setExpired();
                searchView.setVisibility(View.GONE);
                getItemListPresenter.getItemList("", "", "");
            }
        });

        fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemListFragmentListener.onFabAddProductClicked();
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
    public void onErrorGetItemList(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        searchView.setVisibility(View.GONE);

        itemListAdapter.setItemList(null);
        itemListAdapter.notifyDataSetChanged();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetItemList(ArrayList<BJItem> bjItemList) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        itemListAdapter.setItemList(bjItemList);
        itemListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        getItemListPresenter.onResume();
        deleteItemPresenter.onResume();
        if (needRefreshList) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            PreferenceMsItemHelper.setExpired();
            searchView.setVisibility(View.GONE);
            getItemListPresenter.getItemList("", "", "");
            needRefreshList = false;
            return;
        }

        if (itemListAdapter.getBjItemArrayList().size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            getItemListPresenter.getItemList("", "", "");
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void setNeedRefreshList() {
        needRefreshList = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        getItemListPresenter.onPause();
        deleteItemPresenter.onPause();
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
        onItemListFragmentListener = (OnItemListFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_DATA_LIST, itemListAdapter.getBjItemArrayList());
        outState.putString(SAVED_KEYWORD, searchView.getSearchText());
    }

    @Override
    public void onAdapterItemClicked(BJItem bjItem) {
        onItemListFragmentListener.onProductItemClicked(bjItem);
    }

    @Override
    public void onAdapterItemDeleteClicked(final BJItem bjItem) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.delete_item_x, bjItem.getCode() + " " + bjItem.getSize()))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((BaseActivity) getActivity()).showProgressDialog();
                        itemIDToDelete = bjItem.getItemID();
                        deleteItemPresenter.deleteItem(itemIDToDelete);
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
    public void onErrorDeleteItem(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessDeleteItem(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        if (!TextUtils.isEmpty(itemIDToDelete)) {
            itemListAdapter.removeItem(itemIDToDelete);
        }
    }
}
