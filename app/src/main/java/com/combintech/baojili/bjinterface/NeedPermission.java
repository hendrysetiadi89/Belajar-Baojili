package com.combintech.baojili.bjinterface;

/**
 * Created by Hendry Setiadi
 */

public interface NeedPermission {
    void onPermissionGranted(@PermissionCode int permissionCode);
    void onPermissionDenied(@PermissionCode int permissionCode);
}
