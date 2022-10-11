package com.mrash.instagramclone.network;

import android.content.Context;

import com.mrash.instagramclone.network.interceptor.TokenAuthenticator;

import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {


//    private static final String url = "http://192.168.0.101:8023/ig-clone/";
//    private static final String url = "http://10.20.32.202:8023/ig-clone/";
//    private static final String url = "http://192.168.0.104:8023/ig-clone/";
    private static final String url = "http://sangnk.xyz/ig-clone/";

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient= null;
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                //.sslSocketFactory(sslSocketFactory, trustManager)
                .followRedirects(false)
                .addInterceptor(new TokenAuthenticator())
                .followSslRedirects(false)
                .retryOnConnectionFailure(false)
                .cache(null)//new Cache(sContext.getCacheDir(),10*1024*1024)
                .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}