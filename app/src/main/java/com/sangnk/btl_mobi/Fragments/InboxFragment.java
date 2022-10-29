package com.sangnk.btl_mobi.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sangnk.btl_mobi.Adapter.InboxAdapter;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class InboxFragment extends Fragment {
    private static final String TAG = "InboxFragment";
    private RecyclerView recyclerViewInbox;
    private List<User> userList;
    private ApiInterface apiInterface;
    private InboxAdapter inboxAdapter;
    private ImageView inbox_demo_back;
    private TextView search_user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Started");
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        recyclerViewInbox = view.findViewById(R.id.recycler_view_inbox);
        recyclerViewInbox.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        userList = new ArrayList<>();
        recyclerViewInbox.setLayoutManager(linearLayoutManager);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        inbox_demo_back = view.findViewById(R.id.inbox_demo_back);
        search_user = view.findViewById(R.id.search_input_text_move_to_search_user);
        inboxAdapter = new InboxAdapter(getContext(), userList);
        recyclerViewInbox.setAdapter(inboxAdapter);

        getListInbox();
        tap();
        return view;
    }

    private void tap() {
        inbox_demo_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        search_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchUserFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void getListInbox() {
//        apiInterface.getListChatRecent()

        Call<ResponseBody> call = apiInterface.getListChatRecent();
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("code").equals("200")) {
//                            JSONObject data = jsonObject.getJSONObject("data");
                            //get list from content
                            JSONArray content = jsonObject.getJSONArray("data");
                            for (int i = 0; i < content.length(); i++) {
                                JSONObject object = content.getJSONObject(i);
                                User user = new User();
                                user.setId(object.getLong("id"));
                                user.setUsername(object.getString("username"));
                                user.setFullName(object.getString("fullName"));
                                user.setAvatar(object.getString("avatar"));

                                userList.add(user);
                            }

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                        inboxAdapter.notifyDataSetChanged();
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
