package com.sangnk.btl_mobi.Adapter;

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
import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.sangnk.btl_mobi.Fragments.CommentFragment;
import com.sangnk.btl_mobi.Fragments.PostDetailFragment;
import com.sangnk.btl_mobi.Fragments.SearchUserFragment;
import com.sangnk.btl_mobi.Model.Post;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.sangnk.btl_mobi.utils.H;
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



        // function to check if post is liked or not
        isLiked(post.getPostid(), holder.like);

        //if user click on the like button then it will update the post which is liked and
        // add data of user who like it
        //and at the same time if user unlike the post then remove the value from firebase

        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to profile fragment
                mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putLong("profileid", post.getUser().getId()).apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });


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
                                    Long totalLike = data.getLong("totalLike");
                                    if (isDeleted == 1L) {
                                        //set like icon
                                        holder.like.setImageResource(R.drawable.ic_like);
                                        //set total like - 1 if > 0
                                        if (post.getTotalLike() > 0) {
                                            holder.noOfLikes.setText(totalLike + " Likes");
                                        } else {
                                            holder.noOfLikes.setText("0 Likes");
                                        }
                                    } else {
                                        //set unlike icon
                                        holder.like.setImageResource(R.drawable.ic_liked);
                                        //set total like + 1
                                        holder.noOfLikes.setText(totalLike + " Likes");

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


        //move to detail
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid())
                        .apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        //move to comment
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("postid", post.getPostid())
                        .apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new CommentFragment()).addToBackStack(null).commit();


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
