package com.sangnk.btl_mobi.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.sangnk.btl_mobi.Adapter.TagAdapter;
import com.sangnk.btl_mobi.Adapter.UserAdapter;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.sangnk.btl_mobi.utils.H;
import com.sangnk.btl_mobi.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView searchBar;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    private int page = 0;
    private int size = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //attaching rv of user to show on search bar
        recyclerView = view.findViewById(R.id.recycler_view_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(getContext());
        //creating array list for getting user n'd setting on search bar
        mUsers = new ArrayList<>();

        userAdapter = new UserAdapter(getContext(),mUsers,true);
        recyclerView.setAdapter(userAdapter);

        searchBar = view.findViewById(R.id.search_bar);

        readUsers();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: " + s);

                //when searching search string starting with that characters
//                searchUser(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {
                //
                filter(s.toString());

            }
        });


        return view;
    }

    private void filter(String text) {
        Log.d(TAG, "filter: "+text);
    }

    private void readUsers() {
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


        Call<ResponseBody> call = apiInterface.getListUser((new JSONObject(searchParam)).toString(), page, size, "updateTime,desc");
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
                                JSONObject user = content.getJSONObject(i);
                                User user1 = new User();

                                if(H.isTrue(user.getString("avatar"))){
                                    user1.setAvatar(user.getString("avatar"));
                                }
                                user1.setUsername(user.getString("username"));
                                user1.setFullName(user.getString("fullName"));
                                user1.setId(user.getLong("id"));
                                user1.setAddress(user.getString("address"));
                                if(user1.getId() != sharedPrefManager.getSPUserId()) {
                                    mUsers.add(user1);
                                }
                            }

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    //search user
    private void searchUser(String keySearch)
    {
        //Query is subclass of Firebase Database Reference
        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username")
                .startAt(keySearch).endAt(keySearch + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}