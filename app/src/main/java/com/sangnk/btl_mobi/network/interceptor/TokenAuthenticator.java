package com.sangnk.btl_mobi.network.interceptor;

import android.content.Intent;

import com.sangnk.btl_mobi.LoginActivity;
import com.sangnk.btl_mobi.Myapp;
import com.sangnk.btl_mobi.utils.SharedPrefManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenAuthenticator implements Interceptor {

    SharedPrefManager sharedPrefManager;

    public TokenAuthenticator() {
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String authToken = "Bearer " + sharedPrefManager.getSPToken();
        Request mainRequest = chain.request();
        Request request = original.newBuilder()
                .header("Authorization", authToken)
                .method(original.method(), original.body()).build();
        Response mainResponse = chain.proceed(request);

        if ( mainResponse.code() == 401 || mainResponse.code() == 403 ) {
//            String token = sharedPrefManager.getSPToken();
//            retrofit2.Response<UserResponse> refreshToken = apiInterface.refreshToken(token).execute();
//            if (refreshToken.isSuccessful()) {
//                sharedPrefManager.saveSPString(SharedPrefManager.SP_TOKEN, "Bearer " +
//                        refreshToken.body().getToken());
//                Request.Builder builder = mainRequest.newBuilder().header("Authorization",
//                        sharedPrefManager.getSPToken())
//                        .method(mainRequest.method(), mainRequest.body());
//                mainResponse = chain.proceed(builder.build());
//            }

            //Jika tidak ingin refresh token dan langsung logout
            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN_STATUS, false);
            Intent i = new Intent(Myapp.getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Myapp.getContext().startActivity(i);

        } else if ( mainResponse.code() == 500 ){
            sharedPrefManager.saveSPBoolean(SharedPrefManager.SP_LOGIN_STATUS, false);
            Intent i = new Intent(Myapp.getContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Myapp.getContext().startActivity(i);
        }

        return mainResponse;
    }
}