package com.sangnk.btl_mobi.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sangnk.btl_mobi.Adapter.PostAdapter;
import com.sangnk.btl_mobi.Model.Post;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.sangnk.btl_mobi.utils.H;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;


public class PostDetailFragment extends Fragment {

    private String postId;
    private String profileId;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private ApiInterface apiInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                .getString("postid","none");

        //attach RecyclerView of Fragment_post_detail
        recyclerView = view.findViewById(R.id.recycler_view);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();

        //calling to post adapter again and setting that single post
        postAdapter = new PostAdapter(getContext(),postList);

        recyclerView.setAdapter(postAdapter);

//        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        postList.clear();
//                        postList.add(snapshot.getValue(Post.class));
//                        postAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


        //call api to get post detail
        apiInterface.getDetailPost(postId).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("code").equals("200")) {
                            JSONObject post = jsonObject.getJSONObject("data");
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
                            postList.clear();
                            postList.add(post1);
                            postAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


//                    postList.clear();
//                    postList.add(response.body());
//                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });



        return view;
    }


}