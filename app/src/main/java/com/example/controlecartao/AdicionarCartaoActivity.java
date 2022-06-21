package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.controlecartao.utils.Utils;

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
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(this.uriCartao == null){
            MenuItem item = menu.findItem(R.id.item_menu_delete);
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
            case R.id.item_menu_delete:
                Utils.createAlertDialog(this, "Ao excluir o cartão, você perderá todas as suas informações de compras permanentemente!\n\nDeseja continuar?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog != null){
                            dialog.dismiss();
                        }
                        Utils.createAlertDialog(AdicionarCartaoActivity.this, "Deseja realmente excluir o cartão?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletarCartao();
                            }
                        });
                    }
                });
                break;
            default:
                return false;
        }
        return true;
    }

    private void inserirCartao(){
        ContentValues values = new ContentValues();
        String nome = this.nomeCartao.getText().toString().trim();
        String diaP = this.diaPagamento.getText().toString().trim();
        String diaF = this.diaFechamento.getText().toString().trim();
        String numero = this.numeroCartao.getText().toString().trim();
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
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(i);
            } else {
                Toast.makeText(this, "Erro ao salvar cartão", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void deletarCartao(){
        int rowsDeleted = getContentResolver().delete(this.uriCartao, null, null);
        if(rowsDeleted != 0){
            Toast.makeText(this, "Cartao deletado", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            // Com esta flag, quando a MainActivity for iniciada, todas as outras acticitys que
            // estiverem na pilha de execução do app seram finalizadas
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(i);
            this.finish();
        } else {
            Toast.makeText(this, "Erro ao deletar cartao", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(this.uriCartao != null) {
            String[] projection = ControleContract.CartaoEntry.getArrayColunms();
            return new CursorLoader(this, this.uriCartao, projection, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToNext()){
            this.nomeCartao.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_NOME_CARTAO)));
            this.diaPagamento.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_DIA_PAGAMENTO)));
            this.diaFechamento.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_DIA_FECHAMENTO)));
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