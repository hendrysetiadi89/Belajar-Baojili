package com.combintech.baojili.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.dialog.ChangePasswordDialogFragment;
import com.combintech.baojili.dialog.ChooseImageFromFragment;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.presenter.MyInfoPresenter;
import com.combintech.baojili.presenter.UpdateMemberPresenter;
import com.combintech.baojili.presenter.UpdatePasswordPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.view.AfterTextTextWatcher;

import java.io.File;

public class UserInfoFragment extends Fragment implements MyInfoPresenter.GetMyInfoView,
        UpdateMemberPresenter.UpdateMemberView, UpdatePasswordPresenter.UpdatePasswordView {

    public static final String TAG = UserInfoFragment.class.getSimpleName();

    public static final String SAVED_IS_IMAGE_CHANGED = "svd_img_chg";
    public static final String SAVED_PHOTO_PATH = "svd_photo_path";

    private MyInfoPresenter myInfoPresenter;
    private UpdateMemberPresenter updateMemberPresenter;
    private UpdatePasswordPresenter updatePasswordPresenter;

    private View progressBar;
    private View vContent;
    private TextView tvUserName;
    private TextView tvRole;
    private EditText etName;
    private EditText etEmail;
    private ImageView ivPhoto;
    private boolean isImageChanged;
    private String photoPath;

    private OnUserInfoFragmentListener onUserInfoFragmentListener;
    public interface OnUserInfoFragmentListener{
        void onUserChanged();
    }

    public static UserInfoFragment newInstance() {

        Bundle args = new Bundle();

        UserInfoFragment fragment = new UserInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myInfoPresenter = new MyInfoPresenter(this);
        updateMemberPresenter = new UpdateMemberPresenter(this);
        if (savedInstanceState == null) {
            isImageChanged = false;
            photoPath = PreferenceLoginHelper.getPhoto();
        } else {
            isImageChanged = savedInstanceState.getBoolean(SAVED_IS_IMAGE_CHANGED);
            photoPath = savedInstanceState.getString(SAVED_PHOTO_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        vContent = view.findViewById(R.id.vg_content);

        ivPhoto = vContent.findViewById(R.id.iv_profile);
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImageFromFragment.newInstance()
                        .show(getActivity().getSupportFragmentManager(), ChooseImageFromFragment.TAG);
            }
        });

        tvUserName = vContent.findViewById(R.id.tv_username);
        tvRole = vContent.findViewById(R.id.tv_role);
        final TextInputLayout tilName = vContent.findViewById(R.id.til_name);
        etName = tilName.getEditText();
        final TextInputLayout tilEmail = vContent.findViewById(R.id.til_email);
        etEmail = tilEmail.getEditText();
        TextView tvChangePassword = vContent.findViewById(R.id.tv_change_password);
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePasswordDialogFragment.newInstance()
                        .show(getActivity().getSupportFragmentManager(), ChangePasswordDialogFragment.TAG);
            }
        });

        View buttonChange = vContent.findViewById(R.id.btn_change);

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
        String username = PreferenceLoginHelper.getUserId();
        String role = PreferenceLoginHelper.getRole();
        String name = etName.getText().toString();
        String email = etEmail.getText().toString();

        ((BaseActivity) getActivity()).showProgressDialog();
        if (isImageChanged) {
            updateMemberPresenter.updateMember(username,name,email, role, photoPath);
        } else { // image will no change
            updateMemberPresenter.updateMember(username,name,email, role, null);
        }
    }

    public void onChangePasswordDialogClicked (String oldPassword, String newPassword) {
        ((BaseActivity) getActivity()).showProgressDialog();
        if (updatePasswordPresenter == null) {
            updatePasswordPresenter = new UpdatePasswordPresenter(this);
        }
        updatePasswordPresenter.updatePassword(PreferenceLoginHelper.getUserId(), oldPassword, newPassword);
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
        myInfoPresenter.onResume();
        updateMemberPresenter.onResume();
        if (updatePasswordPresenter!= null) {
            updatePasswordPresenter.onResume();
        }
        progressBar.setVisibility(View.VISIBLE);
        vContent.setVisibility(View.GONE);
        myInfoPresenter.getMyInfo();
    }

    @Override
    public void onErrorGetMyInfo(Throwable throwable) {
        progressBar.setVisibility(View.GONE);
        vContent.setVisibility(View.VISIBLE);
        onSuccessGetMyInfo();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessGetMyInfo() {
        progressBar.setVisibility(View.GONE);
        vContent.setVisibility(View.VISIBLE);
        tvUserName.setText(PreferenceLoginHelper.getUserId());
        tvRole.setText(PreferenceLoginHelper.getRole());
        etName.setText(PreferenceLoginHelper.getName());
        etEmail.setText(PreferenceLoginHelper.getEmail());
        if (!isImageChanged) {
            photoPath = PreferenceLoginHelper.getPhoto();
        }
        loadImageFromPhotoPath();
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
        myInfoPresenter.onPause();
        updateMemberPresenter.onPause();
        if (updatePasswordPresenter!=null) {
            updatePasswordPresenter.onPause();
        }
    }

    @Override
    public void onErrorUpdatePassword(Throwable throwable) {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessUpdatePassword() {
        ((BaseActivity) getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), getString(R.string.password_has_been_changed));
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
        if (isImageChanged) {
            isImageChanged = false;
        }
        progressBar.setVisibility(View.VISIBLE);
        vContent.setVisibility(View.GONE);
        myInfoPresenter.getMyInfo();
        onUserInfoFragmentListener.onUserChanged();
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
        onUserInfoFragmentListener = (OnUserInfoFragmentListener) context;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_IS_IMAGE_CHANGED, isImageChanged);
        outState.putString(SAVED_PHOTO_PATH, photoPath);
    }

}
