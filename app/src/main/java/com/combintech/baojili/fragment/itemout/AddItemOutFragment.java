package com.combintech.baojili.fragment.itemout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import android.widget.Spinner;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.adapter.arrayadapter.LocationArrayAdapter;
import com.combintech.baojili.apphelper.BarcodeActivityHelper;
import com.combintech.baojili.apphelper.PreferenceCodeAndSizeHelper;
import com.combintech.baojili.apphelper.PreferenceLocationHelper;
import com.combintech.baojili.apphelper.PreferenceTorchHelper;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.constant.RequestCode;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.AddItemInPresenter;
import com.combintech.baojili.presenter.AddItemOutPresenter;
import com.combintech.baojili.presenter.GetItemListPresenter;
import com.combintech.baojili.presenter.GetNearestLocationPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.util.ViewUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;
import com.combintech.baojili.view.ContainArrayAdapter;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCaptureActivity;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;
import java.util.List;

public class AddItemOutFragment extends Fragment implements AddItemOutPresenter.AddItemOutView,
        GetItemListPresenter.GetItemListView,
        GetNearestLocationPresenter.GetNearestLocationView {

    public static final String TAG = AddItemOutFragment.class.getSimpleName();

    public static final String SAVED_TYPE_SELECTION = "svd_type";
    public static final String SAVED_SOURCE_LOCATION_ID = "svd_source_loc_id";
    public static final String SAVED_TARGET_LOCATION_ID = "svd_target_loc_id";

    private AddItemOutPresenter addItemOutPresenter;
    private GetItemListPresenter getItemListPresenter;
    private GetNearestLocationPresenter getNearestLocationPresenter;

    private View tvTerjualChoice;
    private View tvCacatChoice;
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

    private OnAddItemOutFragmentListener onAddItemOutFragmentListener;

    ContainArrayAdapter<String> codeAutoCompleteAdapter;
    ContainArrayAdapter<String> sizeAutoCompleteAdapter;

    public interface OnAddItemOutFragmentListener {
        void onSuccessAddItemOut();
        void requestCurrentLocation();
    }

    public static AddItemOutFragment newInstance() {

        Bundle args = new Bundle();

        AddItemOutFragment fragment = new AddItemOutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addItemOutPresenter = new AddItemOutPresenter(this);
        getItemListPresenter = new GetItemListPresenter(this);
        getNearestLocationPresenter = new GetNearestLocationPresenter(this);
        if (savedInstanceState == null) {
            selectionType = "";
            sourceLocationId = "";
            targetLocationId = PreferenceLocationHelper.getMapLocationId();
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
        onAddItemOutFragmentListener.requestCurrentLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item_out, container, false);

        tvTerjualChoice = view.findViewById(R.id.tv_terjual_choice);
        tvCacatChoice = view.findViewById(R.id.tv_cacat_choice);
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
                BarcodeActivityHelper.startBarcodeActivityForResult(AddItemOutFragment.this, getContext());
            }
        });

        tilQuantity = view.findViewById(R.id.til_quantity);
        etQuantity = tilQuantity.getEditText();

        tvTerjualChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTerjualChoice.setSelected(true);
                tvCacatChoice.setSelected(false);
                tvPemindahanChoice.setSelected(false);
                vgTargetLocation.setVisibility(View.GONE);
            }
        });
        tvCacatChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTerjualChoice.setSelected(false);
                tvCacatChoice.setSelected(true);
                tvPemindahanChoice.setSelected(false);
                vgTargetLocation.setVisibility(View.GONE);
            }
        });
        tvPemindahanChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvTerjualChoice.setSelected(false);
                tvCacatChoice.setSelected(false);
                tvPemindahanChoice.setSelected(true);
                vgTargetLocation.setVisibility(View.VISIBLE);
            }
        });
        if (selectionType.equals(NetworkConstant.TERJUAL)) {
            tvTerjualChoice.performClick();
        } else if (selectionType.equals(NetworkConstant.CACAT)) {
            tvCacatChoice.performClick();
        } else if (selectionType.equals(NetworkConstant.PEMINDAHAN)) {
            tvPemindahanChoice.performClick();
        } else {
            vgTargetLocation.setVisibility(View.GONE);
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
        View buttonAdd = view.findViewById(R.id.btn_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonAddClicked();
            }
        });
        view.requestFocus();

        return view;
    }

    public void onLocationChanged(Location location){
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        getNearestLocationPresenter.getNearestLocation(lat, lng);
    }

    private int getSelectedLocationPosition(String locationIdToLookUp) {
        if ("".equals(locationIdToLookUp)) {
            return 0;
        }
        for (int i = 0, sizei = locationArrayList.size(); i < sizei; i++) {
            if (locationArrayList.get(i).getLocationID().equals(locationIdToLookUp)) {
                return i;
            }
        }
        return 0;
    }

    private void onButtonAddClicked() {
        ViewUtil.hideKeyboard(getActivity());
        if (!tvPemindahanChoice.isSelected() && !tvTerjualChoice.isSelected() && !tvCacatChoice.isSelected()) {
            ErrorUtil.showToast(getContext(), getString(R.string.type_terjual_cacat_pemindahan_must_be_selected));
            return;
        }
        String type = tvTerjualChoice.isSelected() ? NetworkConstant.TERJUAL :
                        tvCacatChoice.isSelected() ? NetworkConstant.CACAT :
                        NetworkConstant.PEMINDAHAN;

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
            tilQuantity.requestFocus();
            return;
        }
        if (quantity.equals("0")) {
            tilQuantity.setError(getString(R.string.quantity_must_greater_0));
            tilQuantity.requestFocus();
            return;
        }

        BJLocation sourceLocation = (BJLocation) spinnerSourceLocation.getSelectedItem();
        sourceLocationId = sourceLocation == null ? "" : sourceLocation.getLocationID();
        BJLocation targetLocation = (BJLocation) spinnerTargetLocation.getSelectedItem();
        targetLocationId = targetLocation == null ? "" : targetLocation.getLocationID();

        if (TextUtils.isEmpty(sourceLocationId)) {
            ErrorUtil.showToast(getContext(), getString(R.string.location_name_must_be_filled));
            return;
        }
        if (type.equals(NetworkConstant.TERJUAL) || type.equals(NetworkConstant.CACAT) ) {
            targetLocationId = sourceLocationId;
        }
        if (type.equals(NetworkConstant.PEMINDAHAN)) {
            if (TextUtils.isEmpty(targetLocationId)) {
                ErrorUtil.showToast(getContext(), getString(R.string.location_name_must_be_filled));
                return;
            }
        }
        ((BaseActivity) getActivity()).showProgressDialog();
        addItemOutPresenter.addItemOut(sourceLocationId, targetLocationId, code, size, type, Integer.parseInt(quantity));
    }

    @Override
    public void onResume() {
        super.onResume();
        addItemOutPresenter.onResume();
        getItemListPresenter.onResume();
        getNearestLocationPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        addItemOutPresenter.onPause();
        getItemListPresenter.onPause();
        getItemListPresenter.onResume();
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
                    if (!TextUtils.isEmpty(barcodeString) && barcodeString.length() == 8) {
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
    public void onErrorAddItemOut(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessAddItemOut(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        onAddItemOutFragmentListener.onSuccessAddItemOut();
    }

    @Override
    public void onErrorGetNearestLocationView(Throwable throwable) {
        // no op. it is just for convenience
    }

    @Override
    public void onSuccessGetNearestLocationView(BJLocation bjLocation) {
        sourceLocationId = bjLocation.getLocationID();
        spinnerSourceLocation.setSelection(getSelectedLocationPosition(sourceLocationId));
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
        onAddItemOutFragmentListener = (OnAddItemOutFragmentListener) context;
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
                tvTerjualChoice.isSelected() ? NetworkConstant.TERJUAL :
                        tvCacatChoice.isSelected() ? NetworkConstant.CACAT :
                                tvPemindahanChoice.isSelected() ? NetworkConstant.PEMINDAHAN : "");
        BJLocation sourceLocation = (BJLocation) spinnerSourceLocation.getSelectedItem();
        outState.putString(SAVED_SOURCE_LOCATION_ID, sourceLocation == null ? "" : sourceLocation.getLocationID());
        BJLocation targetLocation = (BJLocation) spinnerTargetLocation.getSelectedItem();
        outState.putString(SAVED_TARGET_LOCATION_ID, targetLocation == null ? "" : targetLocation.getLocationID());
    }
}
