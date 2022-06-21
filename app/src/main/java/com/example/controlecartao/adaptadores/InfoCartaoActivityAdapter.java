package com.example.controlecartao.adaptadores;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.controlecartao.R;
import com.example.controlecartao.dados.ControleContract;
import com.example.controlecartao.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InfoCartaoActivityAdapter extends RecyclerView.Adapter<InfoCartaoActivityAdapter.ItemMyViewHolder>{

    private Cursor cursor;
    public ArrayList<ArrayList<Integer>> list;

    private int count;
    private String ultimoMes;
    private String mesAtual;

    public InfoCartaoActivityAdapter(Cursor cursor){
        this.cursor = cursor;
        this.ultimoMes = "00";
        this.mesAtual = "00";
        this.list = this.subtraiCursor();
    }

    private ArrayList<ArrayList<Integer>> subtraiCursor(){

        if(this.cursor != null) {
            ArrayList<ArrayList<Integer>> lista = new ArrayList<>();
            this.count = 0;
            if(this.cursor.moveToFirst()) {
                this.ultimoMes = this.cursor.getString(this.cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES));
                this.mesAtual = this.cursor.getString(this.cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES));
                do {
                    lista.add(this.getList());
                } while (this.cursor.moveToNext());
            }
            return lista;
        } else {
            return null;
        }
    }

    private ArrayList<Integer> getList(){
        ArrayList<Integer> list = new ArrayList<>();
        while(this.ultimoMes.equals(this.mesAtual)){
            list.add(this.count);
            this.count++;
            if(this.cursor.moveToNext()) {
                this.mesAtual = this.cursor.getString(this.cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES));
            } else {
                break;
            }
        }
        this.ultimoMes = this.mesAtual;
        this.cursor.moveToPrevious();
        return list;
    }

    public void setCursor(Cursor cursor){
        this.cursor = cursor;
        this.list = this.subtraiCursor();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_infocartaoactivity, parent, false);
        return new ItemMyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemMyViewHolder holder, int position) {
        this.cursor.moveToPosition(this.list.get(position).get(0));
        holder.contruir(
                this.cursor.getString(this.cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DIA)),
                this.cursor.getString(this.cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES)),
                this.cursor.getString(this.cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_ANO))
        );
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        } else {
            return list.size();
        }
    }

    public class ItemMyViewHolder extends RecyclerView.ViewHolder{

        private TextView titulo;
        private RecyclerView rv;
        public InfoCartaoActivitySubitemAdapter adapter;

        public ItemMyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_titulo);
            rv = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_recyclerview_interno);
        }

        public void contruir(String dia, String mes, String ano){
            String data = dia + "/" + mes + "/" + ano;
            Date dataConvertida = null;
            try {
                dataConvertida = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(data);
            } catch (ParseException e) {
                Log.e("ERRO", "ao converter string para data");
            }
            String dataFormatada = new SimpleDateFormat("MMMM/yyyy", Utils.getLocale(itemView.getContext())).format(dataConvertida);

            titulo.setText(dataFormatada);
            adapter = new InfoCartaoActivitySubitemAdapter(cursor, mes, list.get(getAdapterPosition()));
            rv.setAdapter(adapter);
        }
    }

}
