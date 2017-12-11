package com.combintech.baojili.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.combintech.baojili.R;
/**
 * Created by hendry on 9/18/2017.
 */

public class SearchView extends FrameLayout {

    private EditText etSearch;
    private ImageView ivSearch;
    private ImageView ivDelete;

    private int hintRes;

    OnSearchViewTextChangedListener onSearchViewTextChangedListener;
    public interface OnSearchViewTextChangedListener{
        void afterSearchTextChanged(String text);
    }
    public SearchView(@NonNull Context context) {
        super(context);
        init();
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyAttrs(attrs);
        init();
    }

    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyAttrs(attrs);
        init();
    }

    @TargetApi(21)
    public SearchView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyAttrs(attrs);
        init();
    }

    private void applyAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SearchView);
        hintRes = a.getResourceId(R.styleable.SearchView_sv_hint, 0);
        a.recycle();
    }

    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_search_layout, this, false);
        etSearch = view.findViewById(R.id.et_search);
        if (hintRes!= 0) {
            etSearch.setHint(hintRes);
        }
        ivSearch = view.findViewById(R.id.iv_search);
        ivDelete = view.findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText(null);
            }
        });
        etSearch.addTextChangedListener(new AfterTextTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    toggleSearchIconOn();
                } else {
                    toggleSearchIconOff();
                }
                if (onSearchViewTextChangedListener!= null) {
                    onSearchViewTextChangedListener.afterSearchTextChanged(s.toString());
                }
            }
        });
        this.addView(view);
    }

    public void setOnSearchViewTextChangedListener(
            OnSearchViewTextChangedListener onSearchViewTextChangedListener) {
        this.onSearchViewTextChangedListener = onSearchViewTextChangedListener;
    }

    public String getSearchText(){
        return etSearch.getText().toString();
    }

    private void toggleSearchIconOff() {
        if (ivSearch.getVisibility() == View.GONE) {
            return;
        }
        ivSearch.setVisibility(View.GONE);
        ivDelete.setVisibility(View.VISIBLE);
    }

    private void toggleSearchIconOn() {
        if (ivSearch.getVisibility() == View.VISIBLE) {
            return;
        }
        ivSearch.setVisibility(View.VISIBLE);
        ivDelete.setVisibility(View.GONE);
    }


}
