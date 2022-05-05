package com.example.controlecartao.dados;

import android.net.Uri;
import android.provider.BaseColumns;

public class ControleContract {

    public static final String AUTORIZADOR = "com.example.android.controle";
    public static final Uri URI_BASE = Uri.parse("content://" + AUTORIZADOR);
    public static final String PATH_CARTAO = "cartao";
    public static final String PATH_COMPRAS = "compras";

    public static final class CartaoEntry implements BaseColumns {

        public static final Uri URI_CONTENT = Uri.withAppendedPath(URI_BASE, PATH_CARTAO);

        public static final String NOME_TABELA = "cartao";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_NOME_CARTAO = "nome_cartao";
        public static final String COLUNA_DIA_PAGAMENTO = "dia_pagamento";
        public static final String COLUNA_DIA_FECHAMENTO = "dia_fechamento";
        public static final String COLUNA_NUMERO_FINAL_CARTAO = "n_final";

    }

    public static final class ComprasEntry implements BaseColumns {

        public static final Uri URI_CONTENT = Uri.withAppendedPath(URI_BASE, PATH_COMPRAS);

        public static final String NOME_TABELA = "compras";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_DESCRICAO = "descricao";
        public static final String COLUNA_DIA = "dia";
        public static final String COLUNA_MES = "mes";
        public static final String COLUNA_ANO = "ano";
        public static final String COLUNA_VALOR = "valor";
        public static final String COLUNA_QUANTIDADE_PARCELAS = "qtd_parcelas";
        public static final String COLUNA_FK_CARTAO = "fk_cartao";

    }

}
