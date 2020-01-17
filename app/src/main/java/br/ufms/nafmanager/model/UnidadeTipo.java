package br.ufms.nafmanager.model;

public class UnidadeTipo extends CustomObject {
    private String nome;

    public UnidadeTipo(){super();}

    public UnidadeTipo(String id, String nome) {
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

    @Override
    public boolean validarRemocao() {
        return true;
    }
}
