package com.sangnk.btl_mobi.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String SP_LOGIN_APP = "Outstagram";

    public static final String SP_TOKEN = "spToken";

    public static final String SP_LOGIN_STATUS = "loginStatus";

    public static final String SP_USER_ID = "spUserId";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;


    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_LOGIN_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public void saveSPLong(String keySP, long value){
        spEditor.putLong(keySP, value);
        spEditor.commit();
    }


    public String getSPToken(){
        return sp.getString(SP_TOKEN, "");
    }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_LOGIN_STATUS, false);
    }

    public Long getSPUserId(){
        return sp.getLong(SP_USER_ID, 0);
    }


    public void clear() {
        spEditor.clear();
        spEditor.commit();
    }
}