package com.combintech.baojili.view;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Hendry Setiadi
 */

public abstract class AfterTextTextWatcher implements TextWatcher {
    @Override
    public final void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public abstract void afterTextChanged(Editable s);
}
