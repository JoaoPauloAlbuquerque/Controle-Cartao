package com.example.controlecartao.utils;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.controlecartao.R;
import com.example.controlecartao.dados.ControleContract;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    /**
     * Formata os valores para o formato pt-br adicionando vírgulas e pontos,
     * este método recebe um double como parâmetro.
     * @param valor valor que será convertido
     * @return
     */
    public static String convertDoubleToString(Context context, Double valor){
        Locale.setDefault(getLocale(context));
        try{
            return new DecimalFormat("###,##0.00").format(valor);
        } catch (Exception e){
            Log.e("ERRO", "ao converter double para string");
            return "0,00";
        }
    }

    /**
     * Formata os valores para o formato pt-br adicionando virgulas e pontos,
     * este método recebe uma string como parâmetro.
     * @param valor valor que será convertido
     * @return
     */
    public static String convertDoubleToString(Context context, String valor){
        Locale.setDefault(getLocale(context));
        try{
            double v = Double.parseDouble(valor);
            String str = String.valueOf(v);
            if(isInt(v)){
                int i = (int) v;
                str = String.valueOf(i);
            }
            return new DecimalFormat("###,##0.00").format(Double.parseDouble(str));
        } catch (Exception e){
            Log.e("ERRO", "ao converter double para string" + e);
            return "0,00";
        }
    }

    /**
     * Remove todos os pontos e vírgulas do valor.
     * Este método é usado para preparar os valores para inserir no banco de dados
     * @param s é o valor
     * @return retorna o valor sem a formatação
     */
    public static String replaceSimbolosValorToDb(String s){
        String valor = s.replace(".", "");
        valor = valor.replace(",", ".");
        return valor;
    }

    /**
     * Converte os valores que vem do DB para um formato que seja
     * possível adicionar no EditText da EditorActivity
     * @param s
     * @return
     */
    public static String convertValoresDbToEditText(String s){
        double v = Double.parseDouble(s) * 100;
        if(isInt(v)){
            int i = (int) v;
            return String.valueOf(i);
        }
        return String.valueOf(v);
    }

    private static boolean isInt(double d){
        return (d % 1) == 0;
    }

    /**
     * Adiciona um cifrão antes do valor.
     * Este método é usado para setar os valores nos RecyclerViews
     * @param context
     * @param valor
     * @return
     */
    public static String convertValueToCifrao(Context context, String valor){
        return context.getString(R.string.cifrao) + " " + valor;
    }

    public static Locale getLocale(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Log.e("API > 24", context.getResources().getConfiguration().getLocales().get(0).toString());
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            Log.e("API < 24", context.getResources().getConfiguration().locale.toString());
            return context.getResources().getConfiguration().locale;
        }
    }

    public static String getMes(){
        Date data = new Date();
        String dataFormatadaString = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(data);
        try {
            return new SimpleDateFormat("MM", Locale.US).format(new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dataFormatadaString));
        } catch (ParseException e) {
            Log.e("ERRO", "ao objeter mes " + e);
            return "";
        }
    }

    public static String getAno(){
        Date data = new Date();
        String dataFormatadaString = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(data);
        try{
            return new SimpleDateFormat("yyyy", Locale.US).format(new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dataFormatadaString));
        } catch (ParseException e){
            Log.e("ERRO", "ao objeter ano " + e);
            return "";
        }
    }

    public static String getMesPorExtenso(Context context){
        Date data = new Date();
        String dataFormatadaString = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(data);
        try {
            return new SimpleDateFormat("MMMM", getLocale(context)).format(new SimpleDateFormat("dd/MM/yyyy", getLocale(context)).parse(dataFormatadaString));
        } catch (ParseException e) {
            Log.e("ERRO", "ao objeter data");
            return "";
        }
    }

    public static void createAlertDialog(Context context, String message, DialogInterface.OnClickListener confirmationOnClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.sim), confirmationOnClickListener);
        builder.setNegativeButton(context.getString(R.string.nao), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static class CalcularCompras{

        public static double calcular(Cursor cursor, Context context){
            double valor = 0.0f;
            String[] projection = ControleContract.ParcelasEntry.getArrayColunms();
            while(cursor.moveToNext()){
                int currentId = cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry._ID));
                Uri uri = ContentUris.withAppendedId(ControleContract.ParcelasEntry.URI_CONTENT, (long) currentId);
                Cursor c = context.getContentResolver().query(uri, projection, null, new String[]{getMes(), getAno()}, null);
                while(c.moveToNext()){
                    String estatus = c.getString(c.getColumnIndexOrThrow(ControleContract.ParcelasEntry.COLUNA_ESTATUS));
                    if(estatus.equals(ControleContract.ParcelasEntry.ESTATUS_COMPRA_DEV)) {
                        double valorAtual = Double.parseDouble(c.getString(c.getColumnIndexOrThrow(ControleContract.ParcelasEntry.COLUNA_VALOR_PARCELA)));
                        valor += valorAtual;
                    }
                }
                c.close();
            }
            return valor;
        }
    }

}
