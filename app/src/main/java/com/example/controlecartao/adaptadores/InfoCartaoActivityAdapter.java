package com.example.controlecartao.adaptadores;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.versionedparcelable.VersionedParcel;

import com.example.controlecartao.EditorActivity;
import com.example.controlecartao.R;
import com.example.controlecartao.dados.ControleContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InfoCartaoActivityAdapter extends RecyclerView.Adapter<InfoCartaoActivityAdapter.MyViewHolder>{

    private Cursor cursor;

    private boolean primeiroTitulo = true;
    private String alterarTitulo = "";

    public InfoCartaoActivityAdapter(Cursor cursor){
        this.cursor = cursor;
    }

    public void setCursor(Cursor cursor){
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_infocartaoactivity, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        this.cursor.moveToPosition(position);
        holder.contruir(this.cursor);
    }

    @Override
    public int getItemCount() {
        if(this.cursor != null){
            return this.cursor.getCount();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tituloData;
        private TextView descricao;
        private TextView valor;
        private TextView data;
        private TextView parcelas;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tituloData = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_titulo_data);
            this.descricao = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_onde_comprou);
            this.valor = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_valor_compra);
            this.data = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_data_compra);
            this.parcelas = itemView.findViewById(R.id.item_recyclerview_infocartaoactivity_parcelas);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    int idCompra = cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry._ID));
                    Uri uri = ContentUris.withAppendedId(ControleContract.ComprasEntry.URI_CONTENT, idCompra);
                    Intent i = new Intent(v.getContext(), EditorActivity.class);
                    i.setData(uri);
                    v.getContext().startActivity(i);
                }
            });
        }

        private void contruir(Cursor cursor){
            String dataRecebida = this.getData(cursor);
            Date dataConvertida = null;
            try {
                dataConvertida = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dataRecebida);
            } catch (ParseException e) {
                Log.e("Erro", "ao converter a string data para tipo Date");
            }
            String dataFormatada = new SimpleDateFormat("MMMM", new Locale("pt", "BR")).format(dataConvertida) + "/" +
                    new SimpleDateFormat("yyyy", new Locale("pt", "BR")).format(dataConvertida);

            String mes = cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_MES));
            if(primeiroTitulo){
                alterarTitulo = mes;
                primeiroTitulo = false;
                this.tituloData.setText(dataFormatada);
            } else if(!alterarTitulo.equals(mes)){
                alterarTitulo = mes;
                this.tituloData.setText(dataFormatada);
            } else {
                this.tituloData.setVisibility(View.GONE);
            }

            this.descricao.setText(cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_DESCRICAO)));
            String v = "R$ " + cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_VALOR));
            this.valor.setText(v);
            this.data.setText(dataRecebida);
            String p = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.ComprasEntry.COLUNA_QUANTIDADE_PARCELAS))) + "x";
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
