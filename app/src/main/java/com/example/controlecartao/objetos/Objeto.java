package com.example.controlecartao.objetos;

public class Objeto {
    private int id;
    private String ondeComprou;
    private String valorCompra;
    private String dia;
    private String mes;
    private String ano;
    private int parcelas;
    private int fkCartao;

    public Objeto(int id, String ondeComprou, String valorCompra, String dia, String mes, String ano, int parcelas, int fkCartao) {
        this.id = id;
        this.ondeComprou = ondeComprou;
        this.valorCompra = valorCompra;
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
        this.parcelas = parcelas;
        this.fkCartao = fkCartao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOndeComprou() {
        return ondeComprou;
    }

    public void setOndeComprou(String ondeComprou) {
        this.ondeComprou = ondeComprou;
    }

    public String getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(String valorCompra) {
        this.valorCompra = valorCompra;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

    public int getFkCartao() {
        return fkCartao;
    }

    public void setFkCartao(int fkCartao) {
        this.fkCartao = fkCartao;
    }

}
