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

import com.example.controlecartao.InfoCartaoActivity;
import com.example.controlecartao.R;
import com.example.controlecartao.dados.ControleContract;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MyViewHolder>{

    private Cursor cursor;

    public MainActivityAdapter(Cursor cursor){
        this.cursor = cursor;
    }

    public void setCursor(Cursor cursorData){
        this.cursor = cursorData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_mainactivity, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        this.cursor.moveToNext();
        holder.construir(this.cursor);
    }

    @Override
    public int getItemCount() {
        if(this.cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView numeroCartao;
        private TextView nomeCartao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            numeroCartao = itemView.findViewById(R.id.item_recyclerview_mainactivity_numero_cartao);
            nomeCartao = itemView.findViewById(R.id.item_recyclerview_mainactivity_nome_cartao);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cursor.moveToPosition(getAdapterPosition());
                    int idCartao = cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.CartaoEntry._ID));
                    Uri uri = ContentUris.withAppendedId(ControleContract.CartaoEntry.URI_CONTENT, idCartao);
                    Intent i = new Intent(v.getContext(), InfoCartaoActivity.class);
                    i.setData(uri);
                    v.getContext().startActivity(i);
                }
            });

        }

        private void construir(Cursor cursor){
            String numero = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_NUMERO_FINAL_CARTAO)));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow(ControleContract.CartaoEntry.COLUNA_NOME_CARTAO));
            numeroCartao.setText(numero);
            nomeCartao.setText(nome);
        }

    }
}
