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

public class ChooseImageFromFragment extends BaseDialogFragment {
    public static final String TAG = ChooseImageFromFragment.class.getSimpleName();

    private OnChooseImageFromFragmentListener onChooseImageFromFragmentListener;
    public interface OnChooseImageFromFragmentListener{
        void onChooseSelectImageCamera();
        void onChooseSelectImageGallery();
    }

    public static ChooseImageFromFragment newInstance() {
        ChooseImageFromFragment fragment = new ChooseImageFromFragment();
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

        View v = inflater.inflate(R.layout.fragment_choose_image_from, container, false);

        View vgCamera = v.findViewById(R.id.vg_camera);
        View vgGallery = v.findViewById(R.id.vg_gallery);

        vgCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onChooseImageFromFragmentListener.onChooseSelectImageCamera();
                ChooseImageFromFragment.this.dismiss();
            }
        });

        vgGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onChooseImageFromFragmentListener.onChooseSelectImageGallery();
                ChooseImageFromFragment.this.dismiss();
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
        onChooseImageFromFragmentListener = (OnChooseImageFromFragmentListener) context;
    }

}
