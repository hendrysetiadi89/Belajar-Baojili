package com.combintech.baojili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.model.StockByLocation;

import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public class InfoStockByLocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<StockByLocation> stockByLocationList;

    private static final int EMPTY_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    private OnAdapterItemClickedListener listener;
    public interface OnAdapterItemClickedListener{
        void onAdapterItemClicked(StockByLocation stockByLocation);
    }
    public InfoStockByLocAdapter(Context context, ArrayList<StockByLocation> stockByLocationList, OnAdapterItemClickedListener listener) {
        this.context = context;
        setStockByLocationList(stockByLocationList);
        this.listener = listener;
    }

    public void setStockByLocationList(ArrayList<StockByLocation> stockByLocationList) {
        if (stockByLocationList == null) {
            this.stockByLocationList = new ArrayList<>();
        } else {
            this.stockByLocationList = stockByLocationList;
        }
    }

    public ArrayList<StockByLocation> getStockByLocationList() {
        return stockByLocationList;
    }

    private class InfoStockByLocViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvLocationName;
        TextView tvStock;

        private InfoStockByLocViewHolder(View itemView) {
            super(itemView);
            this.tvLocationName = itemView.findViewById(R.id.tv_location_name);
            this.tvStock = itemView.findViewById(R.id.tv_stock);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            StockByLocation stockByLocation = stockByLocationList.get(position);
            if (listener!=null) {
                listener.onAdapterItemClicked(stockByLocation);
            }
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case EMPTY_TYPE: {
                View view = inflater.inflate(R.layout.item_empty, parent, false);
                return new EmptyViewHolder(view);
            }
            case ITEM_TYPE: {
                View view = inflater.inflate(R.layout.item_stock_by_loc, parent, false);
                return new InfoStockByLocViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InfoStockByLocViewHolder) {
            StockByLocation stockByLocation = stockByLocationList.get(position);

            InfoStockByLocViewHolder infoStockByLocViewHolder = (InfoStockByLocViewHolder) holder;
            infoStockByLocViewHolder.tvLocationName.setText(stockByLocation.getLocationName());
            infoStockByLocViewHolder.tvStock.setText(stockByLocation.getTotalStock());
        }
    }

    @Override
    public int getItemCount() {
        return stockByLocationList.size() == 0? 1: stockByLocationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (stockByLocationList.size() == 0) {
            return EMPTY_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}
