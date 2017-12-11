package com.combintech.baojili.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.combintech.baojili.R;
import com.combintech.baojili.dialog.base.BaseDialogFragment;

/**
 * Created by Hendry Setiadi
 */

public class ChangePasswordDialogFragment extends BaseDialogFragment {
    public static final String TAG = ChangePasswordDialogFragment.class.getSimpleName();

    private OnChangePasswordDialogFragmentListener onChangePasswordDialogFragmentListener;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;

    public interface OnChangePasswordDialogFragmentListener{
        void onChangePasswordDialogClicked(String oldPassword, String newPassword);
    }

    public static ChangePasswordDialogFragment newInstance() {
        ChangePasswordDialogFragment fragment = new ChangePasswordDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean isFullWidth() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog_change_password, container, false);

        etOldPassword = v.findViewById(R.id.et_old_password);
        etNewPassword = v.findViewById(R.id.et_new_password);
        etConfirmNewPassword = v.findViewById(R.id.et_confirm_new_password);
        View viewSubmit = v.findViewById(R.id.button_submit);
        View viewCancel = v.findViewById(R.id.button_cancel);

        viewCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ChangePasswordDialogFragment.this.dismiss();
            }
        });

        viewSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                if (TextUtils.isEmpty(newPassword)) {
                    etNewPassword.setError(getString(R.string.password_must_be_filled));
                    return;
                }
                String confirmNewPassword = etConfirmNewPassword.getText().toString();
                if (TextUtils.isEmpty(confirmNewPassword)) {
                    etConfirmNewPassword.setError(getString(R.string.confirm_password_must_be_filled));
                    return;
                }
                if (!confirmNewPassword.equals(newPassword)) {
                    etConfirmNewPassword.setError(getString(R.string.confirm_password_not_match));
                    return;
                }
                onChangePasswordDialogFragmentListener.onChangePasswordDialogClicked(oldPassword, newPassword);
                ChangePasswordDialogFragment.this.dismiss();
            }
        });

        return v;
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
        onChangePasswordDialogFragmentListener = (OnChangePasswordDialogFragmentListener) context;
    }

}
