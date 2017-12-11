package com.combintech.baojili.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.KMNumbers;
import com.combintech.baojili.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hendry Setiadi
 */

public class ItemListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<BJItem> bjItemArrayList;

    private List<Integer> filteredBJItemList = new ArrayList<>();
    private String query;

    private boolean isManager;

    private static final int EMPTY_TYPE = 0;
    private static final int ITEM_TYPE = 1;
    private static final int NOT_FOUND_TYPE = 2;

    private OnAdapterItemClickedListener listener;

    public interface OnAdapterItemClickedListener {
        void onAdapterItemClicked(BJItem bjItem);

        void onAdapterItemDeleteClicked(BJItem bjItem);
    }

    public ItemListAdapter(Context context, ArrayList<BJItem> bjItemArrayList,
                           OnAdapterItemClickedListener listener,
                           boolean isManager, String query) {
        this.context = context;
        setItemList(bjItemArrayList);
        this.listener = listener;
        this.isManager = isManager;
        setQuery(query);
    }

    public void setQuery(String query) {
        if (!query.equalsIgnoreCase(this.query)) {
            this.query = query;
        }
        if (TextUtils.isEmpty(query)) {
            this.query = null;
            notifyDataSetChanged();
            return;
        }
        filteredBJItemList = new ArrayList<>();
        for (int i = 0, sizei = bjItemArrayList.size(); i < sizei; i++) {
            BJItem bjItem = bjItemArrayList.get(i);
            String queryLowercase = query.toLowerCase();
            String codeLowercase = bjItem.getCode().toLowerCase();
            if (codeLowercase.contains(queryLowercase)) {
                filteredBJItemList.add(i);
                continue;
            }
            String sizeLowercase = bjItem.getSize().toLowerCase();
            if (sizeLowercase.contains(queryLowercase)) {
                filteredBJItemList.add(i);
                continue;
            }
            String descriptionLowercase = bjItem.getDescription().toLowerCase();
            if (descriptionLowercase.contains(queryLowercase)) {
                filteredBJItemList.add(i);
                continue;
            }
        }
        notifyDataSetChanged();
    }

    public void setItemList(ArrayList<BJItem> bjItemArrayList) {
        if (bjItemArrayList == null) {
            this.bjItemArrayList = new ArrayList<>();
        } else {
            this.bjItemArrayList = bjItemArrayList;
        }
    }

    public ArrayList<BJItem> getBjItemArrayList() {
        return bjItemArrayList;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPhoto;
        TextView tvItemCodeAndSize;
        TextView tvItemDescription;

        TextView tvItemType;
        TextView tvItemPrice;
        TextView tvItemCost;

        TextView tvItemPriceLabel;
        TextView tvItemPriceColon;
        TextView tvItemCostLabel;
        TextView tvItemCostColon;

        View vDelete;

        private ItemViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = itemView.findViewById(R.id.iv_item_photo);
            this.tvItemCodeAndSize = itemView.findViewById(R.id.tv_code_and_size);
            this.tvItemDescription = itemView.findViewById(R.id.tv_item_description);
            this.tvItemType = itemView.findViewById(R.id.tv_type);
            this.tvItemPrice = itemView.findViewById(R.id.tv_price);
            this.tvItemCost = itemView.findViewById(R.id.tv_cost);

            this.tvItemPriceLabel = itemView.findViewById(R.id.tv_label_2);
            this.tvItemPriceColon = itemView.findViewById(R.id.tv_colon_2);
            this.tvItemCostLabel = itemView.findViewById(R.id.tv_label_3);
            this.tvItemCostColon = itemView.findViewById(R.id.tv_colon_3);

            this.vDelete = itemView.findViewById(R.id.iv_delete);
            if (isManager) {
                vDelete.setVisibility(View.VISIBLE);
                vDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        BJItem bjItem;
                        if (TextUtils.isEmpty(query)) {
                            bjItem = bjItemArrayList.get(position);
                        } else {
                            bjItem = bjItemArrayList.get(filteredBJItemList.get(position));
                        }
                        if (listener != null) {
                            listener.onAdapterItemDeleteClicked(bjItem);
                        }
                    }
                });
                itemView.setOnClickListener(this);
            } else {
                vDelete.setVisibility(View.GONE);
                itemView.setClickable(false);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            BJItem bjItem;
            if (TextUtils.isEmpty(query)) {
                bjItem = bjItemArrayList.get(position);
            } else {
                bjItem = bjItemArrayList.get(filteredBJItemList.get(position));
            }
            if (listener != null) {
                listener.onAdapterItemClicked(bjItem);
            }
        }

        public void bind(BJItem bjItem) {
            if (TextUtils.isEmpty(bjItem.getPhoto())) {
                ivPhoto.setImageResource(R.drawable.no_image);
            } else {
                ImageHelper.loadImageFitCenter(context, bjItem.getPhoto(), ivPhoto);
            }

            tvItemCodeAndSize.setText(bjItem.getCode() + " - " + bjItem.getSize());
            if (TextUtils.isEmpty(bjItem.getDescription())) {
                tvItemDescription.setVisibility(View.GONE);
            } else {
                tvItemDescription.setText(bjItem.getDescription());
                tvItemDescription.setVisibility(View.VISIBLE);
            }
            tvItemType.setText(bjItem.getType());
            if (isManager) {
                tvItemPriceLabel.setVisibility(View.VISIBLE);
                tvItemPriceColon.setVisibility(View.VISIBLE);
                tvItemCostLabel.setVisibility(View.VISIBLE);
                tvItemCostColon.setVisibility(View.VISIBLE);

                String priceString = TextUtils.isEmpty(bjItem.getPrice()) ? "0" : bjItem.getPrice();
                tvItemPrice.setText(KMNumbers.formatRupiahString(Long.parseLong(priceString)));

                tvItemPrice.setVisibility(View.VISIBLE);

                String costString = TextUtils.isEmpty(bjItem.getCost()) ? "0" : bjItem.getCost();
                tvItemCost.setText(KMNumbers.formatRupiahString(Long.parseLong(costString)));
                tvItemCost.setVisibility(View.VISIBLE);

            } else { // hide the price if not manager
                tvItemPriceLabel.setVisibility(View.GONE);
                tvItemPriceColon.setVisibility(View.GONE);
                tvItemCostLabel.setVisibility(View.GONE);
                tvItemCostColon.setVisibility(View.GONE);

                tvItemPrice.setVisibility(View.GONE);
                tvItemCost.setVisibility(View.GONE);
            }
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HelpNotFoundViewHolder extends RecyclerView.ViewHolder {
        TextView tvHelpNotFound;

        private HelpNotFoundViewHolder(View itemView) {
            super(itemView);
            this.tvHelpNotFound = itemView.findViewById(R.id.tv_search_not_found);
        }
    }

    public void removeItem(String itemIDToRemove) {
        for (int i = 0, sizei = bjItemArrayList.size(); i < sizei; i++) {
            BJItem bjItem = bjItemArrayList.get(i);
            if (bjItem.getItemID().equals(itemIDToRemove)) {
                bjItemArrayList.remove(i);
                if (TextUtils.isEmpty(query)) {
                    this.notifyItemRemoved(i);
                } else {
                    setQuery(query);
                    this.notifyDataSetChanged();
                }
                break;
            }
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
            case NOT_FOUND_TYPE: {
                View view = inflater.inflate(R.layout.item_help_not_found, parent, false);
                return new HelpNotFoundViewHolder(view);
            }
            case ITEM_TYPE: {
                View view = inflater.inflate(R.layout.item_item, parent, false);
                return new ItemViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            BJItem bjItem;
            if (TextUtils.isEmpty(query)) {
                bjItem = bjItemArrayList.get(position);
            } else {
                bjItem = bjItemArrayList.get(filteredBJItemList.get(position));
            }
            ItemViewHolder infoStockByItemViewHolder = (ItemViewHolder) holder;
            infoStockByItemViewHolder.bind(bjItem);
        } else if (holder instanceof HelpNotFoundViewHolder) {
            HelpNotFoundViewHolder helpNotFoundViewHolder = (HelpNotFoundViewHolder) holder;
            helpNotFoundViewHolder.tvHelpNotFound.setText(
                    ViewUtil.fromHtml(context.getString(R.string.search_keyword_not_found, query)));
        }
    }

    @Override
    public int getItemCount() {
        if (bjItemArrayList.size() == 0) {
            return 1; // empty type
        } else {
            if (TextUtils.isEmpty(query)) {
                return bjItemArrayList.size();
            } else {
                int filteredSize = filteredBJItemList.size();
                if (filteredSize > 0 ){
                    return filteredSize;
                } else{
                    return 1; // for not found type
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (bjItemArrayList.size() == 0) {
            return EMPTY_TYPE;
        } else {
            switch (position){
                case 0:
                    if (TextUtils.isEmpty(query)) {
                        return ITEM_TYPE;
                    } else {
                        if (filteredBJItemList.size() > 0){
                            return ITEM_TYPE;
                        } else {
                            return NOT_FOUND_TYPE;
                        }
                    }
                default:
                    return ITEM_TYPE;
            }
        }
    }
}
