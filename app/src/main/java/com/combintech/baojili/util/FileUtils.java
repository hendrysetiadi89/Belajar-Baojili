package com.combintech.baojili.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Hendry Setiadi
 */

public class FileUtils {

    public static final int MAX_FILE_SIZE_UPLOAD = 2 * 1024 * 1024;
    public static final int MAX_WIDTH_OR_HEIGHT = 960;

    public static String compressAndHandleRotate(Context context, File sourceFile) {
        Bitmap bitmap = getBitmapFromFile(sourceFile);
        Bitmap rotatedBitmap = rotateBitmapIfNeeded(bitmap, sourceFile);
        Bitmap resizedBitmap = resizeBitmap(rotatedBitmap);
        File outputFile = writeBitmapToOutputFile(context, resizedBitmap);
        if (outputFile == null) {
            return sourceFile.getAbsolutePath();
        } else {
            return outputFile.getAbsolutePath();
        }
    }

    public static String compressAndHandleRotate(Context context, Uri galleryUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), galleryUri);
        }catch (Exception e) {
            return getPath(context, galleryUri);
        }
        if (bitmap == null) {
            return getPath(context, galleryUri);
        }
        Bitmap rotatedBitmap = null;
        try {
            rotatedBitmap = rotateBitmapIfNeeded(context, bitmap, galleryUri);
        } catch (Exception e) {
            return getPath(context, galleryUri);
        }
        Bitmap resizedBitmap = resizeBitmap(rotatedBitmap);
        File outputFile = writeBitmapToOutputFile(context, resizedBitmap);
        if (outputFile == null) {
            return getPath(context, galleryUri);
        } else {
            return outputFile.getAbsolutePath();
        }
    }

    public static File writeBitmapToOutputFile(Context context, Bitmap bitmap){
        File imageFile = FileUtils.getRotateOutputFile(context);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapdata = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(imageFile.getAbsoluteFile());

            fos.write(bitmapdata);
            fos.close();
            return imageFile;
        } catch (java.io.IOException e) {
            return null;
        }
    }

    public static Bitmap getBitmapFromFile(File sourceFile) {
//        BitmapFactory.Options checksize = new BitmapFactory.Options();
//        checksize.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        checksize.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(sourceFile.getAbsolutePath(), checksize);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.inSampleSize = calculateInSampleSizeByLength(sourceFile.length());
        return BitmapFactory.decodeFile(sourceFile.getAbsolutePath());
    }

    public static Bitmap resizeBitmap (Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap scaledBitmap = null;
        if (width > height && width > MAX_WIDTH_OR_HEIGHT) {
            int newWidth = MAX_WIDTH_OR_HEIGHT;
            float ratio = ((float)newWidth / width);
            int newHeight =(int) (ratio * height);
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        } else if (height > width && height > MAX_WIDTH_OR_HEIGHT) {
            int newHeight = MAX_WIDTH_OR_HEIGHT;
            float ratio = ((float)newHeight / height);
            int newWidth =(int) (ratio * width);
            scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        }
        if (scaledBitmap != null) {
            return scaledBitmap;
        }
        else {
            return bitmap;
        }

    }

    private static Bitmap rotateBitmapIfNeeded(Bitmap bitmap, File file){
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            return rotateCheckExif(exif, bitmap);
        }
        catch (Exception e) {
            return bitmap;
        }
    }

    public static Bitmap rotateBitmapIfNeeded(Context context, Bitmap bitmap, Uri uri) throws IOException {
        ExifInterface exif = getExifByUri(context, uri);
        return rotateCheckExif(exif, bitmap);
    }

    private static ExifInterface getExifByUri(Context context, Uri uri) {
        InputStream in = null;
        ExifInterface exif = null;
        try {
            in = context.getContentResolver().openInputStream(uri);
            exif = new ExifInterface(in);
        } catch (IOException e) {
            // Handle any errors
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return exif;
    }

    private static Bitmap rotateCheckExif(ExifInterface exifInterface, Bitmap bitmap){
        String orientString = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ?
                Integer.parseInt(orientString) :
                ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationAngle = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationAngle = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationAngle = 270;
        } else {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > 960 || width > 1280) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > 360
                    && (halfWidth / inSampleSize) > 480) {
                inSampleSize = inSampleSize * 2;
            }
        }
        return inSampleSize;
    }

    public static int calculateInSampleSizeByLength(long fileSize) {
        int inSampleSize = 1;
        while (fileSize > MAX_FILE_SIZE_UPLOAD) {
            inSampleSize = inSampleSize * 2;
            fileSize = fileSize / 4;
        }
        return inSampleSize;
    }

    public static File getCameraOutputFile(Context context) {
        File photoFolder = new File(context.getExternalCacheDir().getAbsolutePath());
        if (!photoFolder.exists()) photoFolder.mkdirs();
        return new File(photoFolder, "photo.jpg");
    }

    public static File getRotateOutputFile(Context context) {
        File photoFolder = new File(context.getExternalCacheDir().getAbsolutePath());
        if (!photoFolder.exists()) photoFolder.mkdirs();
        return new File(photoFolder, "photob.jpg");
    }

    public static Uri getUri(Context context, File outputMediaFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context,
                    /* depend on string in the manifest */
                    context.getApplicationContext().getPackageName() + ".provider", outputMediaFile);
        } else {
            return Uri.fromFile(outputMediaFile);
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && isDocumentURI(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = getDocumentID(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = getDocumentID(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = getDocumentID(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @TargetApi(19)
    private static boolean isDocumentURI(Context context, Uri uri) {
        return DocumentsContract.isDocumentUri(context, uri);
    }

    @TargetApi(19)
    private static String getDocumentID(Uri uri) {
        return DocumentsContract.getDocumentId(uri);
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
