package com.mrash.instagramclone.network;

import com.mrash.instagramclone.Model.User;
import com.mrash.instagramclone.network.response.UserResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("public/v1/sso/jwt-login")
    Call<ResponseBody> postLogin(@Body RequestBody body);

    @GET("api/v1/post/getPage")
    Call<ResponseBody> getPost(@Query("search") String search);
    //api lấy những người mình đang theo dõi (id: current user)
    @GET("api/v1/post/getPage")
    Call<ResponseBody> getFollowing(
            @Query("search") String search,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    //đăng ký
    @POST("public/v1/sso/register")
    Call<ResponseBody> postRegister(@Body RequestBody body);

    @GET("api/v1/post/getPage")
    Call<ResponseBody> getFollower(@Query("search") String search);


    @GET("api/auth/me")
    Call<User> getUser(@Header("Authorization") String token);

    @POST("api/auth/refresh")
    Call<UserResponse> refreshToken(@Header("Authorization") String token);

}