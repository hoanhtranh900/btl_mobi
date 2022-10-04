package com.mrash.instagramclone.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrash.instagramclone.Adapter.InboxAdapter;
import com.mrash.instagramclone.Model.Inbox;
import com.mrash.instagramclone.R;
import com.mrash.instagramclone.network.ApiClient;
import com.mrash.instagramclone.network.ApiInterface;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {
    private static final String TAG = "InboxFragment";
    private RecyclerView recyclerViewInbox;
    private List<Inbox> inboxList;
    private ApiInterface apiInterface;
    private InboxAdapter inboxAdapter;
    private ImageView inbox_demo_back;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Started");
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        recyclerViewInbox = view.findViewById(R.id.recycler_view_inbox);
        recyclerViewInbox.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        inboxList = new ArrayList<>();
        recyclerViewInbox.setLayoutManager(linearLayoutManager);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        inbox_demo_back = view.findViewById(R.id.inbox_demo_back);
        inboxAdapter = new InboxAdapter(getContext(), inboxList);
        recyclerViewInbox.setAdapter(inboxAdapter);

        getListInbox();
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

    private void getListInbox() {
        Log.d(TAG, "getListInbox: Started");
    }
}
