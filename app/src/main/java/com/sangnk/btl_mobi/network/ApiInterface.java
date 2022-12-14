package com.sangnk.btl_mobi.network;

import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.network.response.UserResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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
    Call<ResponseBody> getListPost(
            @Query("search") String search,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    ///getComment/{id}
    @GET("api/v1/post/getComment/{id}")
    Call<ResponseBody> getComment(@Path("id") String id);

    //detail post
    @GET("api/v1/post/{id}")
    Call<ResponseBody> getDetailPost(@Path("id") String id);

    @GET("api/v1/system/user/getPage")
    Call<ResponseBody> getListUser(
            @Query("search") String search,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @GET("messages/{senderId}/{recipientId}")
    Call<ResponseBody> getListMessage(
            @Path("senderId") Long senderId,
            @Path("recipientId") Long recipientId
    );

    @GET("api/v1/system/user/getListChatRecent")
    Call<ResponseBody> getListChatRecent();

    @Multipart
    @POST("files/uploadFile")
    Call<ResponseBody> postImage(
            @Part MultipartBody.Part file,
            @Part("objectType") Long objectType
    );
    //update objectType for image
    @PUT("files/upload")
    Call<ResponseBody> updateImage(@Body RequestBody body);

    @POST("api/v1/post/create")
    Call<ResponseBody> postPost(@Body RequestBody body);

    //đăng ký
    @POST("public/v1/sso/register")
    Call<ResponseBody> postRegister(@Body RequestBody body);

    @GET("api/v1/post/getPage")
    Call<ResponseBody> getFollower(@Query("search") String search);

    //like post
    @POST("api/v1/post/like/{id}")
    Call<ResponseBody> postLike(@Path("id") Long id);

    //check like
    @GET("api/v1/post/like/{id}")
    Call<ResponseBody> getLike(@Path("id") Long id);


    @GET("api/v1/system/user/getProfile/{id}")
    Call<ResponseBody> getUser(@Path("id") Long id);

    @POST("api/auth/refresh")
    Call<UserResponse> refreshToken(@Header("Authorization") String token);


    //edit profile
    @PUT("api/v1/system/user")
    Call<ResponseBody> editUser(@Body RequestBody body);

    //addComment
    @POST("api/v1/post/addComment/{id}")
    Call<ResponseBody> postComment(@Path("id") Long id,@Body  String body);

    //check follow
    @GET("api/v1/follow/checkFollow/{id}")
    Call<ResponseBody> checkFollow(@Path("id") Long id);

    //follow or unfollow
    @POST("api/v1/follow/followUser/{id}")
    Call<ResponseBody> followUser(@Path("id") Long id);

    //get list notification
    @GET("api/v1/system/user/getNotify")
    Call<ResponseBody> getNotify(
            @Query("search") String search,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );
}