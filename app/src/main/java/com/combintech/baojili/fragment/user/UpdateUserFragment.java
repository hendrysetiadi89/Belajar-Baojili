package com.combintech.baojili.fragment.user;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import com.combintech.baojili.activity.user.UpdateUserActivity;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.model.User;
import com.combintech.baojili.presenter.UpdateMemberPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.util.ViewUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;

import java.io.File;

public class UpdateUserFragment extends Fragment
        implements UpdateMemberPresenter.UpdateMemberView {

    public static final String TAG = UpdateUserFragment.class.getSimpleName();

    public static final String SAVED_IS_IMAGE_CHANGED = "svd_img_chg";
    public static final String SAVED_PHOTO_PATH = "svd_photo_path";
    public static final String SAVED_TYPE_SELECTION = "svd_type";

    private UpdateMemberPresenter updateMemberPresenter;

    private EditText etName;
    private EditText etEmail;
    private View tvManajerChoice;
    private View tvKaryawanChoice;
    private ImageView ivPhoto;

    private TextInputLayout tilEmail;
    private TextInputLayout tilName;

    private boolean isImageChanged;
    private String photoPath;
    private String selectionType;

    private User userToUpdate;

    private OnUpdateUserFragmentListener onUpdateUserFragmentListener;
    public interface OnUpdateUserFragmentListener{
        void onSuccessUpdateUser();
    }

    public static UpdateUserFragment newInstance() {

        Bundle args = new Bundle();

        UpdateUserFragment fragment = new UpdateUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userToUpdate = getActivity().getIntent().getParcelableExtra(UpdateUserActivity.EXTRA_USER);

        updateMemberPresenter = new UpdateMemberPresenter(this);
        if (savedInstanceState == null) {
            isImageChanged = false;
            photoPath = userToUpdate.getPhoto();
            selectionType = userToUpdate.getRole();
        } else {
            isImageChanged = savedInstanceState.getBoolean(SAVED_IS_IMAGE_CHANGED);
            photoPath = savedInstanceState.getString(SAVED_PHOTO_PATH);
            selectionType = savedInstanceState.getString(SAVED_TYPE_SELECTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_user, container, false);

        ivPhoto = view.findViewById(R.id.iv_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImageFromFragment.newInstance()
                        .show(getActivity().getSupportFragmentManager(), ChooseImageFromFragment.TAG);
            }
        });
        loadImageFromPhotoPath();

        TextView tvUsername = view.findViewById(R.id.tv_username);
        tvUsername.setText(userToUpdate.getUserID());

        tvManajerChoice = view.findViewById(R.id.tv_manajer_choice);
        tvKaryawanChoice = view.findViewById(R.id.tv_karyawan_choice);

        tilName = view.findViewById(R.id.til_name);
        etName = tilName.getEditText();

        tilEmail = view.findViewById(R.id.til_email);
        etEmail = tilEmail.getEditText();

        tvManajerChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvManajerChoice.setSelected(true);
                tvKaryawanChoice.setSelected(false);
            }
        });
        tvKaryawanChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvManajerChoice.setSelected(false);
                tvKaryawanChoice.setSelected(true);
            }
        });
        if (selectionType.equals(NetworkConstant.MANAJER)) {
            tvManajerChoice.setSelected(true);
            tvKaryawanChoice.setSelected(false);
        } else if (selectionType.equals(NetworkConstant.KARYAWAN)) {
            tvManajerChoice.setSelected(false);
            tvKaryawanChoice.setSelected(true);
        }

        etEmail.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilEmail.setErrorEnabled(false);
            }
        });
        etName.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilName.setErrorEnabled(false);
            }
        });

        if (savedInstanceState == null) {
            etName.setText(userToUpdate.getName());
            etEmail.setText(userToUpdate.getEmail());
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

    private void onButtonChangeClicked() {
        ViewUtil.hideKeyboard(getActivity());
        if (!tvManajerChoice.isSelected() && !tvKaryawanChoice.isSelected()) {
            ErrorUtil.showToast(getContext(), getString(R.string.role_must_be_selected));
            return;
        }
        String type = tvManajerChoice.isSelected() ? NetworkConstant.MANAJER : NetworkConstant.KARYAWAN;

        String name = etName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            tilName.setError(getString(R.string.name_must_be_filled));
            return;
        }
        String email = etEmail.getText().toString();
        if (!TextUtils.isEmpty(email)) {
            if (!StringUtil.isValidEmail(email)) {
                tilEmail.setError(getString(R.string.email_format_is_not_valid));
                return;
            }
        }

        ((BaseActivity) getActivity()).showProgressDialog();
        if (isImageChanged) {
            updateMemberPresenter.updateMember(userToUpdate.getUserID(),
                    name,email,type, photoPath);
        } else { // image will no change
            updateMemberPresenter.updateMember(userToUpdate.getUserID(),
                    name,email,type, null);
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
        updateMemberPresenter.onResume();
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
        updateMemberPresenter.onPause();
    }

    @Override
    public void onErrorUpdateMember(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessUpdateMember(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        onUpdateUserFragmentListener.onSuccessUpdateUser();
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
        onUpdateUserFragmentListener = (OnUpdateUserFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_IS_IMAGE_CHANGED, isImageChanged);
        outState.putString(SAVED_PHOTO_PATH, photoPath);
        outState.putString(SAVED_TYPE_SELECTION,
                tvManajerChoice.isSelected() ?
                        NetworkConstant.MANAJER:
                        tvKaryawanChoice.isSelected()? NetworkConstant.KARYAWAN: "");
    }

}
