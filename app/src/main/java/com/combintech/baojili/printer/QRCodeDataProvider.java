package com.combintech.baojili.printer;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.epson.lwprint.sdk.LWPrintDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 10/12/2017.
 */

public class QRCodeDataProvider implements LWPrintDataProvider {
    private static final String DATA_DIR = "Sample";
    private static final String IMAGE_DIR = "Image";
    private final String code;
    private final String size;

    private static final String QR_CODE = "QRCode";

    private String formName;

    private InputStream _formDataInputStream;

    private List<ContentsData> _contentsData = null;
    private Context context;
    public QRCodeDataProvider(Context context, String code, String size) {
        this.context = context;
        this.code = code;
        this.size = size;
        setFormName("QRCode.plist");
    }

    private void setFormName(String formName) {
        this.formName = formName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.epson.lwprint.sdk.LWPrint.DataProvider#startOfPrint()
     */
    @Override
    public void startOfPrint() {
        // Contents data
        _contentsData = new ArrayList<>();
        ContentsData contentsData = new ContentsData();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(QR_CODE, this.code+"-"+this.size);
        contentsData.setElementMap(hashMap);
        _contentsData.add(contentsData);
        Log.i("BarcodeP", "startOfPrint " + this.code+"-"+this.size);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.epson.lwprint.sdk.LWPrint.DataProvider#endOfPrint()
     */
    @Override
    public void endOfPrint() {
        //Logger.d(TAG, "endOfPrint");
        Log.i("BarcodeP", "end Of Print " + this.code+"-"+this.size);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.epson.lwprint.sdk.LWPrint.DataProvider#startPage()
     */
    @Override
    public void startPage() {
        Log.i("BarcodeP", "start Page");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.epson.lwprint.sdk.LWPrint.DataProvider#endPage()
     */
    @Override
    public void endPage() {
        Log.i("BarcodeP", "end Page");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.epson.lwprint.sdk.LWPrint.DataProvider#getNumberOfPages()
     */
    @Override
    public int getNumberOfPages() {
        if (_contentsData == null) {
            Log.i("BarcodeP", "num page 0");
            return 0;
        } else {
            Log.i("BarcodeP", "num page :" + _contentsData.size());
            return _contentsData.size();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.epson.lwprint.sdk.LWPrint.DataProvider#getFormDataForPage(int)
     */
    @Override
    public InputStream getFormDataForPage(int pageIndex) {
        Log.i("BarcodeP", "getFormDataForPage " + pageIndex);
        if (_formDataInputStream != null) {
            try {
                _formDataInputStream.close();
            } catch (IOException e) {
                Log.i("BarcodeP", "err getformdataforpage " + e.toString());
            }
            _formDataInputStream = null;
        }
        try {
            AssetManager as = context.getResources().getAssets();
            _formDataInputStream = as.open(DATA_DIR + "/" + formName);
            Log.i("BarcodeP", "getformdataforpage " + formName + "success");
        } catch (IOException e) {
            Log.i("BarcodeP", "err getformdataforpage open asset" + e.toString());
        }

        return _formDataInputStream;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.epson.lwprint.sdk.LWPrint.DataProvider#getStringContentData(java
     * .lang .String, int)
     */
    @Override
    public String getStringContentData(String contentName, int pageIndex) {
        Log.i("BarcodeP", "getStringContentData " + pageIndex);
        String content = null;
        if (_contentsData != null) {
            int index = pageIndex - 1;
            ContentsData pageDictionary = _contentsData.get(index);
            content = pageDictionary.get(contentName);
        }
        return content;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.epson.lwprint.sdk.LWPrint.DataProvider#getBitmapContentData(java
     * .lang .String, int)
     */
    @Override
    public Bitmap getBitmapContentData(String contentName, int pageIndex) {
        Log.i("BarcodeP", "getBitmapContentData " + pageIndex);
        Bitmap bitmap = null;
        if (_contentsData != null) {
            int index = pageIndex - 1;
            ContentsData pageDictionary = _contentsData.get(index);
            String content = pageDictionary.get(contentName);
            InputStream is = null;
            try {
                is = context.getResources().getAssets().open(IMAGE_DIR + "/" + content);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
//                Logger.e(TAG, e.toString(), e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                    is = null;
                }
            }
        }
        return bitmap;
    }

}
