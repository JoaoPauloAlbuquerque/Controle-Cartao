package com.example.controlecartao.adaptadores;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.controlecartao.EditorActivity;
import com.example.controlecartao.R;
import com.example.controlecartao.dados.ControleContract;
import com.example.controlecartao.utils.Utils;

import java.util.ArrayList;

public class InfoCartaoActivitySubitemAdapter extends RecyclerView.Adapter<InfoCartaoActivitySubitemAdapter.SubitemMyViewHolder>{

    private Cursor cursor;
    private String mes;
    private ArrayList<Integer> list;

    public InfoCartaoActivitySubitemAdapter(Cursor cursor, String mes, ArrayList<Integer> list){
        this.cursor = cursor;
        this.mes = mes;
        this.list = list;
    }

    @NonNull
    @Override
    public SubitemMyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subitem_recyclerview_infocartaoactivity, parent, false);
        return new SubitemMyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubitemMyViewHolder holder, int position) {
        this.cursor.moveToPosition(this.list.get(position));
        String mes = this.cursor.getString(this.cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES));
        if(this.mes.equals(mes)){
            holder.contruir(this.cursor);
        }
    }

    @Override
    public int getItemCount() {
        if(this.list != null){
            return this.list.size();
        } else {
            return 0;
        }
    }

    public class SubitemMyViewHolder extends RecyclerView.ViewHolder{

        private TextView descricao;
        private TextView valor;
        private TextView data;
        private TextView parcelas;

        public SubitemMyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.descricao = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_onde_comprou);
            this.valor = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_valor_compra);
            this.data = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_data_compra);
            this.parcelas = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_parcelas);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(list.get(getAdapterPosition()));
                    int idCompra = cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry._ID));
                    Uri uri = ContentUris.withAppendedId(ControleContract.ComprasEntry.URI_CONTENT, idCompra);
                    Intent i = new Intent(v.getContext(), EditorActivity.class);
                    i.setData(uri);
                    v.getContext().startActivity(i);
                }
            });
        }

        private void contruir(Cursor cursor){
            String data = this.getData(cursor);

            this.descricao.setText(cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DESCRICAO)));
            this.valor.setText(
                    Utils.convertValueToCifrao(
                            Utils.convertDoubleToString(itemView.getContext(), cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_VALOR)))
                    )
            );
            this.data.setText(data);
            String p = cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS)) + "x";
            this.parcelas.setText(p);
        }

        private String getData(Cursor cursor){
            StringBuilder data = new StringBuilder();
            data.append(cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DIA)));
            data.append("/");
            data.append(cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES)));
            data.append("/");
            data.append(cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_ANO)));
            return data.toString();
        }

    }
}
