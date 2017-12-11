package com.combintech.baojili.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.combintech.baojili.R;
import com.combintech.baojili.adapter.base.EndlessScrollingAdapter;
import com.combintech.baojili.constant.NetworkConstant;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.util.DateUtil;

import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public class ItemOutListAdapter extends EndlessScrollingAdapter<ItemInOut> {

    private OnAdapterItemClickedListener listener;
    public interface OnAdapterItemClickedListener{
        void onAdapterItemClicked(ItemInOut itemInOut);
        void onAdapterItemDeleteClicked(ItemInOut itemInOut);
    }
    public ItemOutListAdapter(Context context, ArrayList<ItemInOut> itemInOutArrayList, int numPerPage,
                              OnAdapterItemClickedListener listener) {
        super(context,itemInOutArrayList,numPerPage);
        this.listener = listener;
    }

    private class ItemOutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDateAndUser;
        TextView tvType;

        TextView tvLocationName;
        TextView tvItem;
        TextView tvQuantity;

        View vDelete;

        private ItemOutViewHolder(View itemView) {
            super(itemView);
            this.tvDateAndUser = itemView.findViewById(R.id.tv_date_and_user);
            this.tvType = itemView.findViewById(R.id.tv_type);
            this.tvLocationName = itemView.findViewById(R.id.tv_location_name);
            this.tvItem = itemView.findViewById(R.id.tv_item);
            this.tvQuantity = itemView.findViewById(R.id.tv_quantity);

            this.vDelete = itemView.findViewById(R.id.iv_delete);
            if (vDelete!=null) {
                vDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        ItemInOut itemInOut = getDataList().get(position);
                        if (listener!=null) {
                            listener.onAdapterItemDeleteClicked(itemInOut);
                        }
                    }
                });
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ItemInOut itemInOut = getDataList().get(position);
            if (listener!=null) {
                listener.onAdapterItemClicked(itemInOut);
            }
        }

        public void bind(ItemInOut itemInOut){
            String dateAndUserString = "";
            if (!TextUtils.isEmpty(itemInOut.getUpdatedBy())) {
                dateAndUserString = context.getString(R.string.updated) + ": " + DateUtil.formatDate(
                        DateUtil.FORMAT_YYYY_MM_DD_STRIPE_HH_mm_ss,
                        DateUtil.FORMAT_EEE_D_MM_YYYY_HH_mm,
                        itemInOut.getUpdatedAt())
                        + " (" + itemInOut.getUpdatedBy() + ")";
            } else {
                dateAndUserString = context.getString(R.string.created) + ": " + DateUtil.formatDate(
                        DateUtil.FORMAT_YYYY_MM_DD_STRIPE_HH_mm_ss,
                        DateUtil.FORMAT_EEE_D_MM_YYYY_HH_mm,
                        itemInOut.getDatetime())
                        + " (" + itemInOut.getUserID() + ")";
            }
            tvDateAndUser.setText(dateAndUserString);
            String typeAndSource = itemInOut.getTrType();
            if (itemInOut.getTrType().equals(NetworkConstant.PEMINDAHAN)) {
                typeAndSource += " " + context.getString(R.string.to_x, itemInOut.getTargetLocationName());
            }
            tvType.setText(typeAndSource);
            tvLocationName.setText(itemInOut.getSourceLocationName());
            String itemString = itemInOut.getCode() + " - " + itemInOut.getSize();
            tvItem.setText(itemString);
            tvQuantity.setText(itemInOut.getQuantity());
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void removeItem (String itemTrIdToRemove){
        for (int i =0, sizei = getDataList().size(); i<sizei; i++) {
            ItemInOut itemInOut = getDataList().get(i);
            if (itemInOut.getTrItemID().equals(itemTrIdToRemove)){
                getDataList().remove(i);
                this.notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_item_out, parent, false);
        return new ItemOutViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateEmptyViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_empty, parent, false);
        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemOutViewHolder) {
            ItemInOut itemInOut = getDataList().get(position);

            ItemOutViewHolder itemInViewHolder = (ItemOutViewHolder) holder;
            itemInViewHolder.bind(itemInOut);
        }
    }

}
