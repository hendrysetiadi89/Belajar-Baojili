package com.combintech.baojili.view;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import com.combintech.baojili.util.CurrencyFormatUtil;
import com.combintech.baojili.util.StringUtil;

/**
 * Created by Hendry Setiadi
 */

public class IDRCurrencyFormatTextWatcher extends AfterTextTextWatcher {
    private String defaultValue;
    private EditText editText;

    public IDRCurrencyFormatTextWatcher(EditText editText){
        this.editText = editText;
        this.defaultValue = "0";
    }
    @Override
    public void afterTextChanged(Editable s) {
        String sString = s.toString();
        String nonNumericString = StringUtil.removeAllNonNumeric(sString);
        if (TextUtils.isEmpty(nonNumericString)) {
            editText.setText(defaultValue);
            editText.setSelection(defaultValue.length());
        } else {
            CurrencyFormatUtil.ThousandString thousandString =
                    CurrencyFormatUtil.getThousandSeparatorString(
                            nonNumericString, false, editText.getSelectionStart());
            editText.removeTextChangedListener(this);
            editText.setText(thousandString.getFormattedString());
                editText.setSelection(Math.min(editText.length(), thousandString.getSelection()));
            editText.addTextChangedListener(this);
        }
    }
}
