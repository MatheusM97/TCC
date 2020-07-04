package br.ufms.nafmanager.model;

public class AtendidoTipo extends CustomObject {
    private String nome;

    public AtendidoTipo(){super();}

    public AtendidoTipo(String id, String nome){
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
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
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
