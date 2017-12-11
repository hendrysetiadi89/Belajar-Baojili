package com.combintech.baojili.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.util.AttributeSet;

/**
 * Created by User on 10/14/2017.
 */

public class AllCapsEditText extends AppCompatEditText {

    public AllCapsEditText(Context context) {
        super(context);
        init();
    }

    public AllCapsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AllCapsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        InputFilter[] editFilters = getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        setFilters(newFilters);
    }
}
