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
import com.example.controlecartao.objetos.Objeto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InfoCartaoActivityAdapter extends RecyclerView.Adapter<InfoCartaoActivityAdapter.ItemMyViewHolder>{

    private Cursor cursor;
    private ArrayList<Objeto> list;

    public InfoCartaoActivityAdapter(Cursor cursor){
        this.cursor = cursor;
        this.list = this.subtraiCursor(this.cursor);
    }

    private ArrayList<Objeto> subtraiCursor(Cursor c){
        if(c != null) {
            ArrayList<Objeto> mList = new ArrayList<>();
            String ultimoMesAnalizado = "00";
            if(c.moveToFirst()) {
                do {
                    if (ultimoMesAnalizado.equals("00")) {
                        ultimoMesAnalizado = c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES));
                        mList.add(
                                new Objeto(
                                        c.getInt(c.getColumnIndexOrThrow(ControleContract.ComprasEntry._ID)),
                                        c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DESCRICAO)),
                                        c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_VALOR)),
                                        c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DIA)),
                                        c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES)),
                                        c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_ANO)),
                                        c.getInt(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS)),
                                        c.getInt(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_FK_CARTAO))
                                )
                        );
                    } else {
                        String mesAtual = c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES));
                        if (!ultimoMesAnalizado.equals(mesAtual)) {
                            ultimoMesAnalizado = mesAtual;
                            mList.add(
                                    new Objeto(
                                            c.getInt(c.getColumnIndexOrThrow(ControleContract.ComprasEntry._ID)),
                                            c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DESCRICAO)),
                                            c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_VALOR)),
                                            c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DIA)),
                                            c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES)),
                                            c.getString(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_ANO)),
                                            c.getInt(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS)),
                                            c.getInt(c.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_FK_CARTAO))
                                    )
                            );
                        }
                    }
                } while (c.moveToNext());
            }
            return mList;
        } else {
            return null;
        }
    }

    public void setCursor(Cursor cursor){
        this.cursor = cursor;
        this.list = this.subtraiCursor(this.cursor);
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
        holder.contruir(
                this.list.get(position).getDia(),
                this.list.get(position).getMes(),
                this.list.get(position).getAno()
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
            String dataFormatada = new SimpleDateFormat("MMMM/yyyy", new Locale("pt", "br")).format(dataConvertida);

            titulo.setText(dataFormatada);
            adapter = new InfoCartaoActivitySubitemAdapter(cursor, mes);
            rv.setAdapter(adapter);
        }
    }

}
