package com.combintech.baojili.bjinterface;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Hendry Setiadi
 */

@IntDef({PermissionCode.CALL, PermissionCode.LOCATION, PermissionCode.CAMERA, PermissionCode.READ_EXTERNAL_STORAGE,
        PermissionCode.WRITE_EXTERNAL_STORAGE})
@Retention(RetentionPolicy.SOURCE)
public @interface PermissionCode {
    int CALL = 0;
    int LOCATION = 1;
    int CAMERA = 2;
    int READ_EXTERNAL_STORAGE = 3;
    int WRITE_EXTERNAL_STORAGE = 4;
}