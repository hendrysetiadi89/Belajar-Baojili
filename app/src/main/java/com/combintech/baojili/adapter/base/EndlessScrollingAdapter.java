package com.combintech.baojili.adapter.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.combintech.baojili.R;

import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public abstract class EndlessScrollingAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    // how many item before loadmore
    private static final int VISIBLE_THRESHOLD = 1;

    private ArrayList<T> dataList;
    protected Context context;
    private boolean isLoading;

    private boolean hasMoreDataToLoad = true;
    private int numPerPage = 0;

    //TYPE
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private static final int VIEW_TYPE_EMPTY = 3;

    public EndlessScrollingAdapter(Context context, ArrayList<T> dataList, int numPerPage){
        setDataList(dataList);
        this.context = context;
        this.numPerPage = numPerPage;
    }

    public void setDataList(ArrayList<T> dataList) {
        if (dataList == null) {
            this.dataList = new ArrayList<>();
        } else {
            this.dataList = dataList;
        }
    }

    public void addDataListAndNotify(ArrayList<T> dataList) {
        int prevSize = this.dataList.size();
        int addedSize = dataList.size();
        if (addedSize == 0) {
            hasMoreDataToLoad = false;
            return;
        }
        this.dataList.addAll(dataList);
        if (prevSize == 0) {
            this.notifyDataSetChanged();
        } else {
            this.notifyItemRangeInserted(prevSize, addedSize);
        }
        // if it is not fit, it must be no more data.
        if (addedSize < numPerPage) {
            hasMoreDataToLoad = false;
        }
    }

    public void reset() {
        this.hasMoreDataToLoad = true;
        setDataList(null);
        notifyDataSetChanged();
    }

    public boolean canLoadMore(LinearLayoutManager layoutManager){
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition ();
        if (this.hasMoreDataToLoad && !isLoading
                && layoutManager.getItemCount() <= (
                lastVisibleItemPosition
                        + VISIBLE_THRESHOLD)) {
            return true;
        }
        return false;
    }

    public void setLoading(boolean loading) {
        if (this.isLoading == loading) {
            return;
        }
        isLoading = loading;
        if (isLoading) {
            notifyItemInserted(dataList.size());
        } else {
            notifyItemRemoved(dataList.size());
        }
    }

    public int getNumberOfPage(){
        return (dataList.size() / numPerPage);
    }

    public boolean isLoading() {
        return isLoading;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        LoadingViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType) {
            case VIEW_TYPE_LOADING:
                view = inflater.inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(view);
            case VIEW_TYPE_EMPTY:
                return onCreateEmptyViewHolder(inflater, parent, viewType);
            default:
            case VIEW_TYPE_ITEM:
                return onCreateItemViewHolder(inflater, parent, viewType);
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateItemViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);
    protected abstract RecyclerView.ViewHolder onCreateEmptyViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    @Override
    public final int getItemViewType(int position) {
        if (dataList == null || dataList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        if (position == dataList.size()) {
            return VIEW_TYPE_LOADING;
        }
        else {
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public final int getItemCount() {
        if (dataList == null || dataList.size() == 0) return 1; // for empty
        return dataList.size() + (isLoading?1:0); // for loadmore
    }

    public final ArrayList<T> getDataList() {
        return dataList;
    }
}
