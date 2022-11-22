package com.sangnk.btl_mobi.Adapter;

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

import com.sangnk.btl_mobi.MessageListActivity;
import com.sangnk.btl_mobi.Model.Comment;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.utils.H;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private static final String TAG = "CommentAdapter";

    private Context mContext;
    private List<Comment> comments;

    public CommentAdapter(Context mContext, List<Comment> comments) {
        this.mContext = mContext;
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.inbox_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.fullName.setText(comment.getUser().getFullName());
        holder.time.setText(comment.getCreateTimeStr());
        holder.content.setText(comment.getBody());
        if (H.isTrue(comment.getUser()) && H.isTrue(comment.getUser().getAvatar())) {
            Picasso.get().load(comment.getUser().getAvatar()).into(holder.imgProfile);
        }

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgProfile;
        public TextView fullName;
        public TextView content;
        public TextView time;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.image_profile_demo_inbox);
            content = itemView.findViewById(R.id.content_demo_inbox);
            fullName = itemView.findViewById(R.id.full_name_demo_inbox);
            time = itemView.findViewById(R.id.content_demo_last_inbox);
        }
    }
}