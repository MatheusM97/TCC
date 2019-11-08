package br.ufms.nafmanager.model;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Atendimento {

    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    private Date dataAtendimento;
    private String tempoAtendimento;
    private ArrayList<String> atendimentoTipo;

    public Atendimento(){}

    public Atendimento(Date dataAtendimento, String tempoAtendimento, ArrayList<String> tipoIds){
        this.dataAtendimento = dataAtendimento;
        this.tempoAtendimento = tempoAtendimento;
        this.atendimentoTipo = new ArrayList<>();
        this.atendimentoTipo = tipoIds;
    }

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
}
