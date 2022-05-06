package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.controlecartao.dados.ControleContract;

public class EditorActivity extends AppCompatActivity {

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
        
        if(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_CARTAO)){
            Uri uriInsert = getContentResolver().insert(ControleContract.ComprasEntry.URI_CONTENT, values);
            if(ContentUris.parseId(uriInsert) != 0){
                Toast.makeText(this, "Compra inserida", Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, "Erro ao inserir compra", Toast.LENGTH_SHORT).show();
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
}