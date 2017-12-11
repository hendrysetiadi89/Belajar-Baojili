package com.combintech.baojili.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Hendry Setiadi
 */

public class ViewUtil {
    public static void hideKeyboard(@NonNull Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            hideKeyboard(view);
        }
    }

    public static void hideKeyboard(@NonNull View view){
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public static int getRelativeTop(View viewToCheck, View rootView) {
        if (viewToCheck.getParent() == rootView)
            return viewToCheck.getTop();
        else
            return viewToCheck.getTop() + getRelativeTop((View) viewToCheck.getParent(), rootView);
    }

    public static Spanned fromHtml(String text) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(text);
        }
        return result;
    }
}
