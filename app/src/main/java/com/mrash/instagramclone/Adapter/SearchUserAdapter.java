package com.mrash.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mrash.instagramclone.MessageListActivity;
import com.mrash.instagramclone.Model.User;
import com.mrash.instagramclone.R;
import com.mrash.instagramclone.utils.H;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    private static final String TAG = "SearchUserAdapter";

    private Context mContext;
    private List<User> mUsers;

    public SearchUserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new SearchUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.fullName.setText(user.getFullName());
        holder.username.setText(user.getUsername());
        if (H.isTrue(user.getAvatar())) {
            Picasso.get().load(user.getAvatar()).into(holder.image_profile);
        }

        //set onclick listener all in holder
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + user.getUsername());
                //move to activity MessageListActivity
                Long userId = user.getId();
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("userId", userId);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile;
        public TextView fullName;
        public TextView username;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            fullName = itemView.findViewById(R.id.full_name);
            username = itemView.findViewById(R.id.username);
        }
    }
}