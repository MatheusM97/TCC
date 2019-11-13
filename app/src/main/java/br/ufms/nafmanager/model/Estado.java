package br.ufms.nafmanager.model;

import java.util.ArrayList;
import java.util.List;

public class Estado extends CustomObject {
    private String nome;
    private String sigla;
    private List<Cidade> cidades;

    public Estado(){super(); this.cidades = new ArrayList<Cidade>();}

    public Estado(String id, String nome, String sigla){
        super();
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public List<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }

    @Override
    public String toString(){
        return nome;
    }
}
