package com.sangnk.btl_mobi;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sangnk.btl_mobi.Adapter.InboxDetailMessageListAdapter;
import com.sangnk.btl_mobi.Model.ChatMessage;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.sangnk.btl_mobi.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private InboxDetailMessageListAdapter mMessageAdapter;
    private List<ChatMessage> messageList;
    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;
    private OkHttpClient client;
    private StompClient mStompClient;
    private Disposable mRestPingDisposable;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private RecyclerView mRecyclerView;
    private Gson mGson = new GsonBuilder().create();
    private CompositeDisposable compositeDisposable;
    private List<String> mDataSet = new ArrayList<>();
    private Button mSendButton;
    private Long userId;
    private long currentUserId;
    private EditText mMessageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        messageList = new ArrayList<>();
        mMessageRecycler = (RecyclerView) findViewById(R.id.recycler_gchat1);
        mSendButton = (Button) findViewById(R.id.button_gchat_send);
        mMessageEditText = (EditText) findViewById(R.id.edit_gchat_message);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);

        mMessageAdapter = new InboxDetailMessageListAdapter(this, messageList);

        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        //get data from another activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getLong("userId");
            currentUserId = sharedPrefManager.getSPUserId();
        }

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://127.0.0.1:8023/ig-clone/ws");
        resetSubscriptions();
        connectStomp();
        sendMessage();
        loadListMessage(currentUserId, userId);
    }

    private void sendMessage() {
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat(userId, mMessageEditText.getText().toString());
//                loadListMessage(currentUserId, userId);
            }
        });
    }

    private void loadListMessage(long currentUserId, Long userId) {
        Call<ResponseBody> call = apiInterface.getListMessage(currentUserId, userId);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setChatId(object.getString("chatId"));
                            chatMessage.setSenderId(object.getString("senderId"));
                            chatMessage.setRecipientId(object.getString("recipientId"));
                            chatMessage.setContent(object.getString("content"));
                            chatMessage.setTimestamp(object.getString("sendDate"));
                            chatMessage.setSenderName(object.getString("senderName"));
                            chatMessage.setRecipientName(object.getString("recipientName"));

                            messageList.add(chatMessage);
                        }
                        mMessageAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    public void disconnectStomp(View view) {
        mStompClient.disconnect();
    }

    public static final String LOGIN = "login";

    public static final String PASSCODE = "passcode";

    public void connectStomp() {
        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);

        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            toast("Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            toast("Stomp connection error");
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            toast("Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        // Receive greetings
        Disposable dispTopic = mStompClient.topic("/user/" + sharedPrefManager.getSPUserId() + "/queue/messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received " + topicMessage.getPayload());
                    addItem(mGson.fromJson(topicMessage.getPayload(), ChatMessage.class));
                }, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });

        compositeDisposable.add(dispTopic);

        mStompClient.connect();
        loadListMessage(currentUserId, userId);
    }

    private void addItem(ChatMessage fromJson) {
        messageList.add(fromJson);
        mMessageAdapter.notifyDataSetChanged();

        //move to bottom
        mMessageRecycler.scrollToPosition(messageList.size() - 1);
    }

    public void chat(Long recipientId, String content) {
        String chatId = sharedPrefManager.getSPUserId() + "-" + recipientId;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatId(chatId);
        chatMessage.setSenderId(String.valueOf(sharedPrefManager.getSPUserId()));
        chatMessage.setRecipientId(String.valueOf(recipientId));
        chatMessage.setContent(content);

        mStompClient.send("/app/chat", mGson.toJson(chatMessage))
                .compose(applySchedulers())
                .subscribe(() -> {
                    Log.d(TAG, "STOMP echo send successfully");
                }, throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                });

        //clear edit text
        mMessageEditText.setText("");
        //update list message
        messageList.add(chatMessage);
        mMessageAdapter.notifyDataSetChanged();

        //auto scroll to bottom
        mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount() - 1);
    }

//        Map<String, Object> message = new ArrayMap<>();
//        message.put("senderId", sharedPrefManager.getSPUserId());
//
//
//
//        compositeDisposable.add(mStompClient.send("/app/chat", "Echo STOMP " + mTimeFormat.format(new Date()))
//                .compose(applySchedulers())
//                .subscribe(() -> {
//                    Log.d(TAG, "STOMP echo send successfully");
//                }, throwable -> {
//                    Log.e(TAG, "Error send STOMP echo", throwable);
//                    toast(throwable.getMessage());
//                }));
//    }



    private void toast(String text) {
        Log.i(TAG, text);
//        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        mStompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
        super.onDestroy();
    }
}