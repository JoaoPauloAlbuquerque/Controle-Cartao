package com.example.controlecartao;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.controlecartao.adaptadores.MainActivityAdapter;
import com.example.controlecartao.dados.ControleContract;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int LOAD_VERSION = 1;

    private AppCompatButton botaoAdicionarCartao;
    private Toolbar toolbar;

    private RecyclerView rv;
    private MainActivityAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.iniComponentes();
        setSupportActionBar(this.toolbar);

        mAdapter = new MainActivityAdapter(null);
        rv.setAdapter(mAdapter);

        getLoaderManager().initLoader(LOAD_VERSION, null, this);

        botaoAdicionarCartao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AdicionarCartaoActivity.class);
                startActivity(i);
            }
        });
    }

    private void iniComponentes(){
        this.botaoAdicionarCartao = findViewById(R.id.botao_add_cartao_mainactivity);
        this.toolbar = findViewById(R.id.toolbar_mainactivity);
        this.rv = findViewById(R.id.recyclerview_mainactivity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ControleContract.CartaoEntry._ID,
                ControleContract.CartaoEntry.COLUNA_NOME_CARTAO,
                ControleContract.CartaoEntry.COLUNA_DIA_PAGAMENTO,
                ControleContract.CartaoEntry.COLUNA_DIA_FECHAMENTO,
                ControleContract.CartaoEntry.COLUNA_NUMERO_FINAL_CARTAO
        };
        return new CursorLoader(this, ControleContract.CartaoEntry.URI_CONTENT, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.mAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.mAdapter.setCursor(null);
    }
}