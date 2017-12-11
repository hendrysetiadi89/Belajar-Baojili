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

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.AddMemberPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.StringUtil;
import com.combintech.baojili.util.ViewUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;

import java.io.File;

import static com.combintech.baojili.R.id.etDescription;

public class AddUserFragment extends Fragment implements AddMemberPresenter.AddMemberView {

    public static final String TAG = AddUserFragment.class.getSimpleName();

    public static final String SAVED_PHOTO_PATH = "svd_photo_path";
    public static final String SAVED_TYPE_SELECTION = "svd_type";

    private AddMemberPresenter addMemberPresenter;

    private EditText etUsername;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etRepeatPassword;
    private View tvManajerChoice;
    private View tvKaryawanChoice;
    private TextInputLayout tilUsername;

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputLayout tilRepeatPassword;
    private TextInputLayout tilName;

    private ImageView ivPhoto;
    private String photoPath;

    private String selectionType;

    private OnAddUserFragmentListener onAddUserFragmentListener;
    public interface OnAddUserFragmentListener {
        void onSuccessAddUser();
    }

    public static AddUserFragment newInstance() {

        Bundle args = new Bundle();

        AddUserFragment fragment = new AddUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addMemberPresenter = new AddMemberPresenter(this);
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
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);

        ivPhoto = view.findViewById(R.id.iv_photo);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImageFromFragment.newInstance()
                        .show(getActivity().getSupportFragmentManager(), ChooseImageFromFragment.TAG);
            }
        });

        tvManajerChoice = view.findViewById(R.id.tv_manajer_choice);
        tvKaryawanChoice = view.findViewById(R.id.tv_karyawan_choice);
        tilUsername = view.findViewById(R.id.til_username);
        etUsername = tilUsername.getEditText();

        tilName = view.findViewById(R.id.til_name);
        etName = tilName.getEditText();

        tilEmail = view.findViewById(R.id.til_email);
        etEmail = tilEmail.getEditText();

        tilPassword = view.findViewById(R.id.til_password);
        etPassword = tilPassword.getEditText();

        tilRepeatPassword = view.findViewById(R.id.til_repeat_password);
        etRepeatPassword = tilRepeatPassword.getEditText();

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

        View buttonAdd = view.findViewById(R.id.btn_add);

        loadImageFromPhotoPath();

        etUsername.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilUsername.setErrorEnabled(false);
            }
        });
        etEmail.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilEmail.setErrorEnabled(false);
            }
        });
        etPassword.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilPassword.setErrorEnabled(false);
            }
        });
        etRepeatPassword.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilRepeatPassword.setErrorEnabled(false);
            }
        });
        etName.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                tilName.setErrorEnabled(false);
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
        if (!tvManajerChoice.isSelected() && !tvKaryawanChoice.isSelected()) {
            ErrorUtil.showToast(getContext(), getString(R.string.role_must_be_selected));
            return;
        }
        String type = tvManajerChoice.isSelected() ? NetworkConstant.MANAJER : NetworkConstant.KARYAWAN;

        String userName = etUsername.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            tilUsername.setError(getString(R.string.username_must_be_filled));
            return;
        }
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
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.password_must_be_filled));
            return;
        }
        String repeatPassword = etRepeatPassword.getText().toString();
        if (TextUtils.isEmpty(repeatPassword)) {
            tilRepeatPassword.setError(getString(R.string.repeat_password_must_be_filled));
            return;
        }
        if (!password.equals(repeatPassword)) {
            tilRepeatPassword.setError(getString(R.string.repeat_password_not_match));
            return;
        }

        ((BaseActivity) getActivity()).showProgressDialog();
        if (TextUtils.isEmpty(photoPath)) {
            addMemberPresenter.addMember(userName, name, email, password, type, null);
        } else { // has photo
            addMemberPresenter.addMember(userName, name, email, password, type, photoPath);
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
        addMemberPresenter.onResume();
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
        addMemberPresenter.onPause();
    }

    @Override
    public void onErrorAddMember(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessAddMember(MessageResponse messageResponse) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), messageResponse.getMessage());
        onAddUserFragmentListener.onSuccessAddUser();
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
        onAddUserFragmentListener = (OnAddUserFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_PHOTO_PATH, photoPath);
        outState.putString(SAVED_TYPE_SELECTION,
                tvManajerChoice.isSelected() ?
                        NetworkConstant.MANAJER :
                        tvKaryawanChoice.isSelected() ? NetworkConstant.KARYAWAN : "");
    }
}
