package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notes.request.RegisterRequest;
import com.example.notes.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistryActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity1";
    EditText name,email,password,confirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        name=findViewById(R.id.name_regi);
        email=findViewById(R.id.email_regi);
        password=findViewById(R.id.password_regi);
        confirmPassword=findViewById(R.id.confirm_password_regi);

    }




    public void register(View view) {
        String nameM = name.getText().toString();
        String emailM = email.getText().toString();
        String passwordM = password.getText().toString();
        String confirmPasswordM = confirmPassword.getText().toString();
        if (!(nameM.isEmpty() || emailM.isEmpty() || passwordM.isEmpty() || confirmPasswordM.isEmpty()))
        {
            RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(nameM);
        registerRequest.setEmail(emailM);
        registerRequest.setPassword(passwordM);
        registerRequest.setConfirmPassword(confirmPasswordM);

        OnlineMethod.getConnect().register(registerRequest).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.body().isState()) {
                    Log.i(TAG, "onResponse: account created");
                    startActivity(new Intent(RegistryActivity.this,LoginActivity.class));
finish();
                } else {

                    Log.i(TAG, "onResponse: " + response.body().getErrors());
                    Toast.makeText(RegistryActivity.this, ""+response.body().getErrors(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
            }

        });
    }else
            Toast.makeText(this, "there is empty fields", Toast.LENGTH_SHORT).show();
    }

    public void goToLogin(View view) {
        startActivity(new Intent(this,LoginActivity.class));

    }
}