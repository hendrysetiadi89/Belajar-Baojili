package com.combintech.baojili.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.combintech.baojili.application.BaseURL;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.NONE;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.RESOURCE;

/**
 * Created by Hendry Setiadi
 */

public class ImageHelper {
    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(appendWithBaseUrl(url)).into(imageView);
    }

    public static void loadImageCenterCrop(Context context, String url, ImageView imageView) {
        Glide.with(context).load(appendWithBaseUrl(url)).apply(RequestOptions.centerCropTransform()).into(imageView);
    }

    public static void loadImageCenterCropCircle(Context context, String url, ImageView imageView) {
        Glide.with(context).load(appendWithBaseUrl(url)).apply(RequestOptions.centerCropTransform().circleCrop()).into(imageView);
    }

    public static void loadImageFitCenter(Context context, String url, ImageView imageView) {
        Glide.with(context).load(appendWithBaseUrl(url)).apply(RequestOptions.fitCenterTransform()).into(imageView);
    }

    public static void clearDiskCache(Context applicationContext) {
        Glide.get(applicationContext).clearMemory();
        new ClearCacheTask(applicationContext).execute();
    }

    static class ClearCacheTask extends AsyncTask<Void, Void, Void> {
        Context applicationContext;
        ClearCacheTask(Context applicationContext) {
            this.applicationContext = applicationContext;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Glide.get(applicationContext).clearDiskCache();
            return null;
        }
    }

    public static void loadImageOri(Context context, String url, ImageView imageView) {
        Glide.with(context).load(appendWithBaseUrl(url))
                .apply(RequestOptions.diskCacheStrategyOf(RESOURCE))
                .into(imageView);
    }

    public static void loadBitmapFitCenter(Context context, String localPath, ImageView imageView) {
        Bitmap myBitmap = BitmapFactory.decodeFile(localPath);
        Glide.with(context).clear(imageView);
        imageView.setImageBitmap(myBitmap);
    }

    private static String appendWithBaseUrl(String url){
        if (url.startsWith("http")) {
            return url;
        } else {
            return BaseURL.IMG_BASE_URL + url;
        }
    }

}
