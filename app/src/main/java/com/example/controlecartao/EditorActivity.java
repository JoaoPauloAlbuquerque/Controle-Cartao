package com.example.controlecartao;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.controlecartao.dados.ControleContract;
import com.example.controlecartao.utils.Utils;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int LOAD_VERSION = 3;

    private Uri uri;

    private EditText editOndeComprou;
    private EditText editValorCompra;
    private EditText editParcelas;
    private EditText editDataCompra;
    private TextView txtCifrao;
    private TextView txtXParcelas;

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

        this.editValorCompra.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(this.isUpdating){
                    this.isUpdating = false;
                    return;
                }

                this.isUpdating = true;
                String string = s.toString();
                String currentString = string;
                if(string.contains(",") || string.contains(".")){
                    currentString = string.replace(",", "");
                    currentString = currentString.replace(".", "");
                }

                try {
                    Locale.setDefault(Utils.getLocale(EditorActivity.this));
                    String valor = new DecimalFormat("###,##0.00").format(Double.parseDouble(currentString) / 100);
                    editValorCompra.setText(valor);
                    editValorCompra.setSelection(valor.length());
                } catch (Exception e){
                    Log.e("ERRO", "erro ao converter string para double: " + e);
                    editValorCompra.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.editDataCompra.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String currentData = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                this.currentData = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(this.isUpdating){
                    this.isUpdating = false;
                    return;
                }
                this.isUpdating = true;

                if(this.currentData.length() > s.length()){
                    if(s.length() == 5 || s.length() == 2) return;
                }

                StringBuilder str = new StringBuilder(s.toString().replace("/", ""));
                if(str.toString().length() >= 2){
                    str.insert(2, "/");
                }
                if(str.toString().length() >= 5){
                    str.insert(5, "/");
                }
                editDataCompra.setText(str.toString());
                editDataCompra.setSelection(str.toString().length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.editValorCompra.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txtCifrao.setTextColor(getColorChangeListenerEditText(hasFocus));
            }
        });

        this.editParcelas.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txtXParcelas.setTextColor(getColorChangeListenerEditText(hasFocus));
            }
        });

        this.layoutTextInputData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                layoutTextInputData.getEndIconDrawable().setTint(getColorChangeListenerEditText(hasFocus));
            }
        });
    }

    private int getColorChangeListenerEditText(boolean hasFocus){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return hasFocus ? getColor(R.color.azul) : getColor(R.color.cinza);
        } else {
            return hasFocus ? ContextCompat.getColor(EditorActivity.this, R.color.azul) : ContextCompat.getColor(EditorActivity.this, R.color.cinza);
        }
    }

    private void iniComponentes(){
        this.editOndeComprou = findViewById(R.id.edittext_onde_comprou_editoactivity);
        this.editValorCompra = findViewById(R.id.edittext_valor_compra_editoactivity);
        this.editParcelas = findViewById(R.id.edittext_quantidade_parcelas_editoactivity);
        this.editDataCompra = findViewById(R.id.edittext_data_compra_editoactivity);
        this.layoutTextInputData = findViewById(R.id.layout_edittext_data_compra_editoractivity);
        this.botaoSalvar = findViewById(R.id.botao_salvar_editoractivity);
        this.txtCifrao = findViewById(R.id.txt_cifrao_editoractivity);
        this.txtXParcelas = findViewById(R.id.txt_x_parcelas_editoractivity);
    }

    private void onCreateDataPicker(){
        Locale.setDefault(Utils.getLocale(this));
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
                        editDataCompra.setText(data);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),          // Uma instancia de Calendar para o Ano (AQUI É SELECIONADO O ANO ATUAL DA MAQUINA)
                Calendar.getInstance().get(Calendar.MONTH),         // Uma instancia de Calendar para o Mês (AQUI É SELECIONADO O MES ATUAL DA MAQUINA)
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)   // Uma instancia de Calendar para o Dia (AQUI É SELECIONADO O DIA ATUAL DA MAQUINA)
        );
        dataPicker.show();
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
                Utils.createAlertDialog(this, this.getString(R.string.msg_alertdialog_editoractivity), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCompra();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void inserirCompra(){
        ContentValues values = new ContentValues();

        String[] data = this.editDataCompra.getText().toString().trim().split("/");
        values.put(ControleContract.ComprasEntry.COLUNA_DESCRICAO, this.editOndeComprou.getText().toString().trim());
        values.put(ControleContract.ComprasEntry.COLUNA_DIA, data[0]);
        values.put(ControleContract.ComprasEntry.COLUNA_MES, data[1]);
        values.put(ControleContract.ComprasEntry.COLUNA_ANO, data[2]);
        values.put(ControleContract.ComprasEntry.COLUNA_VALOR, Utils.replaceSimbolosValorToDb(this.editValorCompra.getText().toString().trim()));
        values.put(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS, Integer.parseInt(this.editParcelas.getText().toString().trim()));


        // Se for passado apenas o caminho do cartão, eu vou inserir um novo item
        if(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_CARTAO)){  // Estou pegando apenas o nome da tabela e comparando com o nome da tabela "cartao"
            values.put(ControleContract.ComprasEntry.COLUNA_FK_CARTAO, Integer.parseInt(String.valueOf(ContentUris.parseId(this.uri))));
            Uri uriInsert = getContentResolver().insert(ControleContract.ComprasEntry.URI_CONTENT, values);
            if(ContentUris.parseId(uriInsert) != 0){
                Toast.makeText(this, this.getString(R.string.toast_compra_inserida), Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, this.getString(R.string.toast_erro_inserir_compras), Toast.LENGTH_SHORT).show();
            }
        } else {    // Se não, irei atualizar um item
            int rowsUpdated = getContentResolver().update(this.uri, values, null, null);
            if(rowsUpdated != 0){
                Toast.makeText(this, this.getString(R.string.toast_compra_atualizada), Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, this.getString(R.string.toast_erro_atualizar_compras), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteCompra(){
        if(this.uri.getPath().split("/")[1].equals(ControleContract.PATH_COMPRAS)){
            int rowsDeleted = this.getContentResolver().delete(this.uri, null, null);
            if(rowsDeleted > 0){
                Toast.makeText(this, this.getString(R.string.toast_compra_deletada), Toast.LENGTH_SHORT).show();
                this.finish();
            } else {
                Toast.makeText(this, this.getString(R.string.toast_erro_deletar_compra), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = ControleContract.ComprasEntry.getArrayColunms();
        return new CursorLoader(this, this.uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToNext()){
            this.editOndeComprou.setText(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DESCRICAO)));
            this.editValorCompra.setText(
                    Utils.convertValoresDbToEditText(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_VALOR)))
            );
            this.editParcelas.setText(String.valueOf(data.getInt(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS))));
            StringBuilder dataFormatada = new StringBuilder();
            dataFormatada.append(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DIA)));
            dataFormatada.append("/");
            dataFormatada.append(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES)));
            dataFormatada.append("/");
            dataFormatada.append(data.getString(data.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_ANO)));
            this.editDataCompra.setText(dataFormatada.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.editOndeComprou.setText("");
        this.editValorCompra.setText("");
        this.editParcelas.setText("");
        this.editDataCompra.setText("");
    }
}