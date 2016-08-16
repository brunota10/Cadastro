package com.example.brunoalves.cadastro;

/**
 * Created by brunoalves on 08/08/2016.
 */
public class Estados extends MainActivity {
    public int id, pais;
    public String nome, uf;

    public Estados() {
    }

    public Estados(int id, String nome, String uf, int pais) {
        this.id = id;
        this.nome = nome;
        this.uf = uf;
        this.pais = pais;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public int getPais() {
        return pais;
    }

    public void setPais(int pais) {
        this.pais = pais;
    }

    @Override
    public String toString() {
        return uf;
    }
}
