package br.ufms.nafmanager.model;

public class Coordenador extends CustomObject {
    String nome;

    public Coordenador(){super();}

    public Coordenador(String id, String nome){
        super();
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
    public boolean validar() {
        return true;
    }

    @Override
    public boolean validarRemocao() {
        return true;
    }
}
