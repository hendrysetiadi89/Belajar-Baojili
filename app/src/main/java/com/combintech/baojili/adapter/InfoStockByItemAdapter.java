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

import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public class InfoStockByItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<StockByItem> stockByItemArrayList;

    private static final int EMPTY_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    private OnAdapterItemClickedListener listener;
    public interface OnAdapterItemClickedListener{
        void onAdapterItemClicked(StockByItem stockByItem);
    }
    public InfoStockByItemAdapter(Context context, ArrayList<StockByItem> stockByItemArrayList,
                                  OnAdapterItemClickedListener listener) {
        this.context = context;
        setStockByItemList(stockByItemArrayList);
        this.listener = listener;
    }

    public void setStockByItemList(ArrayList<StockByItem> stockByItemArrayList) {
        if (stockByItemArrayList == null) {
            this.stockByItemArrayList = new ArrayList<>();
        } else {
            this.stockByItemArrayList = stockByItemArrayList;
        }
    }

    public ArrayList<StockByItem> getStockByItemArrayList() {
        return stockByItemArrayList;
    }

    private class InfoStockByItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            StockByItem stockByItem = stockByItemArrayList.get(position);
            if (listener!=null) {
                listener.onAdapterItemClicked(stockByItem);
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
                View view = inflater.inflate(R.layout.item_stock_by_item, parent, false);
                return new InfoStockByItemViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InfoStockByItemViewHolder) {
            StockByItem stockByItem = stockByItemArrayList.get(position);

            InfoStockByItemViewHolder infoStockByItemViewHolder = (InfoStockByItemViewHolder) holder;
            if (TextUtils.isEmpty(stockByItem.getPhoto())){
                infoStockByItemViewHolder.ivPhoto.setImageResource(R.drawable.no_image);
            } else {
                ImageHelper.loadImageFitCenter(context, stockByItem.getPhoto(), infoStockByItemViewHolder.ivPhoto);
            }

            infoStockByItemViewHolder.tvItemCodeAndSize.setText(stockByItem.getCode()+ " - " + stockByItem.getSize());
            if (TextUtils.isEmpty(stockByItem.getDescription())){
                infoStockByItemViewHolder.tvItemDescription.setVisibility(View.GONE);
            } else {
                infoStockByItemViewHolder.tvItemDescription.setText(stockByItem.getDescription());
                infoStockByItemViewHolder.tvItemDescription.setVisibility(View.VISIBLE);
            }
            infoStockByItemViewHolder.tvStock.setText(stockByItem.getTotalStock());
        }
    }

    @Override
    public int getItemCount() {
        return stockByItemArrayList.size() == 0? 1: stockByItemArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (stockByItemArrayList.size() == 0) {
            return EMPTY_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}
