package com.mrash.instagramclone.utils;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mrash.instagramclone.Model.User;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;

public class H {
    public static Boolean isTrue(Object value) {

        if (value == null) return false;

        if (value instanceof String)
            return !((String) value).trim().isEmpty() && !((String) value).trim().equalsIgnoreCase("null");

        if (value instanceof Number) return !((Number) value).equals(Long.valueOf(0));

        if (value instanceof Boolean) return (Boolean) value;

        if (value instanceof Collection) return !((Collection) value).isEmpty();

        if (value instanceof Object[]) return ((Object[]) value).length > 0;


        return true;
    }

    public static Object convertJsonToObject(JSONObject jsonObject, Class<?> clazz) {
        // Creates the json object which will manage the information received
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        Gson gson = builder.create();
        return gson.fromJson(jsonObject.toString(), clazz);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}
