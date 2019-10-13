package com.soterocra.crud.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.soterocra.crud.R;
import com.soterocra.crud.dto.DtoLogin;
import com.soterocra.crud.services.RetrofitService;
import com.soterocra.crud.utils.SharedPreferencesUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void logar(View view) {
        String email = ((EditText) findViewById(R.id.et_login_email)).getText().toString();
        String senha = ((EditText) findViewById(R.id.et_login_password)).getText().toString();

        DtoLogin dtoLogin = new DtoLogin();
        dtoLogin.setEmail(email);
        dtoLogin.setPassword(senha);

        RetrofitService.getServico(this).login(dtoLogin).enqueue(new Callback<DtoLogin>() {
            @Override
            public void onResponse(Call<DtoLogin> call, Response<DtoLogin> response) {
                Toast.makeText(LoginActivity.this, "Usuário logado", Toast.LENGTH_SHORT).show();

                SharedPreferences sp = SharedPreferencesUtils.get(getSharedPreferences("dados", 0));

                SharedPreferences.Editor editor = sp.edit();
                Map<String, String> data = new HashMap<>();

                token = response.body().getToken();

                data.put("token", token);
                data.put("loggedUser", new Gson().toJson(response.body()));
                SharedPreferencesUtils.write(sp, editor, data);

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(Call<DtoLogin> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
