package com.soterocra.crud.activities;

import android.content.Intent;
import android.os.Bundle;

import com.soterocra.crud.R;
import com.soterocra.crud.activities.enums.CadastraAlteraEnum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.soterocra.crud.activities.MainActivity";
    public static final String CADASTRA_ALTERA = "com.soterocra.crud.activities.CADASTRA_ALTERA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Intent intent;

        switch (item.getItemId()) {
            case R.id.action_alteracao_usuario:
                intent = new Intent(this, CadastraAlteraUsuario.class);
                intent.putExtra(CADASTRA_ALTERA, CadastraAlteraEnum.ALTERACAO);
                startActivity(intent);
                return true;
            case R.id.action_cadastro_de_usuario:
                intent = new Intent(this, CadastraAlteraUsuario.class);
                intent.putExtra(CADASTRA_ALTERA, CadastraAlteraEnum.CADASTRO);
                startActivity(intent);
                return true;
            case R.id.action_excluir_usuario:
                startActivity(new Intent(this, ExcluirUsuarioActivity.class));
                return true;
            case R.id.action_lista_usuarios:
                startActivity(new Intent(this, ListaUsuariosActivity.class));
                return true;
            case R.id.action_login:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
