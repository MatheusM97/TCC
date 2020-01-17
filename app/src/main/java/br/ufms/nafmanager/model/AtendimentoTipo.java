package br.ufms.nafmanager.model;

public class AtendimentoTipo {
    private String id;
    private String nome;

    public AtendimentoTipo(){super();}

    public AtendimentoTipo(String id, String nome) {
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
        return this.nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
