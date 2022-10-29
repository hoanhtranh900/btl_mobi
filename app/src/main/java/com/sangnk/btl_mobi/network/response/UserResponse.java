package com.sangnk.btl_mobi.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sangnk.btl_mobi.Model.dto.AccessTokenInfo;

public class UserResponse extends BaseResponse {

    @Expose
    @SerializedName("username") String username;
    @Expose
    @SerializedName("email") String email;

    //accessTokenInfo class AccessTokenInfo
    @Expose
    @SerializedName("accessTokenInfo")
    AccessTokenInfo accessTokenInfo;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccessTokenInfo getAccessTokenInfo() {
        return accessTokenInfo;
    }

    public void setAccessTokenInfo(AccessTokenInfo accessTokenInfo) {
        this.accessTokenInfo = accessTokenInfo;
    }
}
