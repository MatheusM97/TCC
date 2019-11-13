package br.ufms.nafmanager.model;

public class Cidade extends CustomObject {
    private String nome;
    private String estadoSigla;

    public Cidade() {
    }

    public Cidade(String id, String nome, String estadoSigla) {
        this.id = id;
        this.nome = nome;
        this.estadoSigla = estadoSigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstadoSigla() {
        return estadoSigla;
    }

    public void setEstadoSigla(String estadoSigla) {
        this.estadoSigla = estadoSigla;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }

    @Override
    public String toString() {
        return nome;
    }
}
