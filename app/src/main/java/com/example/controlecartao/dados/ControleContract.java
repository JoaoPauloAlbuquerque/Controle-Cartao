package com.example.controlecartao.dados;

import android.net.Uri;
import android.provider.BaseColumns;

public class ControleContract {

    public static final String AUTORIZADOR = "com.example.android.controle";
    public static final Uri URI_BASE = Uri.parse("content://" + AUTORIZADOR);
    public static final String PATH_CARTAO = "cartao";
    public static final String PATH_COMPRAS = "compras";
    public static final String PATH_PARCELAS = "parcelas";

    public static final class CartaoEntry implements BaseColumns {

        public static final Uri URI_CONTENT = Uri.withAppendedPath(URI_BASE, PATH_CARTAO);

        public static final String NOME_TABELA = "cartao";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_NOME_CARTAO = "nome_cartao";
        public static final String COLUNA_DIA_PAGAMENTO = "dia_pagamento";
        public static final String COLUNA_DIA_FECHAMENTO = "dia_fechamento";
        public static final String COLUNA_NUMERO_FINAL_CARTAO = "n_final";

        public static String[] getArrayColunms(){
            return new String[]{_ID,
                    COLUNA_NOME_CARTAO,
                    COLUNA_DIA_PAGAMENTO,
                    COLUNA_DIA_FECHAMENTO,
                    COLUNA_NUMERO_FINAL_CARTAO
            };
        }

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

        public static String[] getArrayColunms(){
            return new String[]{_ID,
                    COLUNA_DESCRICAO,
                    COLUNA_VALOR,
                    COLUNA_DIA,
                    COLUNA_MES,
                    COLUNA_ANO,
                    COLUNA_QUANTIDADE_PARCELAS,
                    COLUNA_FK_CARTAO
            };
        }
    }

    public static final class ParcelasEntry implements BaseColumns {
        public static final Uri URI_CONTENT = Uri.withAppendedPath(URI_BASE, PATH_PARCELAS);

        public static final String NOME_TABELA = "parcelas";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUNA_FK_COMPRA = "fk_compra";
        public static final String COLUNA_VALOR_PARCELA = "valor_parcela";
        public static final String COLUNA_MES = "mes";
        public static final String COLUNA_ESTATOS = "estatos";

        public static String[] getArrayColunms(){
            return new String[]{_ID,
                    COLUNA_FK_COMPRA,
                    COLUNA_VALOR_PARCELA,
                    COLUNA_MES,
                    COLUNA_ESTATOS
            };
        }

    }

}
