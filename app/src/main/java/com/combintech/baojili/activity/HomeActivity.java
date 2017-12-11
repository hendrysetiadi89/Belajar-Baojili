package com.combintech.baojili.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.auth.AuthActivity;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.activity.infostock.InfoStockByItemDetailActivity;
import com.combintech.baojili.activity.infostock.InfoStockByLocDetailActivity;
import com.combintech.baojili.activity.item.AddItemActivity;
import com.combintech.baojili.activity.item.UpdateItemActivity;
import com.combintech.baojili.activity.itemin.AddItemInActivity;
import com.combintech.baojili.activity.itemin.UpdateItemInActivity;
import com.combintech.baojili.activity.itemout.AddItemOutActivity;
import com.combintech.baojili.activity.itemout.UpdateItemOutActivity;
import com.combintech.baojili.activity.location.AddLocationActivity;
import com.combintech.baojili.activity.location.UpdateLocationActivity;
import com.combintech.baojili.activity.user.AddUserActivity;
import com.combintech.baojili.activity.user.UpdateUserActivity;
import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.fragment.itemin.ItemInListFragment;
import com.combintech.baojili.fragment.itemout.ItemOutListFragment;
import com.combintech.baojili.fragment.location.LocationListFragment;
import com.combintech.baojili.fragment.infostock.InfoStockByItemFragment;
import com.combintech.baojili.fragment.infostock.InfoStockByLocFragment;
import com.combintech.baojili.fragment.infostock.InfoStockFragment;
import com.combintech.baojili.fragment.item.ItemListFragment;
import com.combintech.baojili.fragment.PrintBarcodeFragment;
import com.combintech.baojili.fragment.user.UserListFragment;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.model.StockByItem;
import com.combintech.baojili.model.StockByLocation;
import com.combintech.baojili.model.User;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.view.MenuDrawerView;

public class HomeActivity extends BaseActivity implements View.OnClickListener ,
        InfoStockByLocFragment.OnInfoStockByLocFragmentListener,
        InfoStockByItemFragment.OnInfoStockByItemFragmentListener,
        ItemListFragment.OnItemListFragmentListener,
        UserListFragment.OnUserListFragmentListener,
        LocationListFragment.OnLocationListFragmentListener,
        ItemInListFragment.OnItemInListFragmentListener,
        ItemOutListFragment.OnItemOutListFragmentListener{

    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;

    public static final String SAVED_SEL_DRAWER_POS = "svd_sel_drw";

    private int selectedDrawerPosition;

    public static final int POS_INFO_STOCK = 1;
    public static final int POS_ITEM_IN = 2;
    public static final int POS_ITEM_OUT = 3;
    public static final int POS_PRINT_BARCODE = 4;
    public static final int POS_ITEM_LIST = 5;
    public static final int POS_USER_LIST = 6;
    public static final int POS_LOCATION_LIST = 7;
    public static final int POS_LOGOUT = 8;
    SparseArray<MenuDrawerView> menuDrawerMap;
    private boolean needSetUpUserDisplay;

    public static void start(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    public static void startNewTask(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        setUpToolbar();
        setUpUserDisplay();

        if (savedInstanceState == null) {
            selectedDrawerPosition = POS_INFO_STOCK;
            replaceFragment(InfoStockFragment.newInstance(), false, InfoStockFragment.TAG);
        } else {
            selectedDrawerPosition = savedInstanceState.getInt(SAVED_SEL_DRAWER_POS);
        }
        setUpDrawerItems();
    }

    private void setUpDrawerItems() {
        menuDrawerMap = new SparseArray<>();
        MenuDrawerView mdvStockInfo = findViewById(R.id.mdv_stock_info);
        MenuDrawerView mdvItemIn = findViewById(R.id.mdv_item_in);
        MenuDrawerView mdvItemOut = findViewById(R.id.mdv_item_out);
        MenuDrawerView mdvPrintBarcode = findViewById(R.id.mdv_print_barcode);
        MenuDrawerView mdvItemList = findViewById(R.id.mdv_item_list);
        MenuDrawerView mdvUserList = findViewById(R.id.mdv_user_list);
        MenuDrawerView mdvLocationList = findViewById(R.id.mdv_location_list);
        MenuDrawerView mdvLogout = findViewById(R.id.mdv_logout);

        menuDrawerMap.put(POS_INFO_STOCK, mdvStockInfo);
        menuDrawerMap.put(POS_ITEM_IN, mdvItemIn);
        menuDrawerMap.put(POS_ITEM_OUT, mdvItemOut);
        menuDrawerMap.put(POS_PRINT_BARCODE, mdvPrintBarcode);
        menuDrawerMap.put(POS_ITEM_LIST, mdvItemList);
        menuDrawerMap.put(POS_USER_LIST, mdvUserList);
        menuDrawerMap.put(POS_LOCATION_LIST, mdvLocationList);
        menuDrawerMap.put(POS_LOGOUT, mdvLogout);
        menuDrawerMap.get(POS_LOGOUT);
        menuDrawerMap.get(selectedDrawerPosition).setSelected(true);

        mdvStockInfo.setTag(POS_INFO_STOCK);
        mdvItemIn.setTag(POS_ITEM_IN);
        mdvItemOut.setTag(POS_ITEM_OUT);
        mdvPrintBarcode.setTag(POS_PRINT_BARCODE);
        mdvItemList.setTag(POS_ITEM_LIST);
        mdvUserList.setTag(POS_USER_LIST);
        mdvLocationList.setTag(POS_LOCATION_LIST);
        mdvLogout.setTag(POS_LOGOUT);

        mdvStockInfo.setOnClickListener(this);
        mdvItemIn.setOnClickListener(this);
        mdvItemOut.setOnClickListener(this);
        mdvPrintBarcode.setOnClickListener(this);
        mdvItemList.setOnClickListener(this);
        mdvUserList.setOnClickListener(this);
        mdvLocationList.setOnClickListener(this);
        mdvLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int pos = (int) view.getTag();
        if (selectedDrawerPosition == pos) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (pos == POS_LOGOUT) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppCompatAlertDialogStyle)
                    .setTitle(getString(R.string.sign_out_ask))
                    .setPositiveButton(getString(R.string.sign_out), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PreferenceLoginHelper.logOut();
                            AuthActivity.startNewTask(HomeActivity.this);
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // no op, just dismiss
                        }
                    });
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
            return;
        }
        //set prev to false;
        menuDrawerMap.get(selectedDrawerPosition).setSelected(false);
        selectedDrawerPosition = pos;
        menuDrawerMap.get(selectedDrawerPosition).setSelected(true);
        mDrawerLayout.closeDrawers();
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeFragmentBasedOnPos(pos);
            }
        }, 300);
    }

    private void changeFragmentBasedOnPos(int pos){
        switch (pos) {
            case POS_INFO_STOCK:
                /*if (! showFragment(InfoStockFragment.TAG)) {
                    replaceAndHideOldFragment(InfoStockFragment.newInstance(), false, InfoStockFragment.TAG);
                }*/
                replaceFragment(InfoStockFragment.newInstance(), false, InfoStockFragment.TAG);
                break;
            case POS_ITEM_IN:
                replaceFragment(ItemInListFragment.newInstance(), false, ItemInListFragment.TAG);
                break;
            case POS_ITEM_OUT:
                replaceFragment(ItemOutListFragment.newInstance(), false, ItemOutListFragment.TAG);
                break;
            case POS_PRINT_BARCODE:
                replaceFragment(PrintBarcodeFragment.newInstance(), false, PrintBarcodeFragment.TAG);
                break;
            case POS_ITEM_LIST:
                replaceFragment(ItemListFragment.newInstance(), false, ItemListFragment.TAG);
                break;
            case POS_USER_LIST:
                replaceFragment(UserListFragment.newInstance(), false, UserListFragment.TAG);
                break;
            case POS_LOCATION_LIST:
                replaceFragment(LocationListFragment.newInstance(), false, LocationListFragment.TAG);
                break;
        }
    }

    private void setUpUserDisplay() {
        ImageView ivProfile = findViewById(R.id.iv_profile);
        TextView tvUserName = findViewById(R.id.tv_username);
        TextView tvEmail = findViewById(R.id.tv_email);
        String name = PreferenceLoginHelper.getName();
        String email = PreferenceLoginHelper.getEmail();
        String photo = PreferenceLoginHelper.getPhoto();
        if (TextUtils.isEmpty(name)) {
            tvUserName.setVisibility(View.GONE);
        } else {
            tvUserName.setText(name);
            tvUserName.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(email)) {
            tvEmail.setVisibility(View.GONE);
        } else {
            tvEmail.setText(email);
            tvEmail.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(photo)) {
            ivProfile.setImageResource(R.drawable.logo_baojili);
        } else {
            ImageHelper.loadImageCenterCropCircle(this, photo, ivProfile);
        }
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfoActivity.start(HomeActivity.this);
            }
        });
    }

    @Override
    public void setUpTitleByTag(String tag) {
        if (InfoStockFragment.TAG.equals(tag)) {
            getSupportActionBar().setTitle(R.string.info_stock);
        } else if (ItemInListFragment.TAG.equals(tag)) {
            getSupportActionBar().setTitle(R.string.item_in);
        } else if (ItemOutListFragment.TAG.equals(tag)) {
            getSupportActionBar().setTitle(R.string.item_out);
        } else if (PrintBarcodeFragment.TAG.equals(tag)) {
            getSupportActionBar().setTitle(R.string.print_barcode);
        } else if (ItemListFragment.TAG.equals(tag)) {
            getSupportActionBar().setTitle(R.string.product_list);
        } else if (UserListFragment.TAG.equals(tag)) {
            getSupportActionBar().setTitle(R.string.user_list);
        } else if (LocationListFragment.TAG.equals(tag)) {
            getSupportActionBar().setTitle(R.string.location_list);
        }
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.main_drawer_open, R.string.main_drawer_close);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needSetUpUserDisplay) {
            setUpUserDisplay();
            needSetUpUserDisplay = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_SEL_DRAWER_POS, selectedDrawerPosition);
    }

    @Override
    public void onDetailStockByLocationClicked(StockByLocation stockByLocation) {
        InfoStockByLocDetailActivity.start(this, stockByLocation);
    }

    @Override
    public void onDetailStockByItemClicked(StockByItem stockByItem) {
        InfoStockByItemDetailActivity.start(this, stockByItem);
    }

    @Override
    public void onProductItemClicked(BJItem bjItem) {
        UpdateItemActivity.start(this, bjItem);
    }

    @Override
    public void onFabAddProductClicked() {
        AddItemActivity.start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.ADD_ITEM_REQUEST_CODE:
            case RequestCode.UPDATE_ITEM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ItemListFragment itemListFragment =
                            (ItemListFragment) getSupportFragmentManager().findFragmentByTag(ItemListFragment.TAG);
                    if (itemListFragment!=null ) {
                        itemListFragment.setNeedRefreshList();
                    }
                }
                break;
            case RequestCode.ADD_USER_REQUEST_CODE:
            case RequestCode.UPDATE_USER_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    UserListFragment userListFragment =
                            (UserListFragment) getSupportFragmentManager().findFragmentByTag(UserListFragment.TAG);
                    if (userListFragment!=null ) {
                        userListFragment.setNeedRefreshList();
                    }
                }
                break;
            case RequestCode.ADD_LOCATION_REQUEST_CODE:
            case RequestCode.UPDATE_LOCATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    LocationListFragment locationListFragment =
                            (LocationListFragment) getSupportFragmentManager().findFragmentByTag(LocationListFragment.TAG);
                    if (locationListFragment!=null ) {
                        locationListFragment.setNeedRefreshList();
                    }
                }
                break;
            case RequestCode.ADD_ITEM_IN_REQUEST_CODE:
            case RequestCode.UPDATE_ITEM_IN_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ItemInListFragment itemInListFragment =
                            (ItemInListFragment) getSupportFragmentManager().findFragmentByTag(ItemInListFragment.TAG);
                    if (itemInListFragment!=null ) {
                        itemInListFragment.setNeedRefreshList();
                    }
                }
                break;
            case RequestCode.ADD_ITEM_OUT_REQUEST_CODE:
            case RequestCode.UPDATE_ITEM_OUT_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    ItemOutListFragment itemOutListFragment =
                            (ItemOutListFragment) getSupportFragmentManager().findFragmentByTag(ItemOutListFragment.TAG);
                    if (itemOutListFragment!=null ) {
                        itemOutListFragment.setNeedRefreshList();
                    }
                }
                break;

            case RequestCode.UPDATE_USER_INFO:
                if (resultCode == Activity.RESULT_OK) {
                    needSetUpUserDisplay = true;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onUserItemClicked(User user) {
        if (PreferenceLoginHelper.getUserId().equals(user.getUserID())) {
            UserInfoActivity.start(this);
        } else {
            UpdateUserActivity.start(this, user);
        }
    }

    @Override
    public void onFabAddUserClicked() {
        AddUserActivity.start(this);
    }

    @Override
    public void onLocationItemClicked(BJLocation location) {
        UpdateLocationActivity.start(this, location);
    }

    @Override
    public void onFabAddLocationClicked() {
        AddLocationActivity.start(this);
    }

    @Override
    public void onItemInClicked(ItemInOut itemInOut) {
        UpdateItemInActivity.start(this, itemInOut);
    }

    @Override
    public void onFabAdditemInClicked() {
        AddItemInActivity.start(this);
    }

    @Override
    public void onItemOutClicked(ItemInOut itemInOut) {
        UpdateItemOutActivity.start(this, itemInOut);
    }

    @Override
    public void onFabAdditemOutClicked() {
        AddItemOutActivity.start(this);
    }
}
