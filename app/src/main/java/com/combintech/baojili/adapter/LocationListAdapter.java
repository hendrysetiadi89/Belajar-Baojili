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
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.User;
import com.combintech.baojili.util.ImageHelper;

import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public class LocationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<BJLocation> locationArrayList;
    private boolean isManager;

    private static final int EMPTY_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    private OnAdapterItemClickedListener listener;
    public interface OnAdapterItemClickedListener{
        void onAdapterItemClicked(BJLocation bjLocation);
        void onAdapterItemDeleteClicked(BJLocation bjLocation);
    }
    public LocationListAdapter(Context context, ArrayList<BJLocation> locationArrayList,
                               OnAdapterItemClickedListener listener,
                               boolean isManager) {
        this.context = context;
        setLocationList(locationArrayList);
        this.listener = listener;
        this.isManager = isManager;
    }

    public void setLocationList(ArrayList<BJLocation> locationArrayList) {
        if (locationArrayList == null) {
            this.locationArrayList = new ArrayList<>();
        } else {
            this.locationArrayList = locationArrayList;
        }
    }

    public ArrayList<BJLocation> getLocationArrayList() {
        return locationArrayList;
    }

    private class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvLocationName;
        TextView tvAddress;
        TextView tvType;

        View vDelete;

        private LocationViewHolder(View itemView) {
            super(itemView);
            this.tvLocationName = itemView.findViewById(R.id.tv_location_name);
            this.tvAddress = itemView.findViewById(R.id.tv_address);
            this.tvType = itemView.findViewById(R.id.tv_type);
            this.vDelete = itemView.findViewById(R.id.iv_delete);
            if (isManager) {
                vDelete.setVisibility(View.VISIBLE);
                vDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        BJLocation location = locationArrayList.get(position);
                        if (listener!=null) {
                            listener.onAdapterItemDeleteClicked(location);
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
            BJLocation location = locationArrayList.get(position);
            if (listener!=null) {
                listener.onAdapterItemClicked(location);
            }
        }

        public void bind(BJLocation location){
            tvLocationName.setText(location.getName());

            if (TextUtils.isEmpty(location.getAddress())){
                tvAddress.setVisibility(View.GONE);
            } else {
                tvAddress.setText(location.getAddress());
                tvAddress.setVisibility(View.VISIBLE);
            }

            tvType.setText(location.getType());
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void removeLocation (String locationIdToRemove){
        for (int i =0, sizei = locationArrayList.size(); i<sizei; i++) {
            BJLocation location = locationArrayList.get(i);
            if (location.getLocationID().equals(locationIdToRemove)){
                locationArrayList.remove(i);
                this.notifyItemRemoved(i);
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
            case ITEM_TYPE: {
                View view = inflater.inflate(R.layout.item_location, parent, false);
                return new LocationViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LocationViewHolder) {
            BJLocation location = locationArrayList.get(position);

            LocationViewHolder locationViewHolder = (LocationViewHolder) holder;
            locationViewHolder.bind(location);
        }
    }

    @Override
    public int getItemCount() {
        return locationArrayList.size() == 0? 1: locationArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (locationArrayList.size() == 0) {
            return EMPTY_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}
