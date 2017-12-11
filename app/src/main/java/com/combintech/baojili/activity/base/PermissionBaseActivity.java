package com.combintech.baojili.activity.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.combintech.baojili.R;
import com.combintech.baojili.bjinterface.NeedPermission;
import com.combintech.baojili.bjinterface.PermissionCode;

import java.lang.ref.WeakReference;

public abstract class PermissionBaseActivity extends BaseActivity implements NeedPermission {

    WeakReference<NeedPermission> needPermissionWeakReference;

    public void requestPermission(@Nullable NeedPermission caller, @PermissionCode int permissionCode) {
        // BEGIN_INCLUDE(camera_permission)
        // Check if the Camera permission is already available.
        String manifestPermission;
        if (permissionCode == PermissionCode.CALL) {
            manifestPermission = Manifest.permission.CALL_PHONE;
        } else if (permissionCode == PermissionCode.LOCATION) {
            manifestPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        } else if (permissionCode == PermissionCode.CAMERA) {
            manifestPermission = Manifest.permission.CAMERA;
        } else if (permissionCode == PermissionCode.READ_EXTERNAL_STORAGE) {
            manifestPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        } else if (permissionCode == PermissionCode.WRITE_EXTERNAL_STORAGE) {
            manifestPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        } else {
            manifestPermission = null;
        }

        if (TextUtils.isEmpty(manifestPermission)) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, manifestPermission)
                != PackageManager.PERMISSION_GRANTED) {
            // permission has not been granted.
            if (caller != null) {
                needPermissionWeakReference = new WeakReference<>(caller);
            } else {
                needPermissionWeakReference = null;
            }
            requestPermissionFor(manifestPermission, permissionCode);
        } else {
            // permissions is already available
            if (caller != null) {
                caller.onPermissionGranted(permissionCode);
            } else {
                onPermissionGranted(permissionCode);
            }
        }
    }


    // Manifest.permission.ACCESS_FINE_LOCATION
    // PermissionCode.LOCATION
    private void requestPermissionFor(String permissionManifestString, int permissionCode) {
        // BEGIN_INCLUDE(camera_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionManifestString)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionManifestString},
                    permissionCode);
        } else {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{permissionManifestString},
                    permissionCode);
        }
        // END_INCLUDE(camera_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        String message = "";
        if (requestCode == PermissionCode.CALL) {
            message = getString(R.string.please_grant_call_permission_in_settings);
        } else if (requestCode == PermissionCode.LOCATION) {
            message = getString(R.string.please_grant_location_permission_in_settings);
        } else if (requestCode == PermissionCode.CAMERA) {
            message = getString(R.string.please_grant_camera_permission_in_settings);
        } else if (requestCode == PermissionCode.READ_EXTERNAL_STORAGE) {
            message = getString(R.string.please_grant_external_storage_permission_in_settings);
        } else if (requestCode == PermissionCode.WRITE_EXTERNAL_STORAGE) {
            message = getString(R.string.please_grant_external_storage_permission_in_settings);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        // BEGIN_INCLUDE(permission_result)
        // Received permission result for permission.
        // Check if the only required permission has been granted
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission has been granted
            if (needPermissionWeakReference != null && needPermissionWeakReference.get() != null) {
                needPermissionWeakReference.get().onPermissionGranted(requestCode);
            } else {
                onPermissionGranted(requestCode);
            }
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            if (needPermissionWeakReference != null && needPermissionWeakReference.get() != null) {
                needPermissionWeakReference.get().onPermissionDenied(requestCode);
            } else {
                onPermissionDenied(requestCode);
            }
        }
        // END_INCLUDE(permission_result)
    }

    @Override
    public void onPermissionGranted(@PermissionCode int permissionCode) {
        // to be overidden in child if you wish
    }

    @Override
    public void onPermissionDenied(@PermissionCode int permissionCode) {
        // to be overidden in child if you wish
    }
}
