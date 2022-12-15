package com.sangnk.btl_mobi.Fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.sangnk.btl_mobi.Adapter.NotificationAdapter;
import com.sangnk.btl_mobi.Adapter.UserAdapter;
import com.sangnk.btl_mobi.Model.Notification;
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

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Notification>  mNotifications;
    private NotificationAdapter notificationAdapter;

    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    private int page = 0;
    private int size = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_notification);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(getContext());
        //creating array list for getting user n'd setting on search bar
        mNotifications = new ArrayList<>();

        notificationAdapter = new NotificationAdapter(getContext(),mNotifications,true);
        recyclerView.setAdapter(notificationAdapter);

        readNotification();
        // Inflate the layout for this fragment
        return view;
    }

    private void readNotification() {
        /*
        * params: {
                        search: JSON.stringify(apiParam),
                        page: body.page || 0,
                        size: body.size || 10,
                        sort: 'modifiedDate,desc',
                    }
        * */
        Map<String, Object> searchParam = new ArrayMap<>();


        Call<ResponseBody> call = apiInterface.getNotify((new JSONObject(searchParam)).toString(), page, size, "updateTime,desc");
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
                                JSONObject bean = content.getJSONObject(i);
                                Notification notification = new Notification();

                                notification.setContent(bean.getString("content"));
                                notification.setCreateTime(bean.getString("createTimeStr"));

                                mNotifications.add(notification);
                            }

                        } else {
                            Toast.makeText(getContext(), "Load Data faild", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
}