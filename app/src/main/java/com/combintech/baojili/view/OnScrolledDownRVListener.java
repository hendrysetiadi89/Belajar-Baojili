package com.combintech.baojili.view;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Hendry Setiadi
 */

public abstract class OnScrolledDownRVListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy <= 0) {
            // no op when going up
            return;
        }
        onScrolledDown(recyclerView, dx, dy);
    }

    public abstract void onScrolledDown(RecyclerView recyclerView, int dx, int dy);
}
