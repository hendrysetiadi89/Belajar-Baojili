package com.combintech.baojili.view;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.combintech.baojili.util.ViewUtil;

/**
 * Created by User on 10/11/2017.
 */

public class ZeroThresAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    public ZeroThresAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public ZeroThresAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ZeroThresAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private OnItemAutoCompleteClickListener onItemAutoCompleteClickListener;
    public interface OnItemAutoCompleteClickListener{
        void onAutoCompleteClickListener();
    }
    public void setOnItemAutoCompleteClickListener(OnItemAutoCompleteClickListener onItemAutoCompleteClickListener){
        this.onItemAutoCompleteClickListener = onItemAutoCompleteClickListener;
    }

    private void init(){
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ZeroThresAutoCompleteTextView.this.showDropDown();
                }
            }
        });
        this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ViewUtil.hideKeyboard(view);
                if (onItemAutoCompleteClickListener!= null) {
                    onItemAutoCompleteClickListener.onAutoCompleteClickListener();
                }
            }
        });
        setAllCaps();
    }

    private void setAllCaps(){
        InputFilter[] editFilters = getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        setFilters(newFilters);
    }

    @Override
    public boolean enoughToFilter() {
        return getText().length() >= 0;
    }

    @Override
    public int getThreshold() {
        return 0;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)){
                return true;
            }
        }

        return super.onKeyPreIme(keyCode, event);
    }
}
