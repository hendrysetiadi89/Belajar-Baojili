package com.combintech.baojili.dialog.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by Hendry Setiadi
 */

public abstract class BaseDialogFragment extends DialogFragment {

    public static final int STYLE_NO_TITLE = DialogFragment.STYLE_NO_TITLE;
    public static final int STYLE_NO_FRAME = DialogFragment.STYLE_NO_FRAME;
    public static final int STYLE_NO_INPUT = DialogFragment.STYLE_NO_INPUT;
    public static final int STYLE_NORMAL = DialogFragment.STYLE_NORMAL;

    public static final int THEME_HOLO = android.R.style.Theme_Holo;
    public static final int THEME_HOLO_LIGHT_DIALOG = android.R.style.Theme_Holo_Light_Dialog;
    public static final int THEME_HOLO_LIGHT_PANEL = android.R.style.Theme_Holo_Light_Panel;
    public static final int THEME_HOLO_LIGHT = android.R.style.Theme_Holo_Light;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        setStyle(STYLE_NO_TITLE, THEME_HOLO_LIGHT_DIALOG);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFullWidth()) {
            Dialog dialog = getDialog();
            if (dialog != null) {
                DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                int width = displayMetrics.widthPixels;
                int dialogWidth;
                if (getContext().getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
                    dialogWidth = (int) (0.85 * width);
                } else {
                    dialogWidth = (int) (0.75 * width);
                }
                dialog.getWindow().setLayout(dialogWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    public abstract boolean isFullWidth();

    @Override
    public void show(FragmentManager manager, String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }
        super.show(manager, tag);
    }
}
