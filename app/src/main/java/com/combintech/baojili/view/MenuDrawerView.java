package com.combintech.baojili.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.combintech.baojili.R;

/**
 * Created by Hendry Setiadi
 */

public class MenuDrawerView extends FrameLayout {
    private int iconRes;
    private String text;

    boolean selected;

    public MenuDrawerView(@NonNull Context context) {
        super(context);
        init();
    }

    public MenuDrawerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyAttrs(attrs);
        init();
    }

    public MenuDrawerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyAttrs(attrs);
        init();
    }

    @TargetApi(21)
    public MenuDrawerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyAttrs(attrs);
        init();
    }

    private void applyAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MenuDrawerView);
        iconRes = a.getResourceId(R.styleable.MenuDrawerView_mdv_icon_res, 0);
        text = a.getString(R.styleable.MenuDrawerView_mdv_text);
        a.recycle();
    }

    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_menu_item_drawer,this, false);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        TextView tvText = view.findViewById(R.id.tv_text);
        if (iconRes== 0) {
            ivIcon.setVisibility(View.INVISIBLE);
        } else {
            ivIcon.setImageResource(iconRes);
            ivIcon.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(text)) {
            tvText.setVisibility(View.GONE);
        } else {
            tvText.setText(text);
            tvText.setVisibility(View.VISIBLE);
        }
        setClickable(true);
        this.addView(view);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}
