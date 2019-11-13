package br.ufms.nafmanager.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Atendimento extends CustomObject {

    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    private Date dataAtendimento;
    private String tempoAtendimento;
    private Boolean atendimentoConclusivo;
    private ArrayList<String> atendimentoTipo;

    private String usuarioId;
    private String universidadeId;
    private String unidadeId;

    public Atendimento(){}

    public Date getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(Date dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getTempoAtendimento() {
        return this.tempoAtendimento;
    }

    public void setTempoAtendimento(Long tempoAtendimento) {
        Long minuto = tempoAtendimento /60L;
        Long segundo = tempoAtendimento %60L;

       this.tempoAtendimento =  String.format("%02d:%02d", minuto, segundo);
    }

    public ArrayList<String> getAtendimentoTipo() {
        return atendimentoTipo;
    }

    public void setAtendimentoTipo(ArrayList<String> atendimentoTipo) {
        this.atendimentoTipo = atendimentoTipo;
    }

    public Boolean getAtendimentoConclusivo() {
        return atendimentoConclusivo;
    }

    public void setAtendimentoConclusivo(Boolean atendimentoConclusivo) {
        this.atendimentoConclusivo = atendimentoConclusivo;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUniversidadeId() {
        return universidadeId;
    }

    public void setUniversidadeId(String universidadeId) {
        this.universidadeId = universidadeId;
    }

    public String getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(String unidadeId) {
        this.unidadeId = unidadeId;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
        return true;

        return false;
    }
}
