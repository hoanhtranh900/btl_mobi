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
    /*
     * sử dụng HttpLoggingInterceptor để kiểm tra truy vấn mà
     * Retrofit tạo dữa vào Mô tả API Endpoint mà chúng ta đã xem bên trên.
     * Các câu lệnh truy vấn này sẽ được hiển thị trong mục debug của Logcat.
     * OkHttpClient sẽ giúp chúng ta tạo request to server.
     * Còn GsonConverterFactory sẽ chuyển đổi dữ liệu sang dạng object.
     * Với BaseClient class bên dưới chúng ta có thể tạo Retrofit cho các baseUrl khác nhau
     * bằng cách truyền vào trong hàm createService().
     * */

    private static final String url = "http://192.168.0.104:8023/ig-clone/";
//    private static final String url = "http://sangnk.xyz/ig-clone/";

    //    private static ApiInterface REST_CLIENT;
//
//    private static Retrofit retrofit;
//
//    public ApiClient() {
//    }
//
//    public static ApiInterface getInstance() {
//        if (REST_CLIENT == null) {
//            setupApiClient();
//        }
//        return REST_CLIENT;
//    }
//
//    private static void setupApiClient() {
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
////                    .client(client)
////                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        REST_CLIENT = retrofit.create(ApiInterface.class);
//    }
//
//    public static Retrofit getClient() {
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setMaxRequests(1);
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .retryOnConnectionFailure(false)
//                .addInterceptor(interceptor)
//                .dispatcher(dispatcher)
////                .addInterceptor(new TokenAuthenticator())
//                .build();
//
//
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(url)
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//
//        return retrofit;
//
//    }
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