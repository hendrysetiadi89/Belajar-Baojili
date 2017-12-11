package com.combintech.baojili.fragment.location;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.AddItemPresenter;
import com.combintech.baojili.presenter.AddLocationPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.util.ViewUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;
import com.combintech.baojili.view.IDRCurrencyFormatTextWatcher;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.combintech.baojili.R.id.etCode;
import static com.combintech.baojili.R.id.tilCode;

public class AddLocationFragment extends Fragment implements AddLocationPresenter.AddLocationView {

    public static final String TAG = AddLocationFragment.class.getSimpleName();

    public static final String SAVED_TYPE_SELECTION = "svd_type";
    public static final String SAVED_LAT = "svd_lat";
    public static final String SAVED_LON = "svd_lon";

    private AddLocationPresenter addLocationPresenter;

    private String selectionType;
    private double lat;
    private double lon;

    private TextInputLayout tilLocationName;
    private EditText etLocationName;

    private ViewGroup vgTypeChoice;

    private TextInputLayout tilAddress;
    private EditText etAddress;

    private OnAddLocationFragmentListener onAddLocationFragmentListener;

    public interface OnAddLocationFragmentListener {
        void openPlacePicker();
        void onSuccessAddLocation();
        void setMapAsync(SupportMapFragment supportMapFragment);
    }

    public static AddLocationFragment newInstance() {

        Bundle args = new Bundle();

        AddLocationFragment fragment = new AddLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addLocationPresenter = new AddLocationPresenter(this);
        if (savedInstanceState == null) {
            lat = 0f;
            lon = 0f;
            selectionType = "";
        } else {
            lat = savedInstanceState.getDouble(SAVED_LAT);
            lon = savedInstanceState.getDouble(SAVED_LON);
            selectionType = savedInstanceState.getString(SAVED_TYPE_SELECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);

        tilLocationName = view.findViewById(R.id.til_location_name);
        etLocationName = tilLocationName.getEditText();

        vgTypeChoice = view.findViewById(R.id.vg_type_choice);
        View.OnClickListener typeChoiceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTypeSelection();
                view.setSelected(true);
                switch (view.getId()) {
                    case R.id.tv_pabrik_choice:
                        selectionType = NetworkConstant.PABRIK;
                        break;
                    case R.id.tv_pemasok_choice:
                        selectionType = NetworkConstant.PEMASOK;
                        break;
                    case R.id.tv_gudang_choice:
                        selectionType = NetworkConstant.GUDANG;
                        break;
                    case R.id.tv_toko_choice:
                        selectionType = NetworkConstant.TOKO;
                        break;
                }
            }
        };
        for (int i = 0, sizei = vgTypeChoice.getChildCount(); i < sizei; i++) {
            View vType = vgTypeChoice.getChildAt(i);
            vType.setOnClickListener(typeChoiceClickListener);
        }

        switch (selectionType) {
            case NetworkConstant.PABRIK:
                vgTypeChoice.findViewById(R.id.tv_pabrik_choice).performClick();
                break;
            case NetworkConstant.PEMASOK:
                vgTypeChoice.findViewById(R.id.tv_pemasok_choice).performClick();
                break;
            case NetworkConstant.GUDANG:
                vgTypeChoice.findViewById(R.id.tv_gudang_choice).performClick();
                break;
            case NetworkConstant.TOKO:
                vgTypeChoice.findViewById(R.id.tv_toko_choice).performClick();
                break;
        }

        tilAddress = view.findViewById(R.id.til_address);
        etAddress = tilAddress.getEditText();

        etLocationName.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilLocationName.setErrorEnabled(false);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        onAddLocationFragmentListener.setMapAsync(mapFragment);

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

    public void onLocationMapMoved(double lat, double lng, String placeAddress) {
        this.lat = lat;
        this.lon = lng;
        etAddress.setText(placeAddress);
    }

    public void onCurrentLocationChanged(double lat, double lng, String placeAddress) {
        this.lat = lat;
        this.lon = lng;
        etAddress.setText(placeAddress);
    }

    private void clearTypeSelection() {
        for (int i = 0, sizei = vgTypeChoice.getChildCount(); i < sizei; i++) {
            vgTypeChoice.getChildAt(i).setSelected(false);
        }
    }

    public void onLocationPicked(String placeId, String placeName, String placeAddress,
                                 double placeLat, double placeLng) {
        this.lat = placeLat;
        this.lon = placeLng;
        etAddress.setText(placeAddress);
    }

    private void onButtonAddClicked() {
        ViewUtil.hideKeyboard(getActivity());

        String locationName = etLocationName.getText().toString();
        if (TextUtils.isEmpty(locationName)) {
            tilLocationName.setError(getString(R.string.location_name_must_be_filled));
            return;
        }

        if (TextUtils.isEmpty(selectionType)) {
            ErrorUtil.showToast(getContext(), getString(R.string.type_location_must_be_selected));
            return;
        }

        String address = etAddress.getText().toString();

        ((BaseActivity) getActivity()).showProgressDialog();
        addLocationPresenter.addLocation(locationName,
                selectionType,
                address,
                lat == 0 ? "" : String.valueOf(lat),
                lon == 0 ? "" : String.valueOf(lon));
    }

    @Override
    public void onResume() {
        super.onResume();
        addLocationPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        addLocationPresenter.onPause();
    }

    @Override
    public void onErrorAddLocation(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessAddLocation(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        onAddLocationFragmentListener.onSuccessAddLocation();
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
        onAddLocationFragmentListener = (OnAddLocationFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_TYPE_SELECTION, selectionType);
        outState.putDouble(SAVED_LAT, lat);
        outState.putDouble(SAVED_LON, lon);
    }
}
