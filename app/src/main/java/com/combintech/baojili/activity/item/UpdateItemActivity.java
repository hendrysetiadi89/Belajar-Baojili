package com.combintech.baojili.activity.item;

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
import com.combintech.baojili.fragment.item.UpdateItemFragment;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.util.FileUtils;

import java.io.File;

public class UpdateItemActivity extends PermissionBaseActivity
        implements ChooseImageFromFragment.OnChooseImageFromFragmentListener,
        UpdateItemFragment.OnUpdateItemFragmentListener {

    public static final String EXTRA_ITEM = "x_item";

    private BJItem bjItem;

    public static void start(Activity activity, BJItem bjItem) {
        Intent intent = new Intent(activity, UpdateItemActivity.class);
        intent.putExtra(EXTRA_ITEM, bjItem);
        activity.startActivityForResult(intent,RequestCode.UPDATE_ITEM_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bjItem = getIntent().getParcelableExtra(EXTRA_ITEM);

        setContentView(R.layout.activity_base_fragment);

        setUpToolbar();
        if (savedInstanceState == null) {
            replaceFragment(UpdateItemFragment.newInstance(), false, UpdateItemFragment.TAG);
        }
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.update_item) + " "+ bjItem.getCode()+ " " +bjItem.getSize());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private UpdateItemFragment getUpdateItemFragment() {
        return (UpdateItemFragment) getSupportFragmentManager().findFragmentByTag(UpdateItemFragment.TAG);
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
                        UpdateItemFragment updateItemFragment = getUpdateItemFragment();
                        if (updateItemFragment != null) {
                            updateItemFragment.onGalleryActivityResultOK(path);
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
                            UpdateItemFragment updateItemFragment = getUpdateItemFragment();
                            if (updateItemFragment != null) {
                                updateItemFragment.onGalleryActivityResultOK(file.getAbsolutePath());
                            }
                        }
                    } else {
                        String path = FileUtils.compressAndHandleRotate(this, uri);
                        UpdateItemFragment updateItemFragment = getUpdateItemFragment();
                        if (updateItemFragment != null) {
                            updateItemFragment.onGalleryActivityResultOK(path);
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSuccessUpdateItem() {
        setResult(Activity.RESULT_OK);
        this.finish();
    }
}
