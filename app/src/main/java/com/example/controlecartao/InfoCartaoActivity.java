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
import android.widget.TextView;

import com.example.controlecartao.adaptadores.InfoCartaoActivityAdapter;
import com.example.controlecartao.dados.ControleContract;
import com.example.controlecartao.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InfoCartaoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOAD_VERSION = 3;

    private Uri uriCartao;

    private FloatingActionButton actionButton;

    private RecyclerView rv;
    private InfoCartaoActivityAdapter adapter;

    private TextView txtValor;
    private TextView txtMes;

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
        this.txtValor = findViewById(R.id.txt_valor_mensal_infocartaoactivity);
        this.txtMes = findViewById(R.id.txt_mes_total_infocartaoactivity);
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
        String[] projection = ControleContract.ComprasEntry.getArrayColunms();
        return new CursorLoader(this,
                ControleContract.ComprasEntry.URI_CONTENT,
                projection,
                null,
                new String[]{String.valueOf(ContentUris.parseId(this.uriCartao))},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        double valor = Utils.CalcularCompras.calcular(data, this);
        if(valor == 0.0f){
            this.txtValor.setText("R$ 0,00");
        } else {
            this.txtValor.setText(Utils.convertValueToCifrao(
                    Utils.convertDoubleToString(this, valor)
            ));
        }
        this.txtMes.setText(Utils.getMesPorExtenso(this));
        this.adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.txtValor.setText(getString(R.string.cifrao));
        this.adapter.setCursor(null);
    }
}