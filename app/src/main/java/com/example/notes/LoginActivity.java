package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notes.network.NotesApis;
import com.example.notes.request.LoginRequest;
import com.example.notes.response.LoginResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity1";
//    private  NotesApis notesApis;
TextInputEditText email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
          email=findViewById(R.id.email1);
          password=findViewById(R.id.password1);

    }
    public  void login(View view){

        if(!email.getText().toString().isEmpty()||!password.getText().toString().isEmpty()) {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email.getText().toString());
            loginRequest.setPassword(password.getText().toString());

            OnlineMethod.getConnect().login(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    if (response.body().getState()) {
                        //Log.i(TAG, "onResponse: "+response.body().getUser().getName());
                        SharedPref.setToken("Bearer " + response.body().getAccessToken());
                        SharedPref.setUser(response.body().getUser());
                        Log.i(TAG, "onResponse: Log in SharedPref.setUser(response.body().getUser()); " + SharedPref.getUser().toString());
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
//                    addNotes();
//                    getNotes();
                    } else {
                        Log.i(TAG, "onResponse: " + response.body().getMessage());
                        Toast.makeText(LoginActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {

                }
            });

        }else
        Toast.makeText(this, "there is empty fields", Toast.LENGTH_SHORT).show(); }


    public void createAccount(View view) {
        startActivity(new Intent(this,RegistryActivity.class));
        finish();
    }
}