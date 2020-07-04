package br.ufms.nafmanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Estado extends CustomObject {
    private String nome;
    private String sigla;

    public Estado(){super();}

    public Estado(String id, String nome) {
        super();
        this.id = id;
        this.nome = nome;
    }

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

    @Override
    public String toString(){
        return nome;
    }

    @Override
    public boolean validar() {
        return true;
    }
}
