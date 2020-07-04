package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;

@IgnoreExtraProperties
public class Unidade extends CustomObject implements Serializable {
    private String nome;
    private String tipoId;
    private String cidadeId;
    private String regiaoId;

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

    public Unidade(){super();}

    public Unidade(String id){
        super();
        this.id = id;
    }

    public Unidade(String id, String nome){
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

    public String getRegiaoId() {
        return regiaoId;
    }

    public void setRegiaoId(String regiaoId) {
        this.regiaoId = regiaoId;
    }

    public String getTipoId() {
        return tipoId;
    }

    public void setTipoId(String tipoId) {
        this.tipoId = tipoId;
    }

    @Override
    public String toString(){
        return nome;
    }

    @Override
    public boolean validar() {
        if (this.getNome() == null || this.getNome().length() <= 0) {
            mensagem = "É necessário informar um nome";
            return false;
        }

        if (this.getTipoId() == null || this.getTipoId().length() <= 0) {
            mensagem = "É necessário selecionar um tipo";
            return false;
        }

        if (this.getCidadeId() == null || this.getCidadeId().length() <= 0) {
            mensagem = "É necessário selecionar uma cidade";
            return false;
        }

        if (this.getRegiaoId() == null || this.getRegiaoId().length() <= 0) {
            mensagem = "É necessário selecionar uma região fiscal";
            return false;
        }

        return true;
    }
}
