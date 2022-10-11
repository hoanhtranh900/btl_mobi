package com.mrash.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mrash.instagramclone.network.ApiClient;
import com.mrash.instagramclone.network.ApiInterface;
import com.mrash.instagramclone.network.response.UserResponse;
import com.mrash.instagramclone.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText username;
    private EditText password;
    private Button login;
    private TextView registerNow;

    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    SharedPrefManager sharedPrefManager;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //khởi tạo sau khi activity kích hoạt
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started");
        init();
        setLogin();
        goRegisterYourself();
    }

    //init for view connection
    private void init() {
        //tìm kiếm view theo id
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        registerNow = findViewById(R.id.register_now);
        mAuth = FirebaseAuth.getInstance();
        //khởi tại apiInterface
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);

    }

    // if user is not register then goto register activity and signUp First
    private void goRegisterYourself() {
        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, com.mrash.instagramclone.RegisterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });

    }

    //set Login Button ( action khi click vào nút login)
    private void setLogin() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String txtusername = username.getText().toString().trim();
                    String txtPassword = password.getText().toString().trim();
                    try {
                        loginUser(txtusername, txtPassword);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //login user called in set login to check username and password with firebase auth
    private void loginUser(String username, String password) throws JSONException {
        progressDialog.show();

        Map<String, Object> paramObject = new ArrayMap<>();
        paramObject.put("username", username);
        paramObject.put("password", password);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramObject)).toString());
        Call<ResponseBody> postLogin = apiInterface.postLogin(body);
        postLogin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("code").equals("200")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONObject accessTokenInfo = data.getJSONObject("accessTokenInfo");
                            String accessToken = accessTokenInfo.getString("accessToken");
                            sharedPrefManager.saveSPString(SharedPrefManager.SP_TOKEN, accessToken);
                            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN_STATUS, true);
                            sharedPrefManager.saveSPLong(SharedPrefManager.SP_USER_ID, data.getLong("userId"));

                            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //Validate func() used in setlogin() to validate edit text fields
    private boolean validate() {
        String txtusername = username.getText().toString().trim();
        String txtPassword = password.getText().toString().trim();
        boolean flag = true;
        if (TextUtils.isEmpty(txtusername)) {
            username.setError("Enter username");
            flag = false;
        }
        if (TextUtils.isEmpty(txtPassword)) {
            password.setError("Enter Password");
            flag = false;
        }

        return flag;
    }

}