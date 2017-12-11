package com.combintech.baojili.activity.user;

import android.app.Activity;
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
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.fragment.user.UpdateUserFragment;
import com.combintech.baojili.model.User;
import com.combintech.baojili.util.FileUtils;

import java.io.File;

public class UpdateUserActivity extends PermissionBaseActivity
        implements ChooseImageFromFragment.OnChooseImageFromFragmentListener,
        UpdateUserFragment.OnUpdateUserFragmentListener {

    public static final String EXTRA_USER = "x_user";

    private User user;

    public static void start(Activity activity, User user) {
        Intent intent = new Intent(activity, UpdateUserActivity.class);
        intent.putExtra(EXTRA_USER, user);
        activity.startActivityForResult(intent,RequestCode.UPDATE_USER_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = getIntent().getParcelableExtra(EXTRA_USER);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(UpdateUserFragment.newInstance(), false, UpdateUserFragment.TAG);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.update_user) + " "+ user.getUserID());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private UpdateUserFragment getUpdateUserFragment() {
        return (UpdateUserFragment) getSupportFragmentManager().findFragmentByTag(UpdateUserFragment.TAG);
    }

    @Override
    public void onChooseSelectImageCamera() {
        requestPermission(null, PermissionCode.CAMERA);
    }

    @Override
    public void onChooseSelectImageGallery() {
        requestPermission(null, PermissionCode.READ_EXTERNAL_STORAGE);
    }

    @Override
    public void onPermissionGranted(@PermissionCode int permissionCode) {
        super.onPermissionGranted(permissionCode);
        if (permissionCode == PermissionCode.CAMERA) {
            GalleryActivityHelper.startCameraForResult(this);
        } else if (permissionCode == PermissionCode.READ_EXTERNAL_STORAGE) {
            GalleryActivityHelper.startGalleryForResult(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.CAMERA_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    File cameraOutputFile = FileUtils.getCameraOutputFile(this);
                    if (cameraOutputFile.exists()) {
                        //compress it
                        String path = FileUtils.compressAndHandleRotate(this, cameraOutputFile);
                        UpdateUserFragment updateUserFragment = getUpdateUserFragment();
                        if (updateUserFragment != null) {
                            updateUserFragment.onGalleryActivityResultOK(path);
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
                            UpdateUserFragment updateUserFragment = getUpdateUserFragment();
                            if (updateUserFragment != null) {
                                updateUserFragment.onGalleryActivityResultOK(file.getAbsolutePath());
                            }
                        }
                    } else {
                        String path = FileUtils.compressAndHandleRotate(this, uri);
                        UpdateUserFragment updateUserFragment = getUpdateUserFragment();
                        if (updateUserFragment != null) {
                            updateUserFragment.onGalleryActivityResultOK(path);
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccessUpdateUser() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
