package com.example.controlecartao.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObservable;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.versionedparcelable.VersionedParcel;

import com.example.controlecartao.dados.ControleContract;
import com.example.controlecartao.dados.ControleDbHelp;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalcUtils {

    public static Double convertStringToDouble(String valor){
        String valorConvertido = valor.replace(".", "");
        try{
            return Double.parseDouble(valorConvertido.replace(",", "."));
        } catch (Exception e){
            Log.e("ERRO", "ao converter string para double");
            return 0.00;
        }
    }

    public static String convertDoubleToString(Double valor){
        Locale.setDefault(new Locale("pt", "br"));
        try{
            return new DecimalFormat("###,###.00").format(valor);
        } catch (Exception e){
            Log.e("ERRO", "ao converter double para string");
            return "0,00";
        }
    }

    public static String convertDoubleToString(String valor){
        Locale.setDefault(new Locale("pt", "br"));
        try{
            double v = Double.parseDouble(valor);
            return new DecimalFormat("###,###.00").format(v);
        } catch (Exception e){
            Log.e("ERRO", "ao converter double para string");
            return "0,00";
        }
    }

    public static String convertValueToCifrao(String valor){
        return "R$ " + valor;
    }

    public static class CalcularCompras{

        public static double calcular(Cursor cursor, Context context){
            double valor = 0.0f;
            String[] projection = new String[]{
                    ControleContract.ParcelasEntry._ID,
                    ControleContract.ParcelasEntry.COLUNA_FK_COMPRA,
                    ControleContract.ParcelasEntry.COLUNA_VALOR_PARCELA,
                    ControleContract.ParcelasEntry.COLUNA_MES,
                    ControleContract.ParcelasEntry.COLUNA_ESTATOS
            };

            while(cursor.moveToNext()){
                int currentId = cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry._ID));
                Uri uri = ContentUris.withAppendedId(ControleContract.ParcelasEntry.URI_CONTENT, (long) currentId);
                Cursor c = context.getContentResolver().query(uri, projection, null, new String[]{getMes()}, null);
                while(c.moveToNext()){
                    double valorAtual = Double.parseDouble(c.getString(c.getColumnIndexOrThrow(ControleContract.ParcelasEntry.COLUNA_VALOR_PARCELA)));
                    valor += valorAtual;
                }
                c.close();
            }
            return valor;
        }

        private static String getMes(){
            Date data = new Date();
            String dataFormatadaString = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(data);
            try {
                return new SimpleDateFormat("MM", Locale.US).format(new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dataFormatadaString));
            } catch (ParseException e) {
                Log.e("ERRO", "ao objeter data");
                return "";
            }
        }
    }

}
