package com.example.notes;

import android.util.Log;
import android.widget.Toast;

import com.example.notes.network.NotesApis;
import com.example.notes.request.LoginRequest;
import com.example.notes.request.RegisterRequest;
import com.example.notes.response.LoginResponse;
import com.example.notes.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OnlineMethod   {
    private static final String TAG = "MainActivity1";
    private static NotesApis notesApis;
    private static Retrofit retrofit;

    public static NotesApis getConnect(){
        if(notesApis==null){
        retrofit=new Retrofit.Builder()
                .baseUrl("https://notes.amirmohammed.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        notesApis = retrofit.create(NotesApis .class);}
        return notesApis;
    }





}
