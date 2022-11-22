package com.sangnk.btl_mobi.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sangnk.btl_mobi.Fragments.CommentFragment;
import com.sangnk.btl_mobi.Fragments.ProfileFragment;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Started");
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        Log.d(TAG, "onCreateViewHolder: ");
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Started");
//        firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();

        User user = mUsers.get(position);
        holder.follow.setVisibility(View.VISIBLE);
        holder.userName.setText(user.getUsername());
        holder.fullName.setText(user.getFullName());
        //Load pic and set as profile image
        Picasso.get().load(user.getAvatar()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);


        //database ->create a branch called follow then under that id current user
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                Call<ResponseBody> response = apiInterface.followUser(user.getId());
                response.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "onResponse: " + response.body());
                            JSONObject jsonObject1 = null;
                            try {
                                jsonObject1 = new JSONObject(response.body().string());
                                if (jsonObject1.getString("code").equals("200")) {
                                    Boolean data = jsonObject1.getBoolean("data");
                                    if (data) {
                                        holder.follow.setText("Đang theo dõi");
                                    } else {
                                        holder.follow.setText("Theo dõi");
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

        isFollowed(user.getId(), holder.follow);
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit().putString("userId", String.valueOf(user.getId()))
                        .apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ProfileFragment()).addToBackStack(null).commit();


            }
        });
    }


    private void isFollowed(Long id, Button follow) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseBody> response = apiInterface.checkFollow(id);
        response.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    JSONObject jsonObject1 = null;
                    try {
                        jsonObject1 = new JSONObject(response.body().string());
                        if (jsonObject1.getString("code").equals("200")) {
                            Boolean data = jsonObject1.getBoolean("data");
                            if (data) {
                                follow.setText("Đang theo dõi");
                            } else
                                follow.setText("Theo dõi");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imageProfile;
        public TextView userName;
        public TextView fullName;
        public Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            init();
        }

        private void init() {
            imageProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.full_name);
            follow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
