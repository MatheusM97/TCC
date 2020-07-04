package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;

import br.ufms.nafmanager.persistencies.Persistencia;

public class Regiao extends CustomObject {
    private String nome;
    private ArrayList<String> estadosId = new ArrayList<>();

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

    public ArrayList<String> getEstados() {
        return estadosId;
    }

    @Exclude
    public String getEstadosSigla(){
        String retorno = "";
        Persistencia.getInstance().carregaEstados();
        for(Estado estado: Persistencia.getInstance().getEstados()){
            if(estadosId.contains(estado.getId())){
                if(retorno != ""){
                    retorno += ", ";
                }
                retorno+= estado.getSigla();
            }
        }

        return  retorno;
    }

    public void setEstados(ArrayList<String> estados) {
        this.estadosId = estados;
    }

    public void addEstado(String estadoId){
        if(this.estadosId == null ){
            this.estadosId = new ArrayList<>();
        }

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

        if(this.estadosId == null || this.estadosId.size() == 0){
            this.mensagem = "É necessário informar ao menos um Estado";
            return false;
        }

        return true;
    }
}