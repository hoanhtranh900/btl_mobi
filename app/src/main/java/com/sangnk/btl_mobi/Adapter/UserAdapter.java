package com.sangnk.btl_mobi.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private static final String TAG = "UserAdapter";
    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Started");
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        Log.d(TAG, "onCreateViewHolder: ");
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Started");
        firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();

        User user = mUsers.get(position);
        holder.follow.setVisibility(View.VISIBLE);
        holder.userName.setText(user.getUsername());
        holder.fullName.setText(user.getFullName());
        //Load pic and set as profile image
        Picasso.get().load(user.getAvatar()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);

        if(user.getId().equals(firebaseUser.getUid()))
        {
            holder.follow.setVisibility(View.GONE);
        }
        //database ->create a branch called follow then under that id current user
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: follow Button");
                //here setting follow and following button
                if(holder.follow.getText().toString().equals("follow"))
                {
                    Log.d(TAG, "onClick: text is Follow");
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child((firebaseUser.getUid()))
//                            .child("following").child(user.getId()).setValue(true);
//
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
                }
                else
                {
                    Log.d(TAG, "onClick: text is following");
//                    FirebaseDatabase.getInstance().getReference().child("Follow").child((firebaseUser.getUid()))
//                            .child("following").child(user.getId()).removeValue();
//
//                    FirebaseDatabase.getInstance().getReference().child("Follow")
//                            .child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        isFollowed(user.getId(),holder.follow);

    }


    /**
     * check and see if user is following and followed and set text according to it
     * @param id
     * @param follow
     */

    private void isFollowed(Long id, Button follow) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                .child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
//                if(snapshot.child(id).exists())
//                {
//                    follow.setText("following");
//                }
//                else
//                    follow.setText("follow");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView imageProfile;
        public TextView userName;
        public TextView fullName;
        public Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            init();
        }
        private void init()
        {
            imageProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.full_name);
            follow  = itemView.findViewById(R.id.btn_follow);
        }
    }
}