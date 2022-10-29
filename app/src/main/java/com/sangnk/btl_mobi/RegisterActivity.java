package com.sangnk.btl_mobi;

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

import androidx.appcompat.app.AppCompatActivity;

import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.sangnk.btl_mobi.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText username;
    private EditText name;
    private EditText password;
    private Button register;
    private TextView alreadyHaveAccount;

    private ProgressDialog progressDialog;
    private ApiInterface apiInterface;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: Started");

        init();
        alreadyHaveAccount();
        setBtnRegister();
    }

    //setButon REgister
    private void setBtnRegister() {
        Log.d(TAG, "setBtnRegister: register Button Set");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    String txtUsername = username.getText().toString().trim();
                    String txtName = name.getText().toString().trim();
                    String txtPassword = password.getText().toString().trim();
                    // registering user in firebase database
                    Log.d(TAG, "onClick: Calling register User Function in register Button");


                    registerUser(txtUsername, txtName, txtPassword);
                }

            }
        });
    }

    private void registerUser(String username, String name, String password) {
        Log.d(TAG, "registerUser: Registering User Called");


        progressDialog.setMessage("please wait");
        progressDialog.show();

        Map<String, Object> paramObject = new ArrayMap<>();
        paramObject.put("fullName", name);
        paramObject.put("username", username);
        paramObject.put("password", password);


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramObject)).toString());
        Call<ResponseBody> postRegister = apiInterface.postRegister(body);
        postRegister.enqueue(callbackRegister(username, password));


    }
    private Callback<ResponseBody> callbackRegister( String username, String password) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getString("code").equals("200")) {


                            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                            Map<String, Object> paramObject = new ArrayMap<>();
                            paramObject.put("username", username);
                            paramObject.put("password", password);
                            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramObject)).toString());
                            Call<ResponseBody> postLogin = apiInterface.postLogin(body);
                            postLogin.enqueue(callbackLogin());


                        } else {
                            Toast.makeText(RegisterActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (response.code() == 400) {
                    //Log message response.message()
                    try {
                        //get error message call "message" from response
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(RegisterActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(RegisterActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        };
    };

    private Callback<ResponseBody> callbackLogin() {
        return new Callback<ResponseBody>() {
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
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        };
    }


    //validation function for edit text
    private boolean validate() {
        Log.d(TAG, "validate: Going to validate editTexts");
        boolean flag = true;
        String txtUsername = username.getText().toString().trim();
        String txtName = name.getText().toString().trim();
        String txtPassword = password.getText().toString().trim();
        if (TextUtils.isEmpty(txtUsername)) {
            username.setError("Enter username");
            flag = false;
        }

        if (TextUtils.isEmpty(txtName)) {
            name.setError("Enter name");
            flag = false;

        }
        if (TextUtils.isEmpty(txtPassword)) {
            password.setError("Enter password");
            flag = false;
        }
        if (txtPassword.length() < 8) {
            password.setError("Password must be at least 8 character long");
            flag = false;
        }

        return flag;
    }

    //Init View Method
    private void init() {
        Log.d(TAG, "init: Started");
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        alreadyHaveAccount = findViewById(R.id.already_have_account);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sharedPrefManager = new SharedPrefManager(this);


        progressDialog = new ProgressDialog(this);
    }

    //Intent take action to Login Activity Screen
    private void alreadyHaveAccount() {
        Log.d(TAG, "alreadyHaveAccount: Already have and account clicked");
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, com.sangnk.btl_mobi.LoginActivity.class));
            }
        });
    }

}