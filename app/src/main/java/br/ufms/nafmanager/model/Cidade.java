package br.ufms.nafmanager.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Cidade extends CustomObject {
    private String nome;
    private String estadoId;

    public Cidade() {
        super();
    }

    public Cidade(String id, String nome) {
        super();
        this.id = id;
        this.nome = nome;
    }

    public Cidade(String id, String nome, String estadoId, String estadoSigla) {
        super();
        this.id = id;
        this.nome = nome;
        this.estadoId = estadoId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(String estadoId) {
        this.estadoId = estadoId;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean validar() {
        return true;
    }
}
