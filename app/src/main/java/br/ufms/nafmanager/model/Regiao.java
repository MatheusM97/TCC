package br.ufms.nafmanager.model;

public class Regiao extends CustomObject {
    private String nome;

    public Regiao(){
        super();
    }

    public Regiao(String id, String nome){
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

    @Override
    public boolean validar() {
        if (this.nome == null || this.nome.length() <= 0) {
            this.mensagem = "É necessário informar um nome";
            return false;
        }
        return true;
    }

    @Override
    public boolean validarRemocao() {
        return true;
    }
}