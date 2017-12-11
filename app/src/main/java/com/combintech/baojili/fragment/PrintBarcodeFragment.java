package com.combintech.baojili.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.activity.printer.PrintSettingsActivity;
import com.combintech.baojili.activity.printer.SearchActivity;
import com.combintech.baojili.apphelper.LWPrintSampleUtil;
import com.combintech.baojili.apphelper.PreferenceCodeAndSizeHelper;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.network.exception.RetrofitException;
import com.combintech.baojili.presenter.GetItemListPresenter;
import com.combintech.baojili.printer.BarcodeDataProvider;
import com.combintech.baojili.presenter.PrepareCodeSizeLocPresenter;
import com.combintech.baojili.printer.BarcodeImageDataProvider;
import com.combintech.baojili.printer.ContentsData;
import com.combintech.baojili.printer.LWPrintContentsXmlParser;
import com.combintech.baojili.printer.Logger;
import com.combintech.baojili.printer.QRCodeDataProvider;
import com.combintech.baojili.util.BitmapUtils;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;
import com.combintech.baojili.view.ContainArrayAdapter;
import com.combintech.baojili.view.ZeroThresAutoCompleteTextView;
import com.epson.lwprint.sdk.LWPrint;
import com.epson.lwprint.sdk.LWPrintCallback;
import com.epson.lwprint.sdk.LWPrintDataProvider;
import com.epson.lwprint.sdk.LWPrintDiscoverPrinter;
import com.epson.lwprint.sdk.LWPrintParameterKey;
import com.epson.lwprint.sdk.LWPrintPrintingPhase;
import com.google.zxing.BarcodeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class PrintBarcodeFragment extends Fragment implements PrepareCodeSizeLocPresenter.PrepareCodeSizeLocView, LWPrintCallback, ZeroThresAutoCompleteTextView.OnItemAutoCompleteClickListener, GetItemListPresenter.GetItemListView {

    public static final String TAG = PrintBarcodeFragment.class.getSimpleName();

    private static final int REQUEST_ACTIVITY_SEARCH = 1;
    private static final int REQUEST_ACTIVITY_PRINT_SETTINGS = 2;

    private PrepareCodeSizeLocPresenter prepareCodeSizeLocPresenter;
    private GetItemListPresenter getItemListPresenter;

    ContainArrayAdapter<String> codeAutoCompleteAdapter;
    ContainArrayAdapter<String> sizeAutoCompleteAdapter;
    private View btnSearchPrinter;
    private TextView tvSelectedPrinter;
//    private View btnPrinterSetting;
//    private TextView tvSelectedSetting;

    Map<String, String> _printerInfo = null;
    Map<String, Integer> _lwStatus = null;

//    private SignInPresenter signInPresenter;

//    OnSignInFragmentListener onSignInFragmentListener;
//    public interface OnSignInFragmentListener{
//        void onSignInResponseSuccess();
//    }

    Timer timer;

    private ProgressDialog progressDialog;
    private ZeroThresAutoCompleteTextView etCode;
    private ZeroThresAutoCompleteTextView etSize;
    private TextView tvResult;
    private String itemIDcheckResult;

    private LWPrint lwprint;

    private Handler handler = new Handler();
    private Handler resultHandler = new Handler() {
        public void handleMessage(Message msg) {
            tvResult.setText((String) msg.obj);
            if (tvResult.getVisibility() != View.VISIBLE) {
                tvResult.setVisibility(View.VISIBLE);
            }
        }
    };
    private LWPrintDataProvider provider;
    private ImageView ivItemPhoto;
    private TextView tvItemDescription;
    private View btnPrint;
    private AfterTextTextWatcher resetItemTextWatcher;
    private EditText etCopy;
    private int copyInt;

    public static PrintBarcodeFragment newInstance() {

        Bundle args = new Bundle();

        PrintBarcodeFragment fragment = new PrintBarcodeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareCodeSizeLocPresenter = new PrepareCodeSizeLocPresenter(this);
        getItemListPresenter = new GetItemListPresenter(this);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        List<String> codeList = PreferenceCodeAndSizeHelper.getMsCode();
        codeAutoCompleteAdapter = new ContainArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                codeList == null ? new ArrayList<String>() : codeList);
        List<String> sizeList = PreferenceCodeAndSizeHelper.getMsSize();
        sizeAutoCompleteAdapter = new ContainArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                sizeList == null ? new ArrayList<String>() : sizeList);

        lwprint = new LWPrint(getContext());
        lwprint.setCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print_barcode, container, false);

        btnSearchPrinter = view.findViewById(R.id.btn_search_printer);
        tvSelectedPrinter = view.findViewById(R.id.tv_selected_printer);

//        btnPrinterSetting = view.findViewById(R.id.btn_printer_setting);
//        tvSelectedSetting = view.findViewById(R.id.tv_selected_setting);

        TextInputLayout tilCode = view.findViewById(R.id.til_code);
        etCode = (ZeroThresAutoCompleteTextView) tilCode.getEditText();
        TextInputLayout tilSize = view.findViewById(R.id.til_size);
        etSize = (ZeroThresAutoCompleteTextView) tilSize.getEditText();
        etCode.setAdapter(codeAutoCompleteAdapter);
        etSize.setAdapter(sizeAutoCompleteAdapter);
        etCode.setOnItemAutoCompleteClickListener(this);
        etSize.setOnItemAutoCompleteClickListener(this);
        resetItemTextWatcher = new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                resetItem();
            }
        };
        etCode.addTextChangedListener(resetItemTextWatcher);
        etSize.addTextChangedListener(resetItemTextWatcher);

        etCopy = view.findViewById(R.id.et_copy);
        if (savedInstanceState == null) {
            etCopy.setText(String.valueOf(LWPrintSampleUtil.getCopies()));
        }

        View btnCheckBarang = view.findViewById(R.id.btn_item_check);
        btnCheckBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCheckItem();
            }
        });
        ivItemPhoto = view.findViewById(R.id.iv_item_photo);
        tvItemDescription = view.findViewById(R.id.tv_item_description);

        btnPrint = view.findViewById(R.id.btn_print);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPrintButtonClicked();
            }
        });

        View.OnClickListener searchPrinterOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivityForResult(intent, REQUEST_ACTIVITY_SEARCH);
            }
        };
        btnSearchPrinter.setOnClickListener(searchPrinterOnClickListener);
        tvSelectedPrinter.setOnClickListener(searchPrinterOnClickListener);

//        View.OnClickListener printerSettingOnClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), PrintSettingsActivity.class);
//                startActivityForResult(intent, REQUEST_ACTIVITY_PRINT_SETTINGS);
//            }
//        };
//        btnPrinterSetting.setOnClickListener(printerSettingOnClickListener);
//        tvSelectedSetting.setOnClickListener(printerSettingOnClickListener);

        tvResult = view.findViewById(R.id.tv_result);
        tvResult.setVisibility(View.GONE);

        view.requestFocus();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACTIVITY_SEARCH:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        if (_printerInfo != null) {
                            _printerInfo.clear();
                            _printerInfo = null;
                        }
                        _printerInfo = new HashMap<>();
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_NAME,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_NAME));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_PRODUCT));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_USBMDL));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_HOST,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_HOST));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_PORT,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_PORT));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_TYPE));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DOMAIN));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_SERIAL_NUMBER));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_CLASS));
                        _printerInfo
                                .put(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS,
                                        extras.getString(LWPrintDiscoverPrinter.PRINTER_INFO_DEVICE_STATUS));
                        tvSelectedPrinter.setText(extras.getString("name"));
                    }
                }
                break;
            case REQUEST_ACTIVITY_PRINT_SETTINGS:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    LWPrintSampleUtil.savePrefFromBundleActResult(extras);
                }
                break;
            default:
                break;
        }
    }

    private void onPrintButtonClicked() {
        if (_printerInfo == null) {
            Toast.makeText(getActivity(),
                    "Printer belum terpilih", Toast.LENGTH_SHORT).show();
            return;
        }
        String copyStr = etCopy.getText().toString();
        if (TextUtils.isEmpty(copyStr)) {
            Toast.makeText(getActivity(),
                    "Jumlah(copy) harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        copyInt = 0;
        try {
            copyInt = Integer.parseInt(copyStr);
        }
        catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Jumlah/Copy bukan angka", Toast.LENGTH_SHORT).show();
            return;
        }
        if (copyInt < 1 || copyInt > 99) {
            Toast.makeText(getActivity(),
                    "Jumlah/Copy harus di antara 1 sampai 99", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            ErrorUtil.showToast(getContext(), getString(R.string.code_must_be_filled));
            return;
        }
        String size = etSize.getText().toString();
        if (TextUtils.isEmpty(size)) {
            ErrorUtil.showToast(getContext(), getString(R.string.size_must_be_filled));
            return;
        }
        if (TextUtils.isEmpty(itemIDcheckResult)) {
            ErrorUtil.showToast(getContext(), getString(R.string.make_sure_product_is_available_using_check));
            return;
        }

        setEnabledButtons(false);
        performPrint(code, size);
    }

    private void setEnabledButtons(final boolean isEnabled) {
        handler.postDelayed(new Runnable() {
            public void run() {
                btnSearchPrinter.setEnabled(isEnabled);
//                btnPrinterSetting.setEnabled(isEnabled);
                tvSelectedPrinter.setEnabled(isEnabled);
//                tvSelectedSetting.setEnabled(isEnabled);
                btnPrint.setEnabled(isEnabled);
                if (isEnabled) {
                    etCode.addTextChangedListener(resetItemTextWatcher);
                    etSize.addTextChangedListener(resetItemTextWatcher);
                } else {
                    etCode.removeTextChangedListener(resetItemTextWatcher);
                    etSize.removeTextChangedListener(resetItemTextWatcher);
                    etCode.setText(null);
                    etSize.setText(null);
                }
            }
        }, 1);
    }

    private void printDone(){
        Message msg = new Message();
        msg.obj = "Print Selesai";
        resultHandler.sendMessage(msg);
    }

    private void performPrint(final String code, final String size) {
        Message msg = new Message();
        msg.obj = "";
        resultHandler.sendMessage(msg);

        showProgressDialog();

        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                lwprint.setPrinterInformation(_printerInfo);
                if (_lwStatus == null) {
                    _lwStatus = lwprint.fetchPrinterStatus();
                }

                int deviceError = lwprint.getDeviceErrorFromStatus(_lwStatus);
                int tapeWidth = lwprint.getTapeWidthFromStatus(_lwStatus);

                provider = new BarcodeImageDataProvider(getContext(), code, size, itemIDcheckResult);

                LWPrintSampleUtil.saveCopies(copyInt);
                Map<String, Object> printParameter = LWPrintSampleUtil.getPrintParameter();
                printParameter.put(LWPrintParameterKey.TapeWidth, tapeWidth);

                lwprint.doPrint(provider, printParameter);

                return null;
            }
        }.execute();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask task = (new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        float progress = lwprint.getProgressOfPrint();
                        if (progressDialog != null) {
                            Log.i("BarcodeP", "setProgress " + progress);
                            progressDialog.setProgress((int) (progress * 100));
                        }
                        int printingPage = lwprint.getPageNumberOfPrinting();
                        // textPrintingPage.setText(String.valueOf(printingPage));
                    }
                });
            }
        });
        timer.schedule(task, 1000, 1000);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            createProgressDialogForPrinting();
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
            }
        });
    }

    private void createProgressDialogForPrinting() {
        Log.i("BarcodeP", "createProgressDialogForPrinting");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Mencetak...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.incrementProgressBy(0);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Batal",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Logger.d(TAG, "Cancel onClick()");
                        progressDialog.setProgress(0);
                        setEnabledButtons(true);
                        dialog.cancel();
                    }
                });
        progressDialog
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        //Logger.d(TAG, "Cancel onCancel()");
                        setEnabledButtons(true);
                        doCancel();
                    }
                });
    }

    private void doCancel() {
        lwprint.cancelPrint();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.setProgress(0);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareCodeSizeLocPresenter.onResume();
        if (PreferenceCodeAndSizeHelper.isExpired()) {
            prepareCodeSizeLocPresenter.getItemCodeSizeLocList(null);
        }
        hideProgressDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        prepareCodeSizeLocPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @TargetApi(23)
    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public final void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
//        onSignInFragmentListener = (OnSignInFragmentListener) context;
    }

    @Override
    public void onErrorPrepareCodeSizeLocView(Throwable throwable) {
        // no op, since it is just autocomplete
    }

    @Override
    public void onSuccessPrepareCodeSizeLocView(String callerIdentifier) {
        codeAutoCompleteAdapter.clear();
        List<String> codeList = PreferenceCodeAndSizeHelper.getMsCode();
        if (codeList != null) {
            codeAutoCompleteAdapter.addAll(codeList);
            codeAutoCompleteAdapter.notifyDataSetChanged();
        }

        sizeAutoCompleteAdapter.clear();
        List<String> sizeList = PreferenceCodeAndSizeHelper.getMsSize();
        if (sizeList != null) {
            sizeAutoCompleteAdapter.addAll(sizeList);
            sizeAutoCompleteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onChangePrintOperationPhase(LWPrint lwPrint, int phase) {
        String jobPhase = "";
        switch (phase) {
            case LWPrintPrintingPhase.Prepare:
                Log.i("BarcodeP", "onChangePrintOperationPhase Prepare");
                jobPhase = "Memulai Printing";
                break;
            case LWPrintPrintingPhase.Processing:
                jobPhase = "Pemrosesan Printing";
                Log.i("BarcodeP", "onChangePrintOperationPhase Processing");
                break;
            case LWPrintPrintingPhase.WaitingForPrint:
                jobPhase = "Menunggu untuk print";
                Log.i("BarcodeP", "onChangePrintOperationPhase WaitingForPrint");
                break;
            case LWPrintPrintingPhase.Complete:
                jobPhase = "Print Selesai";
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                hideProgressDialog();
                setEnabledButtons(true);
                Log.i("BarcodeP", "onChangePrintOperationPhase Done");
                printDone();
                break;
            default:
                Log.i("BarcodeP", "onChangePrintOperationPhase default");
                hideProgressDialog();
                setEnabledButtons(true);
                break;
        }
        Message msg = new Message();
        msg.obj = jobPhase;
        resultHandler.sendMessage(msg);
    }

    @Override
    public void onSuspendPrintOperation(LWPrint lwPrint, int errorStatus,
                                        int deviceStatus) {
        hideProgressDialog();

        String message = "Error Status : " + errorStatus
                + "\nDevice Status : " + Integer.toHexString(deviceStatus);
        Log.i("BarcodeP", "suspend " + message);
        Message msg = new Message();
        msg.obj = message;
        resultHandler.sendMessage(msg);

        alertSuspendPrintOperation("Print Error! re-print ?", message);
    }

    private void alertSuspendPrintOperation(final String title,
                                            final String message) {
        handler.postDelayed(new Runnable() {
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());
                alert.setTitle(title);
                alert.setMessage(message);
                alert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showProgressDialog();
                                lwprint.resumeOfPrint();
                            }
                        });
                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                lwprint.cancelPrint();
                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }, 1);
    }

    @Override
    public void onAbortPrintOperation(LWPrint lwPrint, int errorStatus,
                                      int deviceStatus) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        hideProgressDialog();

        String message = "Error Status : " + errorStatus
                + "\nDevice Status : " + Integer.toHexString(deviceStatus);
        Log.i("BarcodeP", "abort " + message);
        Message msg = new Message();
        msg.obj = message;
        resultHandler.sendMessage(msg);

        alertAbortOperation("Print Error!", message);
    }
    private void alertAbortOperation(final String title, final String message) {
        handler.postDelayed(new Runnable() {
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        getActivity());
                alert.setTitle(title);
                alert.setMessage(message);
                alert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                lwprint.cancelPrint();
                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        }, 1);
    }


    @Override
    public void onChangeTapeFeedOperationPhase(LWPrint lwPrint, int phase) {
        String jobPhase = "";
        switch (phase) {
            case LWPrintPrintingPhase.Prepare:
                jobPhase = "Mempersiapkan Printing";
                break;
            case LWPrintPrintingPhase.Processing:
                jobPhase = "Memproses Printing";
                break;
            case LWPrintPrintingPhase.WaitingForPrint:
                jobPhase = "Menunggu Printing";
                break;
            case LWPrintPrintingPhase.Complete:
                jobPhase = "Print Selesai";
                setEnabledButtons(true);
                break;
            default:
                setEnabledButtons(true);
                break;
        }
        Message msg = new Message();
        msg.obj = jobPhase;
        resultHandler.sendMessage(msg);
    }

    @Override
    public void onAbortTapeFeedOperation(LWPrint lwPrint, int errorStatus,
                                         int deviceStatus) {
        setEnabledButtons(true);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        String message = "Error Status : " + errorStatus
                + "\nDevice Status : " + Integer.toHexString(deviceStatus);
        Message msg = new Message();
        msg.obj = message;
        resultHandler.sendMessage(msg);

        alertAbortOperation("Tape Feed Error!", message);
    }


    @Override
    public void onAutoCompleteClickListener() {
        onCheckItem();
    }

    private void onCheckItem(){
        String code = etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            resetItem();
            return;
        }
        String size = etSize.getText().toString();
        if (TextUtils.isEmpty(size)) {
            resetItem();
            return;
        }
        ((BaseActivity)getActivity()).showProgressDialog();
        getItemListPresenter.getItemList("", size, code);
    }

    private void resetItem(){
        itemIDcheckResult = "";
        ivItemPhoto.setVisibility(View.GONE);
        tvItemDescription.setVisibility(View.GONE);
    }

    @Override
    public void onErrorGetItemList(Throwable throwable) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        itemIDcheckResult = "";
        tvItemDescription.setText(ErrorUtil.getMessageFromException(throwable));
        tvItemDescription.setVisibility(View.VISIBLE);
        ivItemPhoto.setVisibility(View.GONE);
    }

    @Override
    public void onSuccessGetItemList(ArrayList<BJItem> bjItemList) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        itemIDcheckResult = bjItemList.get(0).getItemID();
        String description = bjItemList.get(0).getDescription();
        if (TextUtils.isEmpty(description)) {
            tvItemDescription.setVisibility(View.GONE);
        } else {
            tvItemDescription.setText(description);
            tvItemDescription.setVisibility(View.VISIBLE);
        }

        String image = bjItemList.get(0).getPhoto();
        if (TextUtils.isEmpty(image)) {
            ivItemPhoto.setVisibility(View.GONE);
        } else {
            ImageHelper.loadImageFitCenter(getContext(), image, ivItemPhoto);
            ivItemPhoto.setVisibility(View.VISIBLE);
        }
    }
}
