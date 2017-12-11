package com.combintech.baojili.fragment.item;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.Image;
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
import com.combintech.baojili.activity.item.UpdateItemActivity;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.UpdateItemPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.util.ViewUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;
import com.combintech.baojili.view.IDRCurrencyFormatTextWatcher;

import java.io.File;

public class UpdateItemFragment extends Fragment implements UpdateItemPresenter.UpdateItemView {

    public static final String TAG = UpdateItemFragment.class.getSimpleName();

    public static final String SAVED_IS_IMAGE_CHANGED = "svd_img_chg";
    public static final String SAVED_PHOTO_PATH = "svd_photo_path";
    public static final String SAVED_TYPE_SELECTION = "svd_type";

    private UpdateItemPresenter updateItemPresenter;

    private EditText etDescription;
    private EditText etCode;
    private EditText etSize;
    private EditText etPrice;
    private EditText etCost;
    private View tvProduksiChoice;
    private View tvPasokanChoice;
    private ImageView ivPhoto;

    private TextInputLayout tilCode;
    private TextInputLayout tilSize;

    private boolean isImageChanged;
    private String photoPath;

    private BJItem bjItemToUpdate;
    private String selectionType;

    private OnUpdateItemFragmentListener onUpdateItemFragmentListener;
    public interface OnUpdateItemFragmentListener{
        void onSuccessUpdateItem();
    }

    public static UpdateItemFragment newInstance() {

        Bundle args = new Bundle();

        UpdateItemFragment fragment = new UpdateItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bjItemToUpdate = getActivity().getIntent().getParcelableExtra(UpdateItemActivity.EXTRA_ITEM);

        updateItemPresenter = new UpdateItemPresenter(this);
        if (savedInstanceState == null) {
            isImageChanged = false;
            photoPath = bjItemToUpdate.getPhoto();
            selectionType = bjItemToUpdate.getType();
        } else {
            isImageChanged = savedInstanceState.getBoolean(SAVED_IS_IMAGE_CHANGED);
            photoPath = savedInstanceState.getString(SAVED_PHOTO_PATH);
            selectionType = savedInstanceState.getString(SAVED_TYPE_SELECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_item, container, false);

        ivPhoto = view.findViewById(R.id.iv_item_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImageFromFragment.newInstance()
                        .show(getActivity().getSupportFragmentManager(), ChooseImageFromFragment.TAG);
            }
        });
        loadImageFromPhotoPath();

        tvProduksiChoice = view.findViewById(R.id.tv_produksi_choice);
        tvPasokanChoice = view.findViewById(R.id.tv_pasokan_choice);
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

        if (savedInstanceState == null) {
            etCode.setText(bjItemToUpdate.getCode());
            etSize.setText(bjItemToUpdate.getSize());
            etDescription.setText(bjItemToUpdate.getDescription());
            etPrice.setText(bjItemToUpdate.getPrice());
            etCost.setText(bjItemToUpdate.getCost());
        } // else: editText has already have savedinstance mechanism

        View buttonChange = view.findViewById(R.id.btn_change);

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

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonChangeClicked();
            }
        });

        view.requestFocus();

        return view;
    }

    private void onButtonChangeClicked() {
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
        if (isImageChanged) {
            updateItemPresenter.updateItem(bjItemToUpdate.getItemID(),
                    description,type,code, size, price, cost, photoPath);
        } else { // image will no change
            updateItemPresenter.updateItem(bjItemToUpdate.getItemID(),
                    description,type,code, size, price, cost, null);
        }
    }

    public void onGalleryActivityResultOK(String photoPath) {
        if (TextUtils.isEmpty(photoPath)) {
            return;
        }
        isImageChanged = true;
        this.photoPath = photoPath;
        loadImageFromPhotoPath();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateItemPresenter.onResume();
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
        updateItemPresenter.onPause();
    }

    @Override
    public void onErrorUpdateItem(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessUpdateItem(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        onUpdateItemFragmentListener.onSuccessUpdateItem();
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
        onUpdateItemFragmentListener = (OnUpdateItemFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_IS_IMAGE_CHANGED, isImageChanged);
        outState.putString(SAVED_PHOTO_PATH, photoPath);
        outState.putString(SAVED_TYPE_SELECTION,
                tvProduksiChoice.isSelected() ?
                        NetworkConstant.PRODUKSI:
                        tvPasokanChoice.isSelected()? NetworkConstant.PASOKAN: "");
    }

}
