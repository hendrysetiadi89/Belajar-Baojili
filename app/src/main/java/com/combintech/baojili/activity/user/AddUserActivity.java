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
import com.combintech.baojili.fragment.user.AddUserFragment;
import com.combintech.baojili.util.FileUtils;

import java.io.File;

public class AddUserActivity extends PermissionBaseActivity
        implements ChooseImageFromFragment.OnChooseImageFromFragmentListener,
        AddUserFragment.OnAddUserFragmentListener {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AddUserActivity.class);
        activity.startActivityForResult(intent,RequestCode.ADD_USER_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(AddUserFragment.newInstance(), false, AddUserFragment.TAG);
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


    private AddUserFragment getAddUserFragment() {
        return (AddUserFragment) getSupportFragmentManager().findFragmentByTag(AddUserFragment.TAG);
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
                        AddUserFragment addUserFragment = getAddUserFragment();
                        if (addUserFragment != null) {
                            addUserFragment.onGalleryActivityResultOK(path);
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
                            AddUserFragment addUserFragment = getAddUserFragment();
                            if (addUserFragment != null) {
                                addUserFragment.onGalleryActivityResultOK(file.getAbsolutePath());
                            }
                        }
                    } else {
                        String path = FileUtils.compressAndHandleRotate(this, uri);
                        AddUserFragment addUserFragment = getAddUserFragment();
                        if (addUserFragment != null) {
                            addUserFragment.onGalleryActivityResultOK(path);
                        }
                    }

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccessAddUser() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
