package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.controlecartao.dados.ControleContract;

public class AdicionarCartaoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOAD_VERSION = 2;

    private EditText nomeCartao;
    private EditText diaPagamento;
    private EditText diaFechamento;
    private EditText numeroCartao;
    private AppCompatButton botaoSalvar;

    private Uri uriCartao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_cartao);
        Toolbar toolbar = findViewById(R.id.toolbar_adicionarcartaoactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Defini o botão de voltar da activity

        this.iniComponentes();


        this.uriCartao = getIntent().getData();

        if(uriCartao == null){
            getSupportActionBar().setTitle(R.string.titulo_adicionarcartaoactivity);
            invalidateOptionsMenu();
        } else {
            getSupportActionBar().setTitle(R.string.titulo_editarcartaoactivity);
        }

        getLoaderManager().initLoader(this.LOAD_VERSION, null, this);

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirCartao();
            }
        });

    }

    private void iniComponentes(){
        this.nomeCartao = findViewById(R.id.edittext_nome_cartao_adicionarcartaoactivity);
        this.diaPagamento = findViewById(R.id.edittext__dia_pagamento_adicionarcartaoactivity);
        this.diaFechamento = findViewById(R.id.edittext__dia_fechamento_adicionarcartaoactivity);
        this.numeroCartao = findViewById(R.id.edittext_ultimos_numeros_cartao_adicionarcartaoactivity);
        this.botaoSalvar = findViewById(R.id.botao_salvar_adicionarcartaoactivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editarcartaoactivity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(this.uriCartao == null){
            MenuItem item = menu.findItem(R.id.item_menu_delete_editcartaoactivity);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: // Este é o ID do botão de voltar da activity
                this.finish();
                break;
            case R.id.item_menu_delete_editcartaoactivity:
                this.deletarCartao();
                break;
            default:
                return false;
        }
        return true;
    }

    private void inserirCartao(){
        ContentValues values = new ContentValues();
        String nome = this.nomeCartao.getText().toString().trim();
        int diaP = Integer.parseInt(this.diaPagamento.getText().toString().trim());
        int diaF = Integer.parseInt(this.diaFechamento.getText().toString().trim());
        int numero = Integer.parseInt(this.numeroCartao.getText().toString().trim());
        values.put(ControleContract.CartaoEntry.COLUNA_NOME_CARTAO, nome);
        values.put(ControleContract.CartaoEntry.COLUNA_DIA_PAGAMENTO, diaP);
        values.put(ControleContract.CartaoEntry.COLUNA_DIA_FECHAMENTO, diaF);
        values.put(ControleContract.CartaoEntry.COLUNA_NUMERO_FINAL_CARTAO, numero);
        if(this.uriCartao == null) {
            Uri uri = getContentResolver().insert(ControleContract.CartaoEntry.URI_CONTENT, values);
            if(uri != null){
                Toast.makeText(this, "Cartão inserido", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao inserir cartão", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAfected = getContentResolver().update(this.uriCartao, values, null, null);
            if(rowsAfected > 0){
                Toast.makeText(this, "Cartao salvo", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao salvar cartão", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void deletarCartao(){
        //TODO:
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(this.uriCartao != null) {
            String[] projection = {
                    ControleContract.CartaoEntry._ID,
                    ControleContract.CartaoEntry.COLUNA_NOME_CARTAO,
                    ControleContract.CartaoEntry.COLUNA_DIA_PAGAMENTO,
                    ControleContract.CartaoEntry.COLUNA_DIA_FECHAMENTO,
                    ControleContract.CartaoEntry.COLUNA_NUMERO_FINAL_CARTAO
            };
            return new CursorLoader(this, this.uriCartao, projection, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToNext()){
            this.nomeCartao.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_NOME_CARTAO)));
            this.diaPagamento.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_DIA_PAGAMENTO))));
            this.diaFechamento.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_DIA_FECHAMENTO))));
            this.numeroCartao.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_NUMERO_FINAL_CARTAO))));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.nomeCartao.setText("");
        this.diaPagamento.setText("");
        this.diaFechamento.setText("");
        this.numeroCartao.setText("");
    }
}