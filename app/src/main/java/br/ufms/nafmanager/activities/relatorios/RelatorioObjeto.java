package br.ufms.nafmanager.activities.relatorios;

import java.util.ArrayList;

public class RelatorioObjeto {
    private String id;
    private String nome;
    private String valor1;
    private String valor2;
    private String valor3;
    private ArrayList<RelatorioObjeto> detalhe = new ArrayList<>();
    private ArrayList<RelatorioObjeto> detalhe2 = new ArrayList<>();

    private boolean detalhesFinalizados = false;

    public RelatorioObjeto(){}

    public RelatorioObjeto(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public RelatorioObjeto(String id, String nome, String valor1) {
        this.id = id;
        this.nome = nome;
        this.valor1 = valor1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor1() {
        return valor1;
    }

    public void setValor1(String valor1) {
        this.valor1 = valor1;
    }

    public String getValor2() {
        return valor2;
    }

    public void setValor2(String valor2) {
        this.valor2 = valor2;
    }

    public String getValor3() {
        return valor3;
    }

    public void setValor3(String valor3) {
        this.valor3 = valor3;
    }

    public ArrayList<RelatorioObjeto> getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(ArrayList<RelatorioObjeto> detalhe) {
        this.detalhe = detalhe;
    }

    public void addDetalhe(RelatorioObjeto detalhe)  {
        if(this.detalhe == null){
            this.detalhe = new ArrayList<>();
        }
        this.detalhe.add(detalhe);
    }

    public boolean isDetalhesFinalizados() {
        if(detalhe != null){
            for(RelatorioObjeto item: detalhe){
                 if(!item.detalhesFinalizados)
                     return false;
            }
        }

        return this.detalhesFinalizados;
    }

    public void marcarDetalhesFinalizados() {
        if(detalhe != null) {
            for (RelatorioObjeto item : detalhe) {
                item.marcarDetalhesFinalizados();
            }
        }

        this.detalhesFinalizados = true;
    }

    public boolean existeDetalhe(String id){
        if(detalhe != null){
            for(RelatorioObjeto item: detalhe){
                if(item.getId().equals(id)){
                    return true;
                }
            }
        }

        return false;
    }


    public boolean existeDetalhe2(String id){
        if(detalhe2 != null){
            for(RelatorioObjeto item: detalhe2){
                if(item.getId().equals(id)){
                    return true;
                }
            }
        }

        return false;
    }


    public ArrayList<RelatorioObjeto> getDetalhe2() {
        return detalhe2;
    }

    public void setDetalhe2(ArrayList<RelatorioObjeto> detalhe2) {
        this.detalhe2 = detalhe2;
    }

    public void addDetalhe2(RelatorioObjeto detalhe2)  {
        if(this.detalhe2 == null){
            this.detalhe2 = new ArrayList<>();
        }
        this.detalhe2.add(detalhe2);
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
