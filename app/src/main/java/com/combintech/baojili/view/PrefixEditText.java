package com.combintech.baojili.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.combintech.baojili.R;

/**
 * Created by Hendry Setiadi
 */

public class PrefixEditText extends AppCompatEditText
        implements TextWatcher {
    private String mPrefix;
    private int mColor;

    public PrefixEditText(Context context) {
        super(context);
        init(null, 0);
    }

    public PrefixEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PrefixEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PrefixEditText, defStyle, 0);

        mPrefix = a.getString(
                R.styleable.PrefixEditText_prefix);
        mColor = a.getColor(
                R.styleable.PrefixEditText_prefixTextColor,
                0);
        a.recycle();

        int[] set = {
                android.R.attr.text        // idx 0
        };
        a = getContext().obtainStyledAttributes(
                attrs, set);
        String text = a.getString(0);
        a.recycle();

        setText( concat(mPrefix, text));
        addTextChangedListener(this);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        removeTextChangedListener(this);
        if (TextUtils.isEmpty(mPrefix)) {
            super.setText(text, type);
        }
        else if (TextUtils.isEmpty(text)) {
            int prefixLength = mPrefix.length();
            Spannable spannable = new SpannableString(mPrefix);
            if (mColor != 0) {
                spannable.setSpan(new ForegroundColorSpan(mColor),
                        0, mPrefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            super.setText(spannable, type);
            Selection.setSelection(spannable,prefixLength);
        }
        else {
            String textString = text.toString();
            Spannable spannable;
            if (textString.startsWith(mPrefix)) {
                spannable = new SpannableString(textString);
            } else {
                String combinedString = mPrefix + textString;
                spannable = new SpannableString(combinedString);
            }
            if (mColor != 0) {
                spannable.setSpan(new ForegroundColorSpan(mColor),
                        0, mPrefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            super.setText(spannable, type);
            Selection.setSelection(super.getText(),
                    super.getText().length());
        }
        addTextChangedListener(this);
    }

    public String getTextWithoutPrefix(){
        Editable s = super.getText();
        return s.toString().replaceFirst(mPrefix, "").trim();
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void setPrefix(String prefix) {
        String previousText = getTextWithoutPrefix();
        mPrefix = prefix;
        setText( concat(prefix, previousText));
    }

    public void setPrefixTextColor(int color) {
        this.mColor = color;
        setText(getText());
    }

    public int getPrefixTextColor(){
        return this.mColor;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().startsWith(mPrefix)) {
            removeTextChangedListener(this);
            PrefixEditText.super.setText(mPrefix);
            Selection.setSelection(PrefixEditText.super.getText(),
                    PrefixEditText.super.getText().length());
            addTextChangedListener(this);
        }
    }

    private String concat(String a, String b){
        String nonNullA = a==null?"":a;
        String nonNullB = b==null?"":b;
        return nonNullA.concat(nonNullB);
    }

    @Override
    public void setSelection(int index) {
        super.setSelection(index + mPrefix.length());
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        // if select on the prefix text, move selection to the end
        int prefixLength = mPrefix == null? 0: mPrefix.length();
        if (selStart < prefixLength
                &&
                selEnd == selStart ) {
            Selection.setSelection(PrefixEditText.super.getText(),
                    PrefixEditText.super.getText().length());
        }
        super.onSelectionChanged(selStart, selEnd);
    }
}