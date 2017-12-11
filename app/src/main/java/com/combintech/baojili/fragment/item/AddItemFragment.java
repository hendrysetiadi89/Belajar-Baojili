package com.combintech.baojili.fragment.item;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.AddItemPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.util.ViewUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;
import com.combintech.baojili.view.IDRCurrencyFormatTextWatcher;

import java.io.File;

public class AddItemFragment extends Fragment
        implements AddItemPresenter.AddItemView {

    public static final String TAG = AddItemFragment.class.getSimpleName();

    public static final String SAVED_PHOTO_PATH = "svd_photo_path";
    public static final String SAVED_TYPE_SELECTION = "svd_type";

    private AddItemPresenter addItemPresenter;

    private EditText etDescription;
    private EditText etCode;
    private EditText etSize;
    private EditText etPrice;
    private EditText etCost;
    private View tvProduksiChoice;
    private View tvPasokanChoice;
    private ImageView ivPhoto;
    private String photoPath;
    private TextInputLayout tilCode;
    private TextInputLayout tilSize;

    private String selectionType;

    private OnAddItemFragmentListener onAddItemFragmentListener;
    public interface OnAddItemFragmentListener{
        void onSuccessAddItem();
    }

    public static AddItemFragment newInstance() {

        Bundle args = new Bundle();

        AddItemFragment fragment = new AddItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addItemPresenter = new AddItemPresenter(this);
        if (savedInstanceState == null) {
            photoPath = "";
            selectionType = "";
        } else {
            photoPath = savedInstanceState.getString(SAVED_PHOTO_PATH);
            selectionType = savedInstanceState.getString(SAVED_TYPE_SELECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        ivPhoto = view.findViewById(R.id.iv_item_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImageFromFragment.newInstance()
                        .show(getActivity().getSupportFragmentManager(), ChooseImageFromFragment.TAG);
            }
        });

        tvProduksiChoice = view.findViewById(R.id.tv_produksi_choice);
        tvPasokanChoice = view.findViewById(R.id.tv_pasokan_choice);
        tilCode = view.findViewById(R.id.tilCode);
        etCode = tilCode.getEditText();
        tilSize = view.findViewById(R.id.tilSize);
        etSize = tilSize.getEditText();
        final TextInputLayout tilDescription = view.findViewById(R.id.tilDescription);
        etDescription = tilDescription.getEditText();
        final TextInputLayout tilPrice = view.findViewById(R.id.tilPrice);
        etPrice = tilPrice.getEditText();
        etPrice.addTextChangedListener(new IDRCurrencyFormatTextWatcher(etPrice));

        final TextInputLayout tilCost = view.findViewById(R.id.tilCost);
        etCost = tilCost.getEditText();
        etCost.addTextChangedListener(new IDRCurrencyFormatTextWatcher(etCost));

        tvProduksiChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvProduksiChoice.setSelected(true);
                tvPasokanChoice.setSelected(false);
            }
        });
        tvPasokanChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvProduksiChoice.setSelected(false);
                tvPasokanChoice.setSelected(true);
            }
        });
        if (selectionType.equals(NetworkConstant.PRODUKSI)) {
            tvProduksiChoice.setSelected(true);
            tvPasokanChoice.setSelected(false);
        } else if (selectionType.equals(NetworkConstant.PASOKAN)) {
            tvProduksiChoice.setSelected(false);
            tvPasokanChoice.setSelected(true);
        }

        View buttonAdd = view.findViewById(R.id.btn_add);

        loadImageFromPhotoPath();

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

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonAddClicked();
            }
        });
        view.requestFocus();

        return view;
    }

    private void onButtonAddClicked() {
        ViewUtil.hideKeyboard(getActivity());
        if (!tvProduksiChoice.isSelected() && !tvPasokanChoice.isSelected()) {
            ErrorUtil.showToast(getContext(), getString(R.string.type_produksi_pasokan_must_be_selected));
            return;
        }
        String type = tvProduksiChoice.isSelected()? NetworkConstant.PRODUKSI: NetworkConstant.PASOKAN;

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
        String description = etDescription.getText().toString();
        String price = StringUtil.removeAllNonNumeric(etPrice.getText().toString());
        String cost = StringUtil.removeAllNonNumeric(etCost.getText().toString());

        ((BaseActivity) getActivity()).showProgressDialog();
        if (TextUtils.isEmpty(photoPath)) {
            addItemPresenter.addItem(description, type, code, size, price, cost, null);
        } else { // has photo
            addItemPresenter.addItem(description, type, code, size, price, cost, photoPath);
        }
    }

    public void onGalleryActivityResultOK(String photoPath) {
        if (TextUtils.isEmpty(photoPath)) {
            return;
        }
        this.photoPath = photoPath;
        loadImageFromPhotoPath();
    }

    @Override
    public void onResume() {
        super.onResume();
        addItemPresenter.onResume();
    }

    private void loadImageFromPhotoPath() {
        if (TextUtils.isEmpty(photoPath)) {
            ivPhoto.setImageResource(R.drawable.no_image);
        } else {
            File f = new File(photoPath);
            if (f.exists()) {
                ImageHelper.loadBitmapFitCenter(getContext(), f.getAbsolutePath(), ivPhoto);
            } else {
                ImageHelper.loadImageFitCenter(getContext(), photoPath, ivPhoto);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        addItemPresenter.onPause();
    }

    @Override
    public void onErrorAddItem(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessAddItem(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        onAddItemFragmentListener.onSuccessAddItem();
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
        onAddItemFragmentListener = (OnAddItemFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_PHOTO_PATH, photoPath);
        outState.putString(SAVED_TYPE_SELECTION,
                tvProduksiChoice.isSelected() ?
                NetworkConstant.PRODUKSI:
                        tvPasokanChoice.isSelected()? NetworkConstant.PASOKAN: "");
    }
}
