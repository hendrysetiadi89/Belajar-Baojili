package com.combintech.baojili.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.PermissionBaseActivity;
import com.combintech.baojili.apphelper.GalleryActivityHelper;
import com.combintech.baojili.bjinterface.PermissionCode;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.dialog.ChangePasswordDialogFragment;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.fragment.UserInfoFragment;
import com.combintech.baojili.util.FileUtils;

import java.io.File;

import static com.combintech.baojili.constant.RequestCode.CAMERA_REQUEST_CODE;
import static com.combintech.baojili.constant.RequestCode.GALLERY_REQUEST_CODE;

public class UserInfoActivity extends PermissionBaseActivity
        implements ChangePasswordDialogFragment.OnChangePasswordDialogFragmentListener,
        ChooseImageFromFragment.OnChooseImageFromFragmentListener,
        UserInfoFragment.OnUserInfoFragmentListener {

    private int requestFlag;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, UserInfoActivity.class);
        activity.startActivityForResult(intent, RequestCode.UPDATE_USER_INFO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(UserInfoFragment.newInstance(), false, UserInfoFragment.TAG);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onChangePasswordDialogClicked(String oldPassword, String newPassword) {
        UserInfoFragment userInfoFragment = getUserInfoFragment();
        if (userInfoFragment != null) {
            userInfoFragment.onChangePasswordDialogClicked(oldPassword, newPassword);
        }
    }

    private UserInfoFragment getUserInfoFragment() {
        return (UserInfoFragment) getSupportFragmentManager().findFragmentByTag(UserInfoFragment.TAG);
    }

    @Override
    public void onChooseSelectImageCamera() {
        requestFlag = CAMERA_REQUEST_CODE;
        requestPermission(null, PermissionCode.CAMERA);
    }

    @Override
    public void onChooseSelectImageGallery() {
        requestFlag = GALLERY_REQUEST_CODE;
        requestPermission(null, PermissionCode.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onPermissionGranted(@PermissionCode int permissionCode) {
        super.onPermissionGranted(permissionCode);
        if (permissionCode == PermissionCode.CAMERA) {
            requestPermission(null, PermissionCode.WRITE_EXTERNAL_STORAGE);
        } else if (permissionCode == PermissionCode.WRITE_EXTERNAL_STORAGE) {
            if (requestFlag == GALLERY_REQUEST_CODE) {
                GalleryActivityHelper.startGalleryForResult(this);
            } else {
                GalleryActivityHelper.startCameraForResult(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    File cameraOutputFile = FileUtils.getCameraOutputFile(this);
                    if (cameraOutputFile.exists()) {
                        //compress it
                        String path = FileUtils.compressAndHandleRotate(this, cameraOutputFile);
                        UserInfoFragment userInfoFragment = getUserInfoFragment();
                        if (userInfoFragment != null) {
                            userInfoFragment.onGalleryActivityResultOK(path);
                        }
                    }
                }
                break;
            case RequestCode.GALLERY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        Bitmap thumbnail =(Bitmap) data.getExtras().get("data");
                        if (thumbnail != null) {
                            File file = FileUtils.writeBitmapToOutputFile(this, thumbnail);
                            if (file == null) {
                                return;
                            }
                            UserInfoFragment userInfoFragment = getUserInfoFragment();
                            if (userInfoFragment != null) {
                                userInfoFragment.onGalleryActivityResultOK(file.getAbsolutePath());
                            }
                        }
                    } else {
                        String path = FileUtils.compressAndHandleRotate(this, uri);
                        UserInfoFragment userInfoFragment = getUserInfoFragment();
                        if (userInfoFragment != null) {
                            userInfoFragment.onGalleryActivityResultOK(path);
                        }
                    }

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onUserChanged() {
        setResult(Activity.RESULT_OK);
    }
}
