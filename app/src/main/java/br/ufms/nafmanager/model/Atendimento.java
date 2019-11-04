package br.ufms.nafmanager.model;

import java.util.ArrayList;
import java.util.Date;

public class Atendimento {

    private Date dataAtendimento;
    private Long tempoFinalizacao;
    private ArrayList<String> atendimentoTipo;

    public Atendimento(){}

    public Atendimento(Date dataAtendimento, Long tempoFinalizacao, ArrayList<String> tipoIds){
        this.dataAtendimento = dataAtendimento;
        this.tempoFinalizacao = tempoFinalizacao;
        this.atendimentoTipo = new ArrayList<>();
        this.atendimentoTipo = tipoIds;
    }

    public Date getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(Date dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public Long getTempoFinalizacao() {
        return tempoFinalizacao;
    }

    public void setTempoFinalizacao(Long tempoFinalizacao) {
        this.tempoFinalizacao = tempoFinalizacao;
    }

    public ArrayList<String> getAtendimentoTipo() {
        return atendimentoTipo;
    }

    public void setAtendimentoTipo(ArrayList<String> atendimentoTipo) {
        this.atendimentoTipo = atendimentoTipo;
    }
}
