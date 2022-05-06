package com.example.controlecartao.dados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ControleDbHelp extends SQLiteOpenHelper {

    private static final int VERSAO_BD = 4;
    private static final String NOME_BD = "controle.db";

    public ControleDbHelp(@Nullable Context context) {
        super(context, NOME_BD, null, VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE_CARTAO = "CREATE TABLE " + ControleContract.CartaoEntry.NOME_TABELA + " (" +
                ControleContract.CartaoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ControleContract.CartaoEntry.COLUNA_NOME_CARTAO + " TEXT NOT NULL," +
                ControleContract.CartaoEntry.COLUNA_DIA_PAGAMENTO + " INTEGER NOT NULL," +
                ControleContract.CartaoEntry.COLUNA_DIA_FECHAMENTO + " INTEGER NOT NULL," +
                ControleContract.CartaoEntry.COLUNA_NUMERO_FINAL_CARTAO + " INTEGER NOT NULL UNIQUE);";
        db.execSQL(SQL_CREATE_TABLE_CARTAO);

        String SQL_CREATE_TABLE_COMPRAS = "CREATE TABLE " + ControleContract.ComprasEntry.NOME_TABELA + " (" +
                ControleContract.ComprasEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ControleContract.ComprasEntry.COLUNA_DESCRICAO + " TEXT NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_DIA + " INTEGER NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_MES + " INTEGER NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_ANO + " INTEGER NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_VALOR + " TEXT NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS + " INTEGER NOT NULL," +
                ControleContract.ComprasEntry.COLUNA_FK_CARTAO + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + ControleContract.ComprasEntry.COLUNA_FK_CARTAO + ")" +
                "REFERENCES " + ControleContract.CartaoEntry.NOME_TABELA + " (" + ControleContract.CartaoEntry._ID + "));";
        db.execSQL(SQL_CREATE_TABLE_COMPRAS);
    }

    /**
     * Este método é chamado sempre que o banco de dados for aberto
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        String SQL_ATIVAR_FOREIGN_KEY = "PRAGMA FOREIGN_KEYS=ON";
        /**
         * Se o banco de dados for aberto como leitura, o isReadOnly() returna verdadeiro.
         * Como eu só quero executar quando não for aberto como leitura, e sim como escrita,
         * eu nego o método
         */
        if(!db.isReadOnly()){
            db.execSQL(SQL_ATIVAR_FOREIGN_KEY);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_TABELA_CARTAO = "DROP TABLE IF EXISTS " + ControleContract.CartaoEntry.NOME_TABELA + ";";
        String SQL_DELETE_TABELA_COMPRAS = "DROP TABLE IF EXISTS " + ControleContract.ComprasEntry.NOME_TABELA + ";";
        db.execSQL(SQL_DELETE_TABELA_CARTAO);
        db.execSQL(SQL_DELETE_TABELA_COMPRAS);
        onCreate(db);
    }
}
