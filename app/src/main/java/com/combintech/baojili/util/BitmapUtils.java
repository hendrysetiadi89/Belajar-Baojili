package com.combintech.baojili.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Created by User on 10/13/2017.
 */

public class BitmapUtils {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    public static int barcodeWidthResult;
    public static int barcodeHeightResult;

    public static Bitmap rotate (Bitmap bitmap, int rotationAngle) throws IOException {
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
                                        int img_width, int img_height) throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        barcodeWidthResult = result.getWidth();
        barcodeHeightResult = result.getHeight();
        int[] pixels = new int[barcodeWidthResult * barcodeHeightResult];
        for (int y = 0; y < barcodeHeightResult; y++) {
            int offset = y * barcodeWidthResult;
            for (int x = 0; x < barcodeWidthResult; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(barcodeWidthResult, barcodeHeightResult,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, barcodeWidthResult, 0, 0, barcodeWidthResult, barcodeHeightResult);
        return bitmap;
    }

    public static Bitmap encodeAsBitmapWithText(String contents, BarcodeFormat format,
                                        int img_width, int img_height) throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    public static Bitmap addStringBelowBarcode(Bitmap bitmap, String stringToAdd, int defWidth, int defHeight, int targetWidth, int targetHeight){
        Paint textPaint = new Paint();
        textPaint.setARGB(255, 0, 0, 0);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize((int)(0.2*defWidth));
        textPaint.setTypeface(Typeface.MONOSPACE);

        Bitmap bitmapWithText = Bitmap.createBitmap(defWidth, defHeight + (int)(0.25*defHeight),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithText);
        canvas.drawColor(0xffffffff);
        canvas.drawBitmap(bitmap, 0,0, null);
        canvas.drawText(stringToAdd, canvas.getWidth()/2, (int)(defHeight * 1.2)  , textPaint);
        return Bitmap.createScaledBitmap(bitmapWithText, targetWidth, targetHeight, false);
    }

    public static Bitmap encodeAsBitmapWithText(String contents, BarcodeFormat format,
                                                int img_width, int img_height, String string) throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contents);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap bitmapCopy = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmapCopy.setPixels(pixels, 0, width, 0, 0, width, height);

        width = 350;
        height = 140;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapCopy, width, height, false);

        Paint textPaint = new Paint();
        textPaint.setARGB(255, 0, 0, 0);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        Bitmap bitmapWithText;
        if (string.length() >=15 && string.contains("-")) {
            bitmapWithText = Bitmap.createBitmap(width, (int) (height * 1.5),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapWithText);
            canvas.drawColor(0xffffffff);
            canvas.drawBitmap(scaledBitmap, 0, 0, null);
            textPaint.setTextSize((int)(0.27*height));
            String[] sSplit = string.split("-");
            canvas.drawText(sSplit[0], canvas.getWidth() / 2, (int) (height * 1.225), textPaint);
            canvas.drawText(sSplit[1], canvas.getWidth() / 2, (int) (height * 1.47), textPaint);
        } else {
            bitmapWithText = Bitmap.createBitmap(width, (int) (height * 1.5),
                    Bitmap.Config.ARGB_8888);
            if (string.length() >= 12) {
                textPaint.setTextSize((int) (0.35 * height));
            } else {
                textPaint.setTextSize((int) (0.4 * height));
            }
            Canvas canvas = new Canvas(bitmapWithText);
            canvas.drawColor(0xffffffff);
            canvas.drawBitmap(scaledBitmap, 0, 0, null);
            canvas.drawText(string, canvas.getWidth() / 2, (int) (height * 1.4), textPaint);
        }

        return bitmapWithText;
    }



}
