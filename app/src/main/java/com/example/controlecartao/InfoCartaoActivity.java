package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class InfoCartaoActivity extends AppCompatActivity {

    private Uri uriCartao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_cartao);
        Toolbar toolbar = findViewById(R.id.toolbar_infocartaoactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.uriCartao = getIntent().getData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_infocartaoactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_menu_configuracao_infocartaoactivity:
                this.abrirConfiguracaoCartao();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                throw new IllegalArgumentException("Este menu não foi encontrado");
        }
    }

    private void abrirConfiguracaoCartao(){
        if(this.uriCartao != null){
            Intent i = new Intent(this, AdicionarCartaoActivity.class);
            i.setData(this.uriCartao);
            this.startActivity(i);
        }
    }
}