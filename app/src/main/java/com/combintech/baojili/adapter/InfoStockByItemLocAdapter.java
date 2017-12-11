package com.combintech.baojili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.model.StockByItem;
import com.combintech.baojili.model.StockByLocation;
import com.combintech.baojili.util.ImageHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Hendry Setiadi
 */

public class InfoStockByItemLocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;

    private List<StockByItem.Location> locationList;

    private static final int EMPTY_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    public InfoStockByItemLocAdapter(Context context, StockByItem stockByItem) {
        this.context = context;
        locationList = stockByItem.getLocation();
        Collections.sort(locationList, new Comparator<StockByItem.Location>() {
            @Override
            public int compare(StockByItem.Location location, StockByItem.Location t1) {
                return location.getLocationName().compareTo(t1.getLocationName());
            }
        });
    }

    private class InfoStockByLocViewHolder extends RecyclerView.ViewHolder{
        TextView tvLocationName;
        TextView tvStock;

        private InfoStockByLocViewHolder(View itemView) {
            super(itemView);
            this.tvLocationName = itemView.findViewById(R.id.tv_location_name);
            this.tvStock = itemView.findViewById(R.id.tv_stock);
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
            StockByItem.Location item = locationList.get(position);

            InfoStockByLocViewHolder infoStockByLocViewHolder = (InfoStockByLocViewHolder) holder;
            infoStockByLocViewHolder.tvLocationName.setText(item.getLocationName());
            infoStockByLocViewHolder.tvStock.setText(item.getLocationStock());
        }
    }

    @Override
    public int getItemCount() {
        return (locationList == null || locationList.size() == 0)?
                1:
                locationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (locationList == null || locationList.size() == 0) {
            return EMPTY_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}
