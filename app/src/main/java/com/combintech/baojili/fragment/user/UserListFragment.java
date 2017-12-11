package com.combintech.baojili.fragment.user;

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
import com.combintech.baojili.adapter.UserListAdapter;
import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.model.User;
import com.combintech.baojili.presenter.DeleteItemPresenter;
import com.combintech.baojili.presenter.DeleteMemberPresenter;
import com.combintech.baojili.presenter.GetMemberListPresenter;
import com.combintech.baojili.util.ErrorUtil;

import java.util.ArrayList;

public class UserListFragment extends Fragment implements
        GetMemberListPresenter.GetMemberListView,
        DeleteMemberPresenter.DeleteMemberView,
        UserListAdapter.OnAdapterItemClickedListener {

    public static final String TAG = UserListFragment.class.getSimpleName();

    private GetMemberListPresenter getMemberListPresenter;
    private DeleteMemberPresenter deleteMemberPresenter;

    private View progressBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String SAVED_DATA_LIST = "svd_data_list";

    private UserListAdapter userListAdapter;
    private ArrayList<User> userArrayList;

    private String userIDToDelete;
    private boolean needRefreshList;
    private FloatingActionButton fabAdd;

    private OnUserListFragmentListener onUserListFragmentListener;
    public interface OnUserListFragmentListener{
        void onUserItemClicked(User user);
        void onFabAddUserClicked();
    }

    public static UserListFragment newInstance() {

        Bundle args = new Bundle();

        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMemberListPresenter = new GetMemberListPresenter(this);
        deleteMemberPresenter = new DeleteMemberPresenter(this);
        if (savedInstanceState == null) {
            userArrayList = null;
        } else {
            userArrayList = savedInstanceState.getParcelableArrayList(SAVED_DATA_LIST);
        }
        userListAdapter = new UserListAdapter(getContext(),userArrayList,this,
                PreferenceLoginHelper.getRole().equals(NetworkConstant.MANAJER),
                PreferenceLoginHelper.getUserId());
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
        recyclerView.setAdapter(userListAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                getMemberListPresenter.getMemberListView();
            }
        });

        fabAdd = view.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUserListFragmentListener.onFabAddUserClicked();
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
    public void onErrorGetMemberList(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        userListAdapter.setUserList(null);
        userListAdapter.notifyDataSetChanged();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetMemberList(ArrayList<User> userList) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        userListAdapter.setUserList(userList);
        userListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorDeleteMember(Throwable throwable) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessDeleteMember(MessageResponse messageResponse) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        if (!TextUtils.isEmpty(userIDToDelete)) {
            userListAdapter.removeUser(userIDToDelete);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMemberListPresenter.onResume();
        deleteMemberPresenter.onResume();
        if (needRefreshList) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getMemberListPresenter.getMemberListView();
            needRefreshList = false;
            return;
        }

        if (userListAdapter.getUserArrayList().size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getMemberListPresenter.getMemberListView();
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
        getMemberListPresenter.onPause();
        deleteMemberPresenter.onPause();
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
        onUserListFragmentListener = (OnUserListFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVED_DATA_LIST, userListAdapter.getUserArrayList());
    }

    @Override
    public void onAdapterItemClicked(User user) {
        onUserListFragmentListener.onUserItemClicked(user);
    }

    @Override
    public void onAdapterItemDeleteClicked(final User user) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
                .setTitle(getString(R.string.delete_user_x, user.getUserID()))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((BaseActivity)getActivity()).showProgressDialog();
                        userIDToDelete = user.getUserID();
                        deleteMemberPresenter.deleteMember(userIDToDelete);
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
