package com.mrash.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.mrash.instagramclone.Model.Inbox;
import com.mrash.instagramclone.Model.Post;
import com.mrash.instagramclone.R;
import com.mrash.instagramclone.utils.H;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder>  {
    private static final String TAG = "InboxAdapter";

    private Context mContext;
    private List<Inbox> mInbox;

    public InboxAdapter(Context mContext, List<Inbox> mInbox) {
        this.mContext = mContext;
        this.mInbox = mInbox;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inbox_item, parent, false);
        return new InboxAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inbox inbox = mInbox.get(position);
        holder.fullName.setText(inbox.getFullname());
        holder.content.setText(inbox.getLastMessage());
        holder.datecreate.setText(inbox.getLastMessageTime());
        if(H.isTrue(inbox.getAvatar())) {
            Picasso.get().load(inbox.getAvatar()).into(holder.imgProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mInbox.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProfile;
        public TextView fullName;
        public TextView numOfUnreadMessages;
        public TextView datecreate;
        public TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.image_profile_demo_inbox);
            content = itemView.findViewById(R.id.content_demo_inbox);
            fullName = itemView.findViewById(R.id.full_name_demo_inbox);
            datecreate = itemView.findViewById(R.id.content_demo_last_inbox);


        }


    }
}
