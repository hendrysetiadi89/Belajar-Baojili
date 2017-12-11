package com.combintech.baojili.apphelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.util.FileUtils;

import java.io.File;

/**
 * Created by Hendry Setiadi
 */

public class GalleryActivityHelper {

    public static void startGalleryForResult(Activity activity){
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        if (pickIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(pickIntent,
                    RequestCode.GALLERY_REQUEST_CODE);
        }
    }

    public static void startCameraForResult(Activity activity){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File outputMediaFile = FileUtils.getCameraOutputFile(activity);
            Uri fileuri = FileUtils.getUri(activity, outputMediaFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
            activity.startActivityForResult(takePictureIntent,
                    RequestCode.CAMERA_REQUEST_CODE);
        }
    }
}
