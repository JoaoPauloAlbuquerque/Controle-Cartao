package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.controlecartao.dados.ControleContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOAD_VERSION = 3;

    private Uri uri;

    private EditText ondeComprou;
    private EditText valorCompra;
    private EditText parcelas;
    private EditText dataCompra;

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

        if(!this.uri.getPath().split("/")[1].equals(ControleContract.PATH_CARTAO)){
            getLoaderManager().initLoader(LOAD_VERSION, null, this);
        }

        this.botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirCompra();
            }
        });
    }

    private void iniComponentes(){
        this.ondeComprou = findViewById(R.id.edittext_onde_comprou_editoactivity);
        this.valorCompra = findViewById(R.id.edittext_valor_compra_editoactivity);
        this.parcelas = findViewById(R.id.edittext_quantidade_parcelas_editoactivity);
        this.dataCompra = findViewById(R.id.edittext_data_compra_editoactivity);
        this.botaoSalvar = findViewById(R.id.botao_salvar_editoractivity);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private void inserirCompra(){
        ContentValues values = new ContentValues();

        Integer[] data = this.getData();
        values.put(ControleContract.ComprasEntry.COLUNA_DESCRICAO, this.ondeComprou.getText().toString().trim());
        values.put(ControleContract.ComprasEntry.COLUNA_DIA, data[0]);
        values.put(ControleContract.ComprasEntry.COLUNA_MES, data[1]);
        values.put(ControleContract.ComprasEntry.COLUNA_ANO, data[2]);
        values.put(ControleContract.ComprasEntry.COLUNA_VALOR, this.valorCompra.getText().toString().trim());
        values.put(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS, Integer.parseInt(this.parcelas.getText().toString().trim()));
        values.put(ControleContract.ComprasEntry.COLUNA_FK_CARTAO, Integer.parseInt(String.valueOf(ContentUris.parseId(this.uri))));

        // Se for passado apenas o caminho do cartão, eu vou inserir um novo item
        if(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_CARTAO)){  // Estou pegando apenas o nome da tabela e comparando com o nome das tabela "cartao"
            Uri uriInsert = getContentResolver().insert(ControleContract.ComprasEntry.URI_CONTENT, values);
            if(ContentUris.parseId(uriInsert) != 0){
                Toast.makeText(this, "Compra inserida", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao inserir compra", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsUpdated = getContentResolver().update(this.uri, values, null, null);
            if(rowsUpdated != 0){
                Toast.makeText(this, "Compra atualizada", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao atualizar compra", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Integer[] getData(){
        String[] data = this.dataCompra.getText().toString().split("/");
        return new Integer[]{
                Integer.parseInt(data[0]),
                Integer.parseInt(data[1]),
                Integer.parseInt(data[2])
        };
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
        data.moveToNext();
        this.ondeComprou.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DESCRICAO)));
        this.valorCompra.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_VALOR)));
        this.parcelas.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS))));
        StringBuilder dataFormatada = new StringBuilder();
        dataFormatada.append(data.getInt(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DIA)));
        dataFormatada.append("/");
        dataFormatada.append(data.getInt(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES)));
        dataFormatada.append("/");
        dataFormatada.append(data.getInt(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_ANO)));
        this.dataCompra.setText(dataFormatada.toString());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.ondeComprou.setText("");
        this.valorCompra.setText("");
        this.parcelas.setText("");
        this.dataCompra.setText("");
    }
}