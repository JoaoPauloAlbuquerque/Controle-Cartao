package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.controlecartao.adaptadores.InfoCartaoActivityAdapter;
import com.example.controlecartao.dados.ControleContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InfoCartaoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOAD_VERSION = 3;

    private Uri uriCartao;

    private FloatingActionButton actionButton;

    private RecyclerView rv;
    private InfoCartaoActivityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_cartao);
        Toolbar toolbar = findViewById(R.id.toolbar_infocartaoactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.uriCartao = getIntent().getData();

        this.iniComponentes();

        this.adapter = new InfoCartaoActivityAdapter(null);
        this.rv.setAdapter(this.adapter);

        this.getLoaderManager().initLoader(LOAD_VERSION, null, this);

        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(InfoCartaoActivity.this, EditorActivity.class);
                i.setData(uriCartao);
                startActivity(i);
            }
        });

    }

    private void iniComponentes(){
        this.actionButton = findViewById(R.id.floatigactionbutton_infocartaoactivity);
        this.rv = findViewById(R.id.recyclerview_infocartaoactivity);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ControleContract.ComprasEntry._ID,
                ControleContract.ComprasEntry.COLUNA_DESCRICAO,
                ControleContract.ComprasEntry.COLUNA_DIA,
                ControleContract.ComprasEntry.COLUNA_MES,
                ControleContract.ComprasEntry.COLUNA_ANO,
                ControleContract.ComprasEntry.COLUNA_VALOR,
                ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS,
                ControleContract.ComprasEntry.COLUNA_FK_CARTAO
        };
        return new CursorLoader(this,
                ControleContract.ComprasEntry.URI_CONTENT,
                projection,
                null,
                new String[]{String.valueOf(ContentUris.parseId(this.uriCartao))},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.adapter.setCursor(null);
    }
}