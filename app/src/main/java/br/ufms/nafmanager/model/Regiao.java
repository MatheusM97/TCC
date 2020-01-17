package br.ufms.nafmanager.model;

public class Regiao extends CustomObject {
    private String nome;

    private Long numero;

    public Regiao(){super();}

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    @Override
    public String toString(){
        return this.nome+ " ("+ this.numero +")";
    }

    @Override
    public boolean validar() {
        if (this.nome == null || this.nome.length() <= 0) {
            this.mensagem = "É necessário informar um nome";
            return false;
        }

        if (this.numero == null || this.numero <= 0) {
            this.mensagem = "É necessário informar um número";
            return false;
        }

        return true;
    }

    @Override
    public boolean validarRemocao() {
        return true;
    }
}