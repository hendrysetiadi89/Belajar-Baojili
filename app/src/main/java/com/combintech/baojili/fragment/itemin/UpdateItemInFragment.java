package com.combintech.baojili.fragment.itemin;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.activity.item.UpdateItemActivity;
import com.combintech.baojili.activity.itemin.UpdateItemInActivity;
import com.combintech.baojili.adapter.arrayadapter.LocationArrayAdapter;
import com.combintech.baojili.apphelper.BarcodeActivityHelper;
import com.combintech.baojili.apphelper.PreferenceCodeAndSizeHelper;
import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.apphelper.PreferenceTorchHelper;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.GetItemListPresenter;
import com.combintech.baojili.presenter.UpdateItemInPresenter;
import com.combintech.baojili.presenter.UpdateItemPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.util.ViewUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;
import com.combintech.baojili.view.ContainArrayAdapter;
import com.combintech.baojili.view.IDRCurrencyFormatTextWatcher;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateItemInFragment extends Fragment implements UpdateItemInPresenter.UpdateItemInView, GetItemListPresenter.GetItemListView {

    public static final String TAG = UpdateItemInFragment.class.getSimpleName();

    public static final String SAVED_TYPE_SELECTION = "svd_type";
    public static final String SAVED_SOURCE_LOCATION_ID = "svd_source_loc_id";
    public static final String SAVED_TARGET_LOCATION_ID = "svd_target_loc_id";

    private UpdateItemInPresenter updateItemInPresenter;
    private GetItemListPresenter getItemListPresenter;

    private ItemInOut itemInOut;

    private View tvPenambahanChoice;
    private View tvPemindahanChoice;

    private View vgSourceLocation;
    private Spinner spinnerSourceLocation;

    private View vgTargetLocation;
    private Spinner spinnerTargetLocation;

    private TextInputLayout tilCode;
    private AutoCompleteTextView etCode;

    private TextInputLayout tilSize;
    private AutoCompleteTextView etSize;

    private ViewGroup vgQrCode;

    private TextInputLayout tilQuantity;
    private EditText etQuantity;

    private String selectionType;
    private String sourceLocationId;
    private String targetLocationId;

    private ArrayList<BJLocation> locationArrayList;
    private LocationArrayAdapter locationArrayAdapter;

    private OnUpdateItemInFragmentListener onUpdateItemInFragmentListener;

    ContainArrayAdapter<String> codeAutoCompleteAdapter;
    ContainArrayAdapter<String> sizeAutoCompleteAdapter;

    public interface OnUpdateItemInFragmentListener{
        void onSuccessUpdateItemIn();
    }

    public static UpdateItemInFragment newInstance() {

        Bundle args = new Bundle();

        UpdateItemInFragment fragment = new UpdateItemInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        itemInOut = getActivity().getIntent().getParcelableExtra(UpdateItemInActivity.EXTRA_TR_ITEM);

        updateItemInPresenter = new UpdateItemInPresenter(this);
        getItemListPresenter = new GetItemListPresenter(this);
        if (savedInstanceState == null) {
            selectionType = itemInOut.getTrType();
            sourceLocationId = itemInOut.getSourceLocationID();
            targetLocationId = itemInOut.getTargetLocationID();
        } else {
            selectionType = savedInstanceState.getString(SAVED_TYPE_SELECTION);
            sourceLocationId = savedInstanceState.getString(SAVED_SOURCE_LOCATION_ID);
            targetLocationId = savedInstanceState.getString(SAVED_TARGET_LOCATION_ID);
        }
        locationArrayList = PreferenceLocationHelper.getMsLocation();
        locationArrayAdapter = new LocationArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                locationArrayList);
        List<String> codeList = PreferenceCodeAndSizeHelper.getMsCode();
        if (codeList!= null && codeList.size() > 0) {
            codeAutoCompleteAdapter = new ContainArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, codeList);
        }
        List<String> sizeList = PreferenceCodeAndSizeHelper.getMsSize();
        if (sizeList!= null && sizeList.size() > 0) {
            sizeAutoCompleteAdapter = new ContainArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, sizeList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_item_in, container, false);

        tvPenambahanChoice = view.findViewById(R.id.tv_penambahan_choice);
        tvPemindahanChoice = view.findViewById(R.id.tv_pemindahan_choice);

        vgSourceLocation = view.findViewById(R.id.vg_source_location);
        spinnerSourceLocation = vgSourceLocation.findViewById(R.id.spinner_source_location);
        spinnerSourceLocation.setAdapter(locationArrayAdapter);

        vgTargetLocation = view.findViewById(R.id.vg_target_location);
        spinnerTargetLocation = vgTargetLocation.findViewById(R.id.spinner_target_location);
        spinnerTargetLocation.setAdapter(locationArrayAdapter);

        spinnerSourceLocation.setSelection(getSelectedLocationPosition(sourceLocationId));
        spinnerTargetLocation.setSelection(getSelectedLocationPosition(targetLocationId));

        tilCode = view.findViewById(R.id.til_code);
        etCode = (AutoCompleteTextView) tilCode.getEditText();
        tilSize = view.findViewById(R.id.til_size);
        etSize = (AutoCompleteTextView) tilSize.getEditText();
        etCode.setAdapter(codeAutoCompleteAdapter);
        etSize.setAdapter(sizeAutoCompleteAdapter);

        vgQrCode = view.findViewById(R.id.vg_qr_code);
        vgQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BarcodeActivityHelper.startBarcodeActivityForResult(UpdateItemInFragment.this, getContext());
            }
        });

        tilQuantity = view.findViewById(R.id.til_quantity);
        etQuantity = tilQuantity.getEditText();

        tvPenambahanChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvPenambahanChoice.setSelected(true);
                tvPemindahanChoice.setSelected(false);
                vgSourceLocation.setVisibility(View.GONE);
            }
        });
        tvPemindahanChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvPenambahanChoice.setSelected(false);
                tvPemindahanChoice.setSelected(true);
                vgSourceLocation.setVisibility(View.VISIBLE);
            }
        });
        if (selectionType.equals(NetworkConstant.PENAMBAHAN)) {
            tvPenambahanChoice.performClick();
        } else if (selectionType.equals(NetworkConstant.PEMINDAHAN)) {
            tvPemindahanChoice.performClick();
        } else {
            vgSourceLocation.setVisibility(View.GONE);
        }

        etCode.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilCode.setErrorEnabled(false);
            }
        });
        etSize.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilSize.setErrorEnabled(false);
            }
        });
        etQuantity.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilQuantity.setErrorEnabled(false);
            }
        });

        if (savedInstanceState == null) {
            etCode.setText(itemInOut.getCode());
            etSize.setText(itemInOut.getSize());
            etQuantity.setText(itemInOut.getQuantity());
        } // else: editText has already have savedinstance mechanism

        View buttonChange = view.findViewById(R.id.btn_change);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonChangeClicked();
            }
        });

        view.requestFocus();

        return view;
    }

    private int getSelectedLocationPosition(String locationIdToLookUp){
        if ("".equals(locationIdToLookUp)) {
            return 0;
        }
        for (int i = 0, sizei = locationArrayList.size(); i<sizei; i++) {
            if (locationArrayList.get(i).getLocationID().equals(locationIdToLookUp)) {
                return i;
            }
        }
        return 0;
    }

    private void onButtonChangeClicked() {
        ViewUtil.hideKeyboard(getActivity());
        if (!tvPemindahanChoice.isSelected() && !tvPenambahanChoice.isSelected()) {
            ErrorUtil.showToast(getContext(), getString(R.string.type_pemindahan_penambahan_must_be_selected));
            return;
        }
        String type = tvPenambahanChoice.isSelected()? NetworkConstant.PENAMBAHAN: NetworkConstant.PEMINDAHAN;

        String code = etCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            tilCode.setError(getString(R.string.code_must_be_filled));
            return;
        }
        String size = etSize.getText().toString();
        if (TextUtils.isEmpty(size)) {
            tilSize.setError(getString(R.string.size_must_be_filled));
            return;
        }
        String quantity = etQuantity.getText().toString();
        if (TextUtils.isEmpty(quantity)) {
            tilQuantity.setError(getString(R.string.quantity_must_be_filled));
            ErrorUtil.showToast(getContext(), getString(R.string.quantity_must_be_filled));
            return;
        }
        if (quantity.equals("0")) {
            tilQuantity.setError(getString(R.string.quantity_must_greater_0));
            ErrorUtil.showToast(getContext(), getString(R.string.quantity_must_greater_0));
            return;
        }

        BJLocation sourceLocation = (BJLocation) spinnerSourceLocation.getSelectedItem();
        sourceLocationId = sourceLocation==null?"":sourceLocation.getLocationID();
        BJLocation targetLocation = (BJLocation) spinnerTargetLocation.getSelectedItem();
        targetLocationId = targetLocation ==null?"":targetLocation.getLocationID();

        if (TextUtils.isEmpty(targetLocationId)) {
            ErrorUtil.showToast(getContext(), getString(R.string.location_name_must_be_filled));
            return;
        }
        if (type.equals(NetworkConstant.PENAMBAHAN)) {
            sourceLocationId = targetLocationId;
        }
        if (type.equals(NetworkConstant.PEMINDAHAN)) {
            if (TextUtils.isEmpty(sourceLocationId)) {
                ErrorUtil.showToast(getContext(), getString(R.string.location_name_must_be_filled));
                return;
            }
        }
        ((BaseActivity) getActivity()).showProgressDialog();
        updateItemInPresenter.updateItemIn(itemInOut.getTrItemID(),
                targetLocationId,sourceLocationId, code, size, type, Integer.parseInt(quantity));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateItemInPresenter.onResume();
        getItemListPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateItemInPresenter.onPause();
        getItemListPresenter.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RequestCode.RC_BARCODE_CAPTURE: {
                if (resultCode == Activity.RESULT_OK) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    boolean useFlash = data.getBooleanExtra(BarcodeCaptureActivity.UseFlash, false);
                    PreferenceTorchHelper.setUseTorch(useFlash);
                    String barcodeString = barcode.displayValue;
                    if (!TextUtils.isEmpty(barcodeString) && barcodeString.length()==8) {
                        ((BaseActivity) getActivity()).showProgressDialog();
                        getItemListPresenter.getItemList(StringUtil.removeLeadingZero(barcodeString.substring(0,7)), "","");
                    }
                }
                break;
            }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onErrorUpdateItemIn(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessUpdateItemIn(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        onUpdateItemInFragmentListener.onSuccessUpdateItemIn();
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
        onUpdateItemInFragmentListener = (OnUpdateItemInFragmentListener) context;
    }

    @Override
    public void onErrorGetItemList(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetItemList(ArrayList<BJItem> bjItemList) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        etCode.setText(bjItemList.get(0).getCode());
        etSize.setText(bjItemList.get(0).getSize());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_TYPE_SELECTION,
                tvPenambahanChoice.isSelected() ?
                        NetworkConstant.PENAMBAHAN:
                        tvPemindahanChoice.isSelected()? NetworkConstant.PEMINDAHAN: "");
        BJLocation sourceLocation = (BJLocation) spinnerSourceLocation.getSelectedItem();
        outState.putString(SAVED_SOURCE_LOCATION_ID, sourceLocation==null?"":sourceLocation.getLocationID());
        BJLocation targetLocation = (BJLocation) spinnerTargetLocation.getSelectedItem();
        outState.putString(SAVED_TARGET_LOCATION_ID, targetLocation==null?"":targetLocation.getLocationID());
    }

}
