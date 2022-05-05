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

public class ControleProvider extends ContentProvider {

    public static final int CARTAO = 1;
    public static final int CARTAO_ID = 2;

    public static final int COMPRAS = 3;

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(ControleContract.AUTORIZADOR, ControleContract.PATH_CARTAO, CARTAO);
        URI_MATCHER.addURI(ControleContract.AUTORIZADOR, ControleContract.PATH_CARTAO + "/#", CARTAO_ID);

        URI_MATCHER.addURI(ControleContract.AUTORIZADOR, ControleContract.PATH_COMPRAS + "/#", COMPRAS);
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
            // Se eu passar apenas o nome da tabela cartao
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
            // Se eu não passar o selectionArgs, eu preciso passar o numero do cartao como id pra o Uri.
            // Caso contrário, eu tenho que passar o id do cartao junto com o Uri, e passar o numero do cartao dentro do selectionArgs
            case COMPRAS:
                if(selectionArgs == null){
                    selection = ControleContract.ComprasEntry.COLUNA_FK_CARTAO + " = ?";
                    selectionArgs = new String[]{
                            String.valueOf(ContentUris.parseId(uri))
                    };
                    cursor = db.query(ControleContract.ComprasEntry.NOME_TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                } else {
                    String idCompra = String.valueOf(ContentUris.parseId(uri));
                    String idCartao = selectionArgs[0];
                    selection = ControleContract.ComprasEntry._ID + " = ? AND " + ControleContract.ComprasEntry.COLUNA_FK_CARTAO + " = ?";
                    selectionArgs = new String[]{
                            idCompra,
                            idCartao
                    };
                    cursor = db.query(ControleContract.ComprasEntry.NOME_TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                }
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
                long idCompra = db.insert(ControleContract.ComprasEntry.NOME_TABELA, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, idCompra);
            default:
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
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
            default:
                return 0;
        }
    }
}
