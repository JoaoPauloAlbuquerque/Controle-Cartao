package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.controlecartao.dados.ControleContract;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOAD_VERSION = 3;

    private Uri uri;

    private EditText ondeComprou;
    private EditText valorCompra;
    private EditText parcelas;
    private EditText dataCompra;

    private TextInputLayout layoutTextInputData;

    private AppCompatButton botaoSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = findViewById(R.id.toolbar_editoractivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.uri = getIntent().getData();

        this.iniComponentes();

        // Se o path do uri não for o nome da tabela cartao, quer dizer que eu passei o id de uma compra, ou seja, vou edita-la.
        // Caso contrário, irei inserir uma nova compra, então tenho que esconder o botão de deletar.
        if(!(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_CARTAO))){
            this.setTitle(R.string.titulo_editoractivity_edit_compra);
            getLoaderManager().initLoader(LOAD_VERSION, null, this);
        } else {
            this.setTitle(R.string.titulo_editoractivity_add_compra);
            invalidateOptionsMenu();
        }

        this.botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirCompra();
            }
        });

        this.layoutTextInputData.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDataPicker();
            }
        });
    }

    private void onCreateDataPicker(){
        DatePickerDialog dataPicker = new DatePickerDialog(
                EditorActivity.this,                        // Contexto
                new DatePickerDialog.OnDateSetListener() {         // OnDateSetListener
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String dia = String.valueOf(dayOfMonth);
                        String mes = String.valueOf(month + 1);
                        String ano = String.valueOf(year);
                        if(dia.length() == 1){
                            dia = "0" + dia;
                        }
                        if(mes.length() == 1){
                            mes = "0" + mes;
                        }
                        String data = dia + "/" + mes + "/" + ano;
                        dataCompra.setText(data);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),          // Uma instancia de Calendar para o Ano (AQUI É SELECIONADO O ANO ATUAL DA MAQUINA)
                Calendar.getInstance().get(Calendar.MONTH),         // Uma instancia de Calendar para o Mês (AQUI É SELECIONADO O MES ATUAL DA MAQUINA)
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)   // Uma instancia de Calendar para o Dia (AQUI É SELECIONADO O DIA ATUAL DA MAQUINA)
        );
        dataPicker.setMessage("Selecione a data");

        dataPicker.show();
    }

    private void iniComponentes(){
        this.ondeComprou = findViewById(R.id.edittext_onde_comprou_editoactivity);
        this.valorCompra = findViewById(R.id.edittext_valor_compra_editoactivity);
        this.parcelas = findViewById(R.id.edittext_quantidade_parcelas_editoactivity);
        this.dataCompra = findViewById(R.id.edittext_data_compra_editoactivity);
        this.layoutTextInputData = findViewById(R.id.layout_edittext_data_compra_editoractivity);
        this.botaoSalvar = findViewById(R.id.botao_salvar_editoractivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_CARTAO)) {
            menu.findItem(R.id.item_menu_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.item_menu_delete:
                this.deleteCompra();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inserirCompra(){
        ContentValues values = new ContentValues();

        String[] data = this.getData();
        values.put(ControleContract.ComprasEntry.COLUNA_DESCRICAO, this.ondeComprou.getText().toString().trim());
        values.put(ControleContract.ComprasEntry.COLUNA_DIA, data[0]);
        values.put(ControleContract.ComprasEntry.COLUNA_MES, data[1]);
        values.put(ControleContract.ComprasEntry.COLUNA_ANO, data[2]);
        values.put(ControleContract.ComprasEntry.COLUNA_VALOR, this.valorCompra.getText().toString().trim());
        values.put(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS, Integer.parseInt(this.parcelas.getText().toString().trim()));


        // Se for passado apenas o caminho do cartão, eu vou inserir um novo item
        if(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_CARTAO)){  // Estou pegando apenas o nome da tabela e comparando com o nome das tabela "cartao"
            values.put(ControleContract.ComprasEntry.COLUNA_FK_CARTAO, Integer.parseInt(String.valueOf(ContentUris.parseId(this.uri))));
            Uri uriInsert = getContentResolver().insert(ControleContract.ComprasEntry.URI_CONTENT, values);
            if(ContentUris.parseId(uriInsert) != 0){
                Toast.makeText(this, "Compra inserida", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao inserir compra", Toast.LENGTH_SHORT).show();
            }
        } else {    // Se não, irei atualizar um item
            int rowsUpdated = getContentResolver().update(this.uri, values, null, null);
            if(rowsUpdated != 0){
                Toast.makeText(this, "Compra atualizada", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao atualizar compra", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteCompra(){
        if(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_COMPRAS)){
            int rowsDeleted = this.getContentResolver().delete(this.uri, null, null);
            if(rowsDeleted > 0){
                Toast.makeText(this, "Compra deletada", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao deletar compra", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String[] getData(){
        String[] data = this.dataCompra.getText().toString().trim().split("/");
        Log.e("dia", this.dataCompra.getText().toString().trim().split("/")[0]);
        Log.e("mes", this.dataCompra.getText().toString().trim().split("/")[1]);
        Log.e("ano", this.dataCompra.getText().toString().split("/")[2]);
        return data;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ControleContract.ComprasEntry._ID,
                ControleContract.ComprasEntry.COLUNA_DESCRICAO,
                ControleContract.ComprasEntry.COLUNA_VALOR,
                ControleContract.ComprasEntry.COLUNA_DIA,
                ControleContract.ComprasEntry.COLUNA_MES,
                ControleContract.ComprasEntry.COLUNA_ANO,
                ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS,
                ControleContract.ComprasEntry.COLUNA_FK_CARTAO
        };
        return new CursorLoader(this, this.uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToNext()){
            this.ondeComprou.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DESCRICAO)));
            this.valorCompra.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_VALOR)));
            this.parcelas.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS))));
            StringBuilder dataFormatada = new StringBuilder();
            dataFormatada.append(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DIA)));
            dataFormatada.append("/");
            dataFormatada.append(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES)));
            dataFormatada.append("/");
            dataFormatada.append(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_ANO)));
            this.dataCompra.setText(dataFormatada.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.ondeComprou.setText("");
        this.valorCompra.setText("");
        this.parcelas.setText("");
        this.dataCompra.setText("");
    }
}