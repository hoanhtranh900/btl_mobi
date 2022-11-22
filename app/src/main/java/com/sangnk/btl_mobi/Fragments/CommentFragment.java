package com.sangnk.btl_mobi.Fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.sangnk.btl_mobi.Adapter.CommentAdapter;
import com.sangnk.btl_mobi.Adapter.TagAdapter;
import com.sangnk.btl_mobi.Adapter.UserAdapter;
import com.sangnk.btl_mobi.Model.Comment;
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
import retrofit2.Call;

public class CommentFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Comment> comments;
    private CommentAdapter commentAdapter;
    private String postId;
    private ApiInterface apiInterface;
    private ImageView closeBtn;
    private EditText commentText;
    private Button postBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        postId = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                .getString("postid","none");

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        recyclerView = view.findViewById(R.id.recycler_view_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        closeBtn = view.findViewById(R.id.close_comment_model);
        commentText = view.findViewById(R.id.comment_edit_text);
        postBtn = view.findViewById(R.id.comment_post_button);
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(),comments);

        recyclerView.setAdapter(commentAdapter);

        readComments();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(CommentFragment.this).commit();
            }
        });

        sendComment();

        return view;
    }

    private void sendComment(){
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(commentText.getText().toString())){
                    Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
                }else {
                    addComment();
                }
            }
        });
    }

    private void addComment(){
        String commentTxt= commentText.getText().toString();
        Call<ResponseBody> call = apiInterface.postComment(Long.parseLong(postId), commentTxt);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.body());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if (jsonObject.getString("code").equals("200")) {
                        JSONObject data = jsonObject.getJSONObject("data");


//                        reload list comment
                        comments.clear();
                        readComments();
                        commentText.setText("");


                    } else {
                        Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    private void readComments() {
        Call<ResponseBody> call = apiInterface.getComment(postId);
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
                                JSONObject i_comment = content.getJSONObject(i);
                                Comment comment = new Comment();
                                comment.setBody(i_comment.getString("body"));
                                comment.setCreateTimeStr(i_comment.getString("createTimeStr"));
                                User user = new User();
                                JSONObject creator = i_comment.getJSONObject("user");
                                //convert jsonObj to user
                                user = (User) H.convertJsonToObject(creator, User.class);
                                comment.setUser(user);


                                comments.add(comment);
                            }

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

}
