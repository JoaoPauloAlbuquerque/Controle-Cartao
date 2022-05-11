package com.example.controlecartao.dados;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;

public class ControleProvider extends ContentProvider {

    public static final int CARTAO = 1;
    public static final int CARTAO_ID = 2;

    public static final int COMPRAS = 3;
    public static final int COMPRAS_ID = 4;

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(ControleContract.AUTORIZADOR, ControleContract.PATH_CARTAO, CARTAO);
        URI_MATCHER.addURI(ControleContract.AUTORIZADOR, ControleContract.PATH_CARTAO + "/#", CARTAO_ID);

        URI_MATCHER.addURI(ControleContract.AUTORIZADOR, ControleContract.PATH_COMPRAS, COMPRAS);
        URI_MATCHER.addURI(ControleContract.AUTORIZADOR, ControleContract.PATH_COMPRAS + "/#", COMPRAS_ID);
    }

    private ControleDbHelp dbHelp;

    @Override
    public boolean onCreate() {
        dbHelp = new ControleDbHelp(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelp.getReadableDatabase();
        Cursor cursor;

        int match = URI_MATCHER.match(uri);
        switch(match){
            // Se eu passar apenas o nome da tabela cartao, seleciono todos os cartões
            case CARTAO:
                cursor = db.query(ControleContract.CartaoEntry.NOME_TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            // Se eu passar o nome da tabela cartao e um id, eu seleciono apenas 1 cartao
            case CARTAO_ID:
                selection = ControleContract.CartaoEntry._ID + " = ?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = db.query(ControleContract.CartaoEntry.NOME_TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            // Se eu passar apenas o nome da tabela, então eu vou passar o id do cartão no selectionArgs, assim, eu seleciono todos as compras do cartão
            case COMPRAS:
                selection = ControleContract.ComprasEntry.COLUNA_FK_CARTAO + " = ?";
                cursor = db.query(ControleContract.ComprasEntry.NOME_TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            // Se eu passar o id ca compra, seleciono apenas ela
            case COMPRAS_ID:
                selection = ControleContract.ComprasEntry._ID + " = ?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = db.query(ControleContract.ComprasEntry.NOME_TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Erro ao fazer consulta no banco de dados;");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        int match = URI_MATCHER.match(uri);
        switch(match){
            case CARTAO:
                long idCartao = db.insert(ControleContract.CartaoEntry.NOME_TABELA, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, idCartao);
            case COMPRAS:
//                long idCompra = db.insert(ControleContract.ComprasEntry.NOME_TABELA, null, values);
//                getContext().getContentResolver().notifyChange(uri, null);
//                return ContentUris.withAppendedId(uri, idCompra);
                long idCompra = db.insert(ControleContract.ComprasEntry.NOME_TABELA, null, values);
                this.inserirParcelas(db, values, idCompra);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, idCompra);
            default:
                return null;
        }
    }

    private void inserirParcelas(SQLiteDatabase db, ContentValues values, long idCompra){

        int parcelas = values.getAsInteger(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS);
        double valorCompra = Double.parseDouble(values.getAsString(ControleContract.ComprasEntry.COLUNA_VALOR));
        double valorParcela = valorCompra / parcelas;
        int mes = values.getAsInteger(ControleContract.ComprasEntry.COLUNA_MES);

        if(parcelas > 1){
            for(int i = 0; i < parcelas; i++){
                mes++;
                ContentValues valores = this.setValores(idCompra, valorParcela, mes);
                db.insert(ControleContract.ParcelasEntry.NOME_TABELA, null, valores);
            }
        } else {
            mes++;  // Aumenta mais 1 no mes porque ele só vai pagar no próximo mês
            ContentValues valores = this.setValores(idCompra, valorParcela, mes);
            db.insert(ControleContract.ParcelasEntry.NOME_TABELA, null, valores);
        }
    }

    private ContentValues setValores(long idCompra, double valorParcela, int mes){
        ContentValues values = new ContentValues();
        values.put(ControleContract.ParcelasEntry.COLUNA_FK_COMPRA, Integer.parseInt(String.valueOf(idCompra)));
        values.put(ControleContract.ParcelasEntry.COLUNA_VALOR_PARCELA, String.valueOf(new DecimalFormat("#.00").format(valorParcela)));
        values.put(ControleContract.ParcelasEntry.COLUNA_MES, mes);
        values.put(ControleContract.ParcelasEntry.COLUNA_ESTATOS, "dev");
        return values;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = this.dbHelp.getWritableDatabase();

        int match = this.URI_MATCHER.match(uri);
        switch (match){
            case CARTAO_ID:
                selection = ControleContract.CartaoEntry._ID + " = ?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                int rowsDeleted = db.delete(ControleContract.CartaoEntry.NOME_TABELA, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case COMPRAS_ID:
                selection = ControleContract.ComprasEntry._ID + " = ?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                int rowDeleted = db.delete(ControleContract.ComprasEntry.NOME_TABELA, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowDeleted;
            default:
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = this.dbHelp.getWritableDatabase();

        int match = this.URI_MATCHER.match(uri);
        switch (match){
            case CARTAO_ID:
                selection = ControleContract.CartaoEntry._ID + " = ?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                int rowsAfected = db.update(ControleContract.CartaoEntry.NOME_TABELA, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsAfected;
            case COMPRAS_ID:
                selection = ControleContract.ComprasEntry._ID + " = ?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                int rowAfected = this.alterarCompras(db, values, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowAfected;
            default:
                return 0;
        }
    }

    private int alterarCompras(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs){
        int rowsAfected = db.update(ControleContract.ComprasEntry.NOME_TABELA, values, selection, selectionArgs);
        int rowsDeleted = this.deletarParcelas(db, selectionArgs);
        Log.e("DELETE", "foram deletadas " + rowsDeleted + " da tabela 'parcelas'");
        this.inserirParcelas(db, values, Long.parseLong(selectionArgs[0]));
        return rowsAfected;
    }

    private int deletarParcelas(SQLiteDatabase db, String[] selectionArgs){
        String selection = ControleContract.ParcelasEntry.COLUNA_FK_COMPRA + " = ?";
        return db.delete(ControleContract.ParcelasEntry.NOME_TABELA, selection, selectionArgs);
    }
}
