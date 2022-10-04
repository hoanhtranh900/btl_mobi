package com.mrash.instagramclone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.mrash.instagramclone.Fragments.PostDetailFragment;
import com.mrash.instagramclone.Model.Post;
import com.mrash.instagramclone.R;
import com.mrash.instagramclone.network.ApiClient;
import com.mrash.instagramclone.network.ApiInterface;
import com.mrash.instagramclone.utils.H;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private static final String TAG = "PostAdapter";

    private Context mContext;
    private List<Post> mPosts;
    private FirebaseUser firebaseUser;

    //Constructor of Post Adapter
    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Started ");


        // get post from post array on the base of position
        Post post = mPosts.get(position);
        //picasso is used to process/load image
        if (H.isTrue(post.getPostImageUrl())) {
            //rezise image max width of device
            Picasso.get().load(post.getPostImageUrl()).into(holder.postImage);

        } else {
            //hide image view if there is no image
            holder.postImage.setVisibility(View.GONE);
        }

        holder.description.setText(post.getDescription());
        holder.fullName.setText(post.getUser().getFullName());
        holder.auther.setText(post.getUser().getUsername());
        holder.datecreate.setText(post.getDatecreate());
        holder.noOfLikes.setText(post.getTotalLike() + " Likes");
        if (H.isTrue(post.getUser().getAvatar())) {
            Picasso.get().load(post.getUser().getAvatar()).into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.ic_person);
        }


        //getting data of post
//        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
//                .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d(TAG, "onDataChange: Getting data from User and setting post");
//                User user = snapshot.getValue(User.class);
//
//                //I have set automatically image url to default so user have to update its pic first time from profile setting
//                if(user.getImageurl().equals("default"))
//                {
//                    holder.imgProfile.setImageResource(R.mipmap.ic_launcher);
//                }
//                else
//                    {
//                        //load the imageurl
//                        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imgProfile);
//                    }
//                holder.username.setText(user.getUsername());
//                holder.auther.setText(user.getName());
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        // function to check if post is liked or not
        isLiked(post.getPostid(), holder.like);

        //if user click on the like button then it will update the post which is liked and
        // add data of user who like it
        //and at the same time if user unlike the post then remove the value from firebase


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<ResponseBody> response = apiInterface.postLike(Long.valueOf(post.getPostid()));
                response.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: Post Liked");
                            JSONObject jsonObject1 = null;
                            try {
                                jsonObject1 = new JSONObject(response.body().string());
                                if (jsonObject1.getString("code").equals("200")) {
                                    JSONObject data = jsonObject1.getJSONObject("data");
                                    Long isDeleted = data.getLong("isDelete");
                                    if (isDeleted == 1L) {
                                        //set like icon
                                        holder.like.setImageResource(R.drawable.ic_like);
                                        //set total like - 1 if > 0
                                        if (post.getTotalLike() > 0) {
                                            holder.noOfLikes.setText(post.getTotalLike() - 1 + " Likes");
                                        } else {
                                            holder.noOfLikes.setText("0 Likes");
                                        }
                                    } else {
                                        //set unlike icon
                                        holder.like.setImageResource(R.drawable.ic_liked);
                                        //set total like + 1
                                        holder.noOfLikes.setText(post.getTotalLike() + 1 + " Likes");

                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });


            }
        });


        //when user click on postImage of anyPost,open that post and replace that main Activity Container Layout
        // with post-detail fragment to show post
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid())
                        .apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

    }//onbind end


    /**
     * Check if the post is liked or not already
     *
     * @param poitId
     * @param imageView
     */

    //if post is already liked by current user then clicking on liked button will unlike it and vice versa...
    private void isLiked(String poitId, ImageView imageView) {
        Log.d(TAG, "isLiked: Checking if post is liked or not");
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> response = apiInterface.getLike(Long.valueOf(poitId));
        response.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Post Liked");
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(response.body().string());
                        if (jsonObject1.getString("code").equals("200")) {

                            if (H.isTrue(jsonObject1.get("data").toString())) {
                                imageView.setImageResource(R.drawable.ic_liked);
                            } else {
                                imageView.setImageResource(R.drawable.ic_like);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProfile;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;
        public TextView fullName;

        public TextView username;
        public TextView noOfLikes;
        public TextView auther;
        public TextView noOfComments;
        public TextView datecreate;

        SocialTextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);
            username = itemView.findViewById(R.id.username);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            auther = itemView.findViewById(R.id.author);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            description = itemView.findViewById(R.id.description);
            fullName = itemView.findViewById(R.id.full_name);
            datecreate = itemView.findViewById(R.id.datecreate);


        }


    }
}
