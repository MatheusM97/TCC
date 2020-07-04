package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

public class Universidade extends CustomObject implements Serializable {
    private String nome;
    private String cidadeId;
    private String unidadeId;

    @Exclude
    private ArrayList<Usuario> representantes = new ArrayList<>();

    @Exclude
    public ArrayList<Usuario> getRepresentantes(){
        return representantes;
    }

    @Exclude
    public void addRepresentante(Usuario us){
        if(this.representantes == null){
            this.representantes = new ArrayList<>();
        }

        this.representantes.add(us);
    }

    public Universidade() {
        super();
    }

    public Universidade(String id, String nome) {
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

    public String getCidadeId() {
        return cidadeId;
    }

    public void setCidadeId(String cidadeId) {
        this.cidadeId = cidadeId;
    }

    public String getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(String unidadeId) {
        this.unidadeId = unidadeId;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean validar() {
        if (this.getNome() == null || this.getNome().length() <= 0) {
            this.mensagem = "É necessário selecionar um nome";
            return false;
        }

        if (this.getCidadeId() == null || this.getCidadeId().length() <= 0) {
            this.mensagem = "É necessário selecionar uma cidade";
            return false;
        }
        return true;
    }
}
