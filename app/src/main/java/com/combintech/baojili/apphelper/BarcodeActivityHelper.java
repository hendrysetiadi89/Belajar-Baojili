package com.combintech.baojili.apphelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.combintech.baojili.constant.RequestCode;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Hendry Setiadi
 */

public class BarcodeActivityHelper {
    public static void startBarcodeActivityForResult(Activity activity){
        // launch barcode activity.
        Intent intent = new Intent(activity, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, PreferenceTorchHelper.isUseTorch());
        intent.putExtra(BarcodeCaptureActivity.BarcodeFormat, Barcode.EAN_8);

        activity.startActivityForResult(intent, RequestCode.RC_BARCODE_CAPTURE);
    }

    public static void startBarcodeActivityForResult(Fragment fragment, Context context){
        // launch barcode activity.
        Intent intent = new Intent(context, BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, PreferenceTorchHelper.isUseTorch());
        intent.putExtra(BarcodeCaptureActivity.BarcodeFormat, Barcode.EAN_8);

        fragment.startActivityForResult(intent, RequestCode.RC_BARCODE_CAPTURE);
    }
}
