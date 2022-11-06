package com.sangnk.btl_mobi.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sangnk.btl_mobi.Adapter.PhotoAdapter;
import com.sangnk.btl_mobi.EditProfileActivity;
import com.sangnk.btl_mobi.Model.Post;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.OptionActivity;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.sangnk.btl_mobi.utils.H;
import com.sangnk.btl_mobi.utils.SharedPrefManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView fullName;
    private TextView bio;
    private TextView username;

    private ImageView myPictures;
    private ImageView savedPictures;
    private Button editProfile;

    private ApiInterface apiInterface;

    private Long profileId;
    private SharedPrefManager sharedPrefManager;

    private String imageProfileUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);



        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        editProfile = view.findViewById(R.id.edit_profile);
        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.followings);
        fullName = view.findViewById(R.id.full_name);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        myPictures = view.findViewById(R.id.my_pictures);
        savedPictures = view.findViewById(R.id.saved_pictures);
        recyclerView = view.findViewById(R.id.recycler_view_pictures);
        sharedPrefManager = new SharedPrefManager(this.getContext());

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        myPhotoList = new ArrayList<>();

        photoAdapter = new PhotoAdapter(getContext(), myPhotoList);

        recyclerView.setAdapter(photoAdapter);

        profileId = sharedPrefManager.getSPUserId();

        userInfo();

        getFollowerAndFollowingCount();

        getPostCount();

        getMyPhotos();

        //Setting Edit profile Button text at runtime
//      if(profileId.equals(fUser.getUid()))
//      {
//          editProfile.setText("Edit Profile");
//      }
//      else {
//          //uncomment when future work on follow or following profiles of other users
//       //   checkFollowingStatus();
//      }

        setEditProfile();

        //setting option so that we can logout and edit profile

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), OptionActivity.class));
            }
        });
        return view;
    }


    /**
     * Edit Profile Button
     */
    private void setEditProfile() {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = editProfile.getText().toString();
                if (buttonText.equals("Edit Profile 4")) {
                    // goto edit profile Activity
                    Log.i(TAG, "onClick: ");
//                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                    //send current user id to edit profile activity
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    intent.putExtra("id", profileId);
                    intent.putExtra("username", username.getText().toString());
                    intent.putExtra("fullname", fullName.getText().toString());
                    intent.putExtra("imageProfile", imageProfileUrl);
                    startActivity(intent);

                } else { // this is extra for the future more work for learning -> Just do nothing for now because
                }
            }
        });

    }

    /**
     * Get All photos on the base of current user profile id
     */
    private void getMyPhotos() {
        //Create Firebase database reference upto Posts then get getting post of that current user
//        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                myPhotoList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    //post is uploaded by current user then add to list
//                    if (post.getPublisher().equals(profileId)) {
//                        myPhotoList.add(post);
//
//                    }
//                }
//                //this will reverse the list of post like on Instagram showing new post at the top and then so-on..
//                Collections.reverse(myPhotoList);
//                photoAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        Map<String, Object> searchParam = new ArrayMap<>();
        searchParam.put("fullName", "");
        searchParam.put("id", profileId);
        int page = 0;
        int size = 100;


        Call<ResponseBody> call = apiInterface.getListPost((new JSONObject(searchParam)).toString(), page, size, "updateTime,desc");
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("code").equals("200")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            //get list from content
                            JSONArray content = data.getJSONArray("content");
                            for (int i = 0; i < content.length(); i++) {
                                JSONObject post = content.getJSONObject(i);
                                Post post1 = new Post();

                                post1.setPostid(post.getString("id"));
                                if(H.isTrue(post.getString("postImageUrl"))){
                                    post1.setPostImageUrl(post.getString("postImageUrl"));
                                }
                                post1.setDescription(post.getString("description"));
                                post1.setPublisher(post.getString("creatorName"));
                                post1.setDatecreate(post.getString("creteTimeStr"));
                                post1.setTotalLike(post.getLong("totalLike"));
//                                post1.setPublisherId(post.getString("creatorId"));
                                User user = new User();
                                //get user from creator in data
                                JSONObject creator = post.getJSONObject("creator");
                                //convert jsonObj to user
                                user = (User) H.convertJsonToObject(creator, User.class);
                                post1.setUser(user);


                                myPhotoList.add(post1);
                            }

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }




    private void getPostCount() {
        posts.setText(String.valueOf(10));
    }

    /**
     * Get Followers and Following Count using getChildren()
     */
    private void getFollowerAndFollowingCount() {
        followers.setText(String.valueOf(10));
    }

    /**
     * getting user info then set on profile Screen like username n'd profile pic n'd description...
     */
    private void userInfo() {


        Call<ResponseBody> call = apiInterface.getUser();
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("code").equals("200")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            Picasso.get().load(data.getString("avatar")).into(imageProfile);
                            username.setText(data.getString("username"));
                            fullName.setText(data.getString("fullName"));
                            imageProfileUrl = data.getString("avatar");



                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });


    }
}