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
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.User;
import com.combintech.baojili.util.ImageHelper;
import com.combintech.baojili.util.KMNumbers;

import java.util.ArrayList;

/**
 * Created by Hendry Setiadi
 */

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<User> userArrayList;
    private boolean isManager;
    private String userID;

    private static final int EMPTY_TYPE = 0;
    private static final int ITEM_TYPE = 1;

    private OnAdapterItemClickedListener listener;
    public interface OnAdapterItemClickedListener{
        void onAdapterItemClicked(User user);
        void onAdapterItemDeleteClicked(User user);
    }
    public UserListAdapter(Context context, ArrayList<User> userArrayList,
                           OnAdapterItemClickedListener listener,
                           boolean isManager, String userID) {
        this.context = context;
        setUserList(userArrayList);
        this.listener = listener;
        this.isManager = isManager;
        this.userID = userID;

    }

    public void setUserList(ArrayList<User> userArrayList) {
        if (userArrayList == null) {
            this.userArrayList = new ArrayList<>();
        } else {
            this.userArrayList = userArrayList;
        }
    }

    public ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    private class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPhoto;
        TextView tvUserId;
        TextView tvName;
        TextView tvEmail;
        TextView tvRole;

        View vDelete;

        private UserViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = itemView.findViewById(R.id.iv_photo);
            this.tvUserId = itemView.findViewById(R.id.tv_username);
            this.tvName = itemView.findViewById(R.id.tv_name);
            this.tvEmail = itemView.findViewById(R.id.tv_email);
            this.tvRole = itemView.findViewById(R.id.tv_role);

            this.vDelete = itemView.findViewById(R.id.iv_delete);
            if (isManager) {
                vDelete.setVisibility(View.VISIBLE);
                vDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        User user = userArrayList.get(position);
                        if (listener!=null) {
                            listener.onAdapterItemDeleteClicked(user);
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
            User user = userArrayList.get(position);
            if (listener!=null) {
                listener.onAdapterItemClicked(user);
            }
        }

        public void bind(User user){
            if (TextUtils.isEmpty(user.getPhoto())){
                ivPhoto.setImageResource(R.drawable.no_image);
            } else {
                ImageHelper.loadImageFitCenter(context, user.getPhoto(), ivPhoto);
            }

            tvUserId.setText(user.getUserID());

            if (TextUtils.isEmpty(user.getName())){
                tvName.setText("-");
            } else {
                tvName.setText(user.getName());
            }

            if (TextUtils.isEmpty(user.getEmail())){
                tvEmail.setText("-");
            } else {
                tvEmail.setText(user.getEmail());
            }

            if (TextUtils.isEmpty(user.getRole())){
                tvRole.setText("-");
            } else {
                tvRole.setText(user.getRole());
            }

            if (isManager && !UserListAdapter.this.userID.equals(user.getUserID())) {
                vDelete.setVisibility(View.VISIBLE);
            } else {
                vDelete.setVisibility(View.GONE);
            }
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {
        private EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void removeUser (String userIdToRemove){
        for (int i =0, sizei = userArrayList.size(); i<sizei; i++) {
            User user = userArrayList.get(i);
            if (user.getUserID().equals(userIdToRemove)){
                userArrayList.remove(i);
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
                View view = inflater.inflate(R.layout.item_user, parent, false);
                return new UserViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            User user = userArrayList.get(position);

            UserViewHolder userViewHolder = (UserViewHolder) holder;
            userViewHolder.bind(user);
        }
    }

    @Override
    public int getItemCount() {
        return userArrayList.size() == 0? 1: userArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (userArrayList.size() == 0) {
            return EMPTY_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}
