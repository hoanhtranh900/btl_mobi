package com.sangnk.btl_mobi.Fragments;

import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangnk.btl_mobi.Adapter.SearchUserAdapter;
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

public class SearchUserFragment extends Fragment {
    private static final String TAG = "SearchUserFragment";
    private RecyclerView recyclerViewInbox;
    private List<User> userList;
    private ApiInterface apiInterface;
    private SearchUserAdapter searchUserAdapter;
    private ImageView inbox_demo_back;
    private int page = 0;
    private int size = 1000;
    private SharedPrefManager sharedPrefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Started");
        View view = inflater.inflate(R.layout.fragment_search_user_to_chat, container, false);
        recyclerViewInbox = view.findViewById(R.id.recycler_list_user);
        recyclerViewInbox.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        sharedPrefManager = new SharedPrefManager(getContext());
        userList = new ArrayList<>();
        recyclerViewInbox.setLayoutManager(linearLayoutManager);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        inbox_demo_back = view.findViewById(R.id.inbox_demo_back);
        searchUserAdapter = new SearchUserAdapter(getContext(), userList);
        recyclerViewInbox.setAdapter(searchUserAdapter);

        getListUser();
        backTap();
        return view;
    }

    private void backTap() {
        inbox_demo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void getListUser() {
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
                                    userList.add(user1);
                                }
                            }

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                searchUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }
}