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
import com.combintech.baojili.model.StockByLocation;
import com.combintech.baojili.util.ImageHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Hendry Setiadi
 */

public class InfoStockByLocItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<StockByLocation.Item> itemList;

    private static final int EMPTY_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    public InfoStockByLocItemAdapter(Context context, StockByLocation stockByLocation) {
        this.context = context;
        itemList = stockByLocation.getItem();
        Collections.sort(itemList, new Comparator<StockByLocation.Item>() {
            @Override
            public int compare(StockByLocation.Item item, StockByLocation.Item t1) {
                int comparison = item.getCode().compareTo(t1.getCode());
                if (comparison == 0) {
                    comparison = item.getSize().compareTo(t1.getSize());
                }
                return comparison;
            }
        });
    }

    private class InfoStockByItemViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPhoto;
        TextView tvItemCodeAndSize;
        TextView tvItemDescription;
        TextView tvStock;

        private InfoStockByItemViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = itemView.findViewById(R.id.iv_item_photo);
            this.tvItemCodeAndSize = itemView.findViewById(R.id.tv_code_and_size);
            this.tvItemDescription = itemView.findViewById(R.id.tv_item_description);
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
                View view = inflater.inflate(R.layout.item_stock_by_item, parent, false);
                return new InfoStockByItemViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InfoStockByItemViewHolder) {
            StockByLocation.Item item = itemList.get(position);

            InfoStockByItemViewHolder infoStockByItemViewHolder = (InfoStockByItemViewHolder) holder;
            if (TextUtils.isEmpty(item.getPhoto())){
                infoStockByItemViewHolder.ivPhoto.setImageResource(R.drawable.no_image);
            } else {
                ImageHelper.loadImageFitCenter(context, item.getPhoto(), infoStockByItemViewHolder.ivPhoto);
            }

            infoStockByItemViewHolder.tvItemCodeAndSize.setText(item.getCode()+ " - " + item.getSize());
            if (TextUtils.isEmpty(item.getDesc())){
                infoStockByItemViewHolder.tvItemDescription.setVisibility(View.GONE);
            } else {
                infoStockByItemViewHolder.tvItemDescription.setText(item.getDesc());
                infoStockByItemViewHolder.tvItemDescription.setVisibility(View.VISIBLE);
            }
            infoStockByItemViewHolder.tvStock.setText(item.getStock());
        }
    }

    @Override
    public int getItemCount() {
        return (itemList == null || itemList.size() == 0)?
                1:
                itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList == null || itemList.size() == 0) {
            return EMPTY_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}
