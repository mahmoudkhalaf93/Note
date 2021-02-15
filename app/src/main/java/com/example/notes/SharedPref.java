package com.example.notes;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.notes.response.User;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {
    private  final static String tokenSP = "token", userSP = "user" ,firstTimeRunSP="firstTimeRun";

    private static SharedPreferences instance;

    public static SharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = context.getSharedPreferences("userData", MODE_PRIVATE);
        }
        return instance;
    }

    public static void setUser(User user) {
        instance.edit().putString(userSP, new Gson().toJson(user)).apply();
    }

    public static User getUser() {
        return new Gson().fromJson(instance.getString(userSP, null), User.class);
    }



    public static void setFirstTimeRun() {
        instance.edit().putString(firstTimeRunSP, "no").apply();
    }

    public static String getFirstTimeRun() {
        return instance.getString(firstTimeRunSP, "");
    }



    public static void setToken(String token) {
        instance.edit().putString(tokenSP, token).apply();
    }

    public static String getToken() {
        return instance.getString(tokenSP, "");
    }


}
