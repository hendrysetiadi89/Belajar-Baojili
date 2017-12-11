package com.combintech.baojili.fragment.auth;

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

import com.combintech.baojili.R;
import com.combintech.baojili.activity.base.BaseActivity;
import com.combintech.baojili.presenter.SignInPresenter;
import com.combintech.baojili.util.ErrorUtil;
import com.combintech.baojili.view.AfterTextTextWatcher;

public class SigninFragment extends Fragment implements SignInPresenter.SignInView {

    private EditText etEmail;
    private EditText etPassword;

    OnSignInFragmentListener onSignInFragmentListener;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    private SignInPresenter signInPresenter;

    public interface OnSignInFragmentListener{
        void onSignInResponseSuccess();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInPresenter = new SignInPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        tilEmail = (TextInputLayout) view.findViewById(R.id.til_email);
        etEmail = tilEmail.getEditText();
        tilPassword = (TextInputLayout) view.findViewById(R.id.til_password);
        etPassword = tilPassword.getEditText();
        View btnSignIn = view.findViewById(R.id.btnSignIn);

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
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInButtonClicked();
            }
        });
        signInPresenter = new SignInPresenter(this);
        view.requestFocus();

        return view;
    }

    private void onSignInButtonClicked() {
        String emailString = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            tilEmail.setError(getString(R.string.id_or_email_must_be_filled));
            return;
        }
        String passwordString = etPassword.getText().toString();
        if (TextUtils.isEmpty( passwordString)){
            tilPassword.setError(getString(R.string.password_must_be_filled));
            return;
        }
        ((BaseActivity)getActivity()).showProgressDialog();
        signInPresenter.signIn(emailString, passwordString);
    }


    @Override
    public void onResume() {
        super.onResume();
        signInPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        signInPresenter.onPause();
    }

    @Override
    public void onErrorSignIn(Throwable throwable) {
        ((BaseActivity)getActivity()).hideProgressDialog();
        ErrorUtil.showToast(getContext(), throwable);
    }

    @Override
    public void onSuccessSignIn() {
        ((BaseActivity)getActivity()).hideProgressDialog();
        onSignInFragmentListener.onSignInResponseSuccess();
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
        onSignInFragmentListener = (OnSignInFragmentListener) context;
    }
}
