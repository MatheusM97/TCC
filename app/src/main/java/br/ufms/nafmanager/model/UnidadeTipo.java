package br.ufms.nafmanager.model;

public class UnidadeTipo extends CustomObject {
    private String nome;

    public UnidadeTipo(){}

    public UnidadeTipo(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString(){
        return nome;
    }
}
