package com.example.brunoalves.cadastro;

/**
 * Created by brunoalves on 18/08/2016.
 */
public class Cidades extends MainActivity {
    private int id;
    private String nome;
    private int estado;

    public Cidades() {
    }

    public Cidades(int id, String nome, int estado) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
