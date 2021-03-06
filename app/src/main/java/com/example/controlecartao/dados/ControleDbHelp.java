package com.example.controlecartao.dados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ControleDbHelp extends SQLiteOpenHelper {

    private static final int VERSAO_BD = 6;
    private static final String NOME_BD = "controle.db";

    public ControleDbHelp(@Nullable Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE_CARTAO = "CREATE TABLE " + ControleContract.CartaoEntry.NOME_TABELA + " (" +
                ControleContract.CartaoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ControleContract.CartaoEntry.COLUNA_NOME_CARTAO + " TEXT NOT NULL," +
                ControleContract.CartaoEntry.COLUNA_DIA_PAGAMENTO + " VARCHAR(2) NOT NULL," +
                ControleContract.CartaoEntry.COLUNA_DIA_FECHAMENTO + " VARCHAR(2) NOT NULL," +
                ControleContract.CartaoEntry.COLUNA_NUMERO_FINAL_CARTAO + " INTEGER NOT NULL UNIQUE);";
        db.execSQL(SQL_CREATE_TABLE_CARTAO);

        String SQL_CREATE_TABLE_COMPRAS = "CREATE TABLE " + ControleContract.ComprasEntry.NOME_TABELA + " (" +
                ControleContract.ComprasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ControleContract.ComprasEntry.COLUNA_DESCRICAO + " TEXT NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_DIA + " VARCHAR(2) NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_MES + " CARCHAR(2) NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_ANO + " VARCHAR(4) NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_VALOR + " TEXT NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS + " INTEGER NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_FK_CARTAO + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + ControleContract.ComprasEntry.COLUNA_FK_CARTAO + ")" +
                "REFERENCES " + ControleContract.CartaoEntry.NOME_TABELA + " (" + ControleContract.CartaoEntry._ID + ") ON DELETE CASCADE);";
        db.execSQL(SQL_CREATE_TABLE_COMPRAS);

        String SQL_CREATE_TABLE_PARCELAS = "CREATE TABLE " + ControleContract.ParcelasEntry.NOME_TABELA + " (" +
                ControleContract.ParcelasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ControleContract.ParcelasEntry.COLUNA_FK_COMPRA + " INTEGER NOT NULL," +
                ControleContract.ParcelasEntry.COLUNA_VALOR_PARCELA + " TEXT NOT NULL," +
                ControleContract.ParcelasEntry.COLUNA_MES + " VARCHAR(2) NOT NULL," +
                ControleContract.ParcelasEntry.COLUNA_ANO + " VARCHAR(4) NOT NULL," +
                ControleContract.ParcelasEntry.COLUNA_ESTATUS + " TEXT NOT NULL DEFAULT 'dev'," +
                "FOREIGN KEY (" + ControleContract.ParcelasEntry.COLUNA_FK_COMPRA + ") " +
                "REFERENCES " + ControleContract.ComprasEntry.NOME_TABELA + " (" + ControleContract.ComprasEntry._ID + ") ON DELETE CASCADE);";
        db.execSQL(SQL_CREATE_TABLE_PARCELAS);
    }

    /**
     * Este m??todo ?? chamado sempre que o banco de dados for aberto
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String SQL_ATIVAR_FOREIGN_KEY = "PRAGMA FOREIGN_KEYS=ON";
        /**
         * Se o banco de dados for aberto como leitura, o isReadOnly() returna verdadeiro.
         * Como eu s?? quero executar quando n??o for aberto como leitura, e sim como escrita,
         * eu nego o m??todo
         */
        if(!db.isReadOnly()){
            db.execSQL(SQL_ATIVAR_FOREIGN_KEY);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_TABELA_CARTAO = "DROP TABLE IF EXISTS " + ControleContract.CartaoEntry.NOME_TABELA + ";";
        String SQL_DELETE_TABELA_COMPRAS = "DROP TABLE IF EXISTS " + ControleContract.ComprasEntry.NOME_TABELA + ";";
        String SQL_DELETE_TABELA_PARCELAS = "DROP TABLE IF EXISTS " + ControleContract.ParcelasEntry.NOME_TABELA + ";";
        db.execSQL(SQL_DELETE_TABELA_CARTAO);
        db.execSQL(SQL_DELETE_TABELA_COMPRAS);
        db.execSQL(SQL_DELETE_TABELA_PARCELAS);
        onCreate(db);
    }
}
