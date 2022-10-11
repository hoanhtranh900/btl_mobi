package com.mrash.instagramclone.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrash.instagramclone.Adapter.PostAdapter;
import com.mrash.instagramclone.LoginActivity;
import com.mrash.instagramclone.MainActivity;
import com.mrash.instagramclone.Model.Post;
import com.mrash.instagramclone.Model.User;
import com.mrash.instagramclone.R;
import com.mrash.instagramclone.network.ApiClient;
import com.mrash.instagramclone.network.ApiInterface;
import com.mrash.instagramclone.utils.H;
import com.mrash.instagramclone.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

//thứ tự: onCreateView, onStart, onResume
//adapter: đưa dữ liệu vào recyclerview
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;
    private int page = 0;
    private int size = 10;
    private ApiInterface apiInterface;
    private ImageView inbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Started");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //initializing PostList
        postList = new ArrayList<>();
        //initializing following List - >will add to this if person has follow someone
        followingList = new ArrayList<>();
        inbox = view.findViewById(R.id.inbox);
        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);

        recyclerViewPosts.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        // new post at top

        //setting linear layout of post on recycler view
        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //setting posts on adapter
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerViewPosts.setAdapter(postAdapter);

        //this will check for those following peoples
        getPost();
        moveToInbox();
        return view;
    }

    private void moveToInbox() {
        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //move to fragment inbox
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InboxFragment()).addToBackStack(null).commit();
            }
        });
    }

    /**
     * Check if User is following Someone
     */
    private void getPost() {
        Log.d(TAG, "getListPost");
        /*
        * params: {
                        search: JSON.stringify(apiParam),
                        page: body.page || 0,
                        size: body.size || 10,
                        sort: 'modifiedDate,desc',
                    }
        * */
        Map<String, Object> searchParam = new ArrayMap<>();
        searchParam.put("fullName", "");


        Call<ResponseBody> call = apiInterface.getFollowing((new JSONObject(searchParam)).toString(), page, size, "updateTime,desc");
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


                                postList.add(post1);
                            }

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    /**
     * Checking and Reading Follow people post and set it on Home Screen using post Adapter
     */
    private void readPosts() {
//        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                postList.clear();
//                for(DataSnapshot dataSnapshot:snapshot.getChildren())
//                {
//                    Post post = dataSnapshot.getValue(Post.class);
//                    for(String id : followingList)
//                    {
//                        if(post.getPublisher().equals(id))
//                        {
//                            postList.add(post);
//                        }
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    //log onresume, onpause, onstart, onstop, ondestroy
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Called");
    }

}