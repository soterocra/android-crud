package com.soterocra.crud.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.soterocra.crud.R;
import com.soterocra.crud.activities.enums.CadastraAlteraEnum;
import com.soterocra.crud.dto.DtoUser;
import com.soterocra.crud.services.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastraAlteraUsuario extends AppCompatActivity {

    private static final String TAG = "com.soterocra.crud.activities.CadastraAlteraUsuario";
    public static final String CADASTRA_ALTERA = "com.soterocra.crud.activities.CADASTRA_ALTERA";

    private EditText editTextNome;
    private EditText editTextEmail;
    private EditText editTextSenha;
    private EditText editTextTelefone;

    private CadastraAlteraEnum cadastraAlteraEnum;

    private String token;
    DtoUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altera_cadastra_usuario);

        setup();

        SharedPreferences sp = getSharedPreferences("dados", MODE_PRIVATE);

        cadastraAlteraEnum = (CadastraAlteraEnum) getIntent().getSerializableExtra(CADASTRA_ALTERA);

        Button btnAlteraCadastra = (Button) findViewById(R.id.btn_altera_cadastra);

        if (cadastraAlteraEnum == CadastraAlteraEnum.ALTERACAO_USUARIO_ATUAL || cadastraAlteraEnum == CadastraAlteraEnum.ALTERACAO) {

            token = sp.getString("token", null);

            if (token == null) {
                Toast.makeText(this, "Primeiro faça o login para tentar alterar o seu ou outro usuário.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, MainActivity.class));
            }

            setTitle("Alteração de Dados");

            btnAlteraCadastra.setText("Alterar Dados");
            btnAlteraCadastra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alterar(view);
                }
            });

            if (cadastraAlteraEnum == CadastraAlteraEnum.ALTERACAO_USUARIO_ATUAL) {
                user = new Gson().fromJson(sp.getString("loggedUser", null), DtoUser.class);
            } else {
                user = (DtoUser) getIntent().getSerializableExtra("user");
            }

            editTextNome.setText(user.getName());
            editTextEmail.setText(user.getEmail());
            editTextSenha.setText(user.getPassword());
            editTextTelefone.setText(user.getPhone());

        } else if (cadastraAlteraEnum == CadastraAlteraEnum.CADASTRO) {

            setTitle("Cadastro de Usuário");

            btnAlteraCadastra.setText("Cadastrar");
            btnAlteraCadastra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cadastrar(view);
                }
            });
        }

    }

    private void setup() {
        editTextNome = ((EditText) findViewById(R.id.et_cadastro_usuario_nome));
        editTextEmail = ((EditText) findViewById(R.id.et_cadastro_usuario_email));
        editTextSenha = ((EditText) findViewById(R.id.et_cadastro_usuario_password));
        editTextTelefone = ((EditText) findViewById(R.id.et_cadastro_usuario_phone));
    }

    private void cadastrar(View view) {
        String nome = editTextNome.getText().toString();
        String email = editTextEmail.getText().toString();
        String senha = editTextSenha.getText().toString();
        String telefone = editTextTelefone.getText().toString();

        DtoUser dtoUser = new DtoUser(email, nome, senha, telefone);

        RetrofitService.getServico(this).cadastrarUsuario(dtoUser).enqueue(new Callback<DtoUser>() {
            @Override
            public void onResponse(Call<DtoUser> call, Response<DtoUser> response) {
                Toast.makeText(CadastraAlteraUsuario.this, "Usuário cadastrado com ID: " + response.body().getId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DtoUser> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void alterar(View view) {
        String nome = editTextNome.getText().toString();
        String email = editTextEmail.getText().toString();
        String senha = editTextSenha.getText().toString();
        String telefone = editTextTelefone.getText().toString();

        DtoUser dtoUser = new DtoUser(email, nome, senha, telefone);

        RetrofitService.getServico(this).atualizaUsuario(user.getId(), "Bearer " + token, dtoUser).enqueue(new Callback<DtoUser>() {
            @Override
            public void onResponse(Call<DtoUser> call, Response<DtoUser> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CadastraAlteraUsuario.this, "Usuário " + response.body().getName() + " alterado.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CadastraAlteraUsuario.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DtoUser> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }
}
