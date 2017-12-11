package com.combintech.baojili.activity.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.combintech.baojili.R;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public MaterialDialog mProgressDialog;

    public void showProgressDialog() {
        showProgressDialog(null);
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new MaterialDialog.Builder(this).progress(true, 0)
                    .cancelable(false).canceledOnTouchOutside(false)
                    .build();
        }
        if (TextUtils.isEmpty(message)) {
            mProgressDialog.setContent(R.string.loading);
        } else {
            mProgressDialog.setContent(message);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backStackCount == 1) {
                setUpTitleByTag(null); // set default
            } else { //2 or more
                setUpTitleByTag(getSupportFragmentManager()
                        .getBackStackEntryAt(backStackCount-2)
                        .getName());
            }
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @CallSuper
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackstack) {
        replaceFragment(fragment, addToBackstack, fragment.getClass().getSimpleName());
    }

    public void replaceFragment(Fragment fragment, boolean addToBackstack, String tag) {
        if (!addToBackstack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, tag).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, tag).addToBackStack(tag).commit();
        }
        setUpTitleByTag (tag);
    }

    public void replaceAndHideOldFragment(Fragment fragment, boolean addToBackstack, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft= fragmentManager.beginTransaction();
        Fragment currentVisibileFragment = getCurrentVisibleFragment(fragmentManager);
        if (currentVisibileFragment!= null) {
            ft.hide(currentVisibileFragment);
        }
        if (!addToBackstack) {
            ft.add(R.id.fragment, fragment, tag).show(fragment).commit();
        }
        else {
            ft.add(R.id.fragment, fragment, tag).addToBackStack(tag).show(fragment).commit();
        }
        setUpTitleByTag (tag);
    }

    public boolean showFragment(String tag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);
        if (f == null) {
            return false;
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft= fragmentManager.beginTransaction();
            Fragment currentVisibileFragment = getCurrentVisibleFragment(fragmentManager);
            if (currentVisibileFragment!= null) {
                ft.hide(currentVisibileFragment);
            }
            ft.show(f).commit();
            setUpTitleByTag (tag);
            return true;
        }
    }

    // assume only 1 fragment visible
    private Fragment getCurrentVisibleFragment(FragmentManager fragmentManager){
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (int i =0, sizei = fragmentList.size(); i<sizei; i++) {
            Fragment f = fragmentList.get(i);
            if (f!= null && f.isVisible()) {
                return f;
            }
        }
        return null;
    }

    protected void setUpTitleByTag(String tag){
        // no operation
    }

}
