package br.ufms.nafmanager.model;

import java.util.ArrayList;

public class Participante {
    private String acessoId;
    private String participanteId;
    private String participanteNome;
    private ArrayList<Atendimento> atendimentos = new ArrayList<>();

    public Participante(){}

    public Participante(String  acessoId){
        this.acessoId = acessoId;
    }

    public Participante(String  acessoId, String participanteId){
        this.acessoId = acessoId;
        this.participanteId = participanteId;
    }

    public Participante(String  acessoId, String participanteId, String participanteNome){
        this.acessoId = acessoId;
        this.participanteId = participanteId;
        this.participanteNome = participanteNome;
    }

    public String getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(String participanteId) {
        this.participanteId = participanteId;
    }

    public String getParticipanteNome() {
        return participanteNome;
    }

    public void setParticipanteNome(String participanteNome) {
        this.participanteNome = participanteNome;
    }

    public ArrayList<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(ArrayList<Atendimento> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public void addAtendimento(Atendimento atendimento){
        if(this.atendimentos == null){
            this.atendimentos = new ArrayList<>();
        }

        atendimentos.add(atendimento);
    }

    public String getAcessoId() {
        return acessoId;
    }

    public void setAcessoId(String acessoId) {
        this.acessoId = acessoId;
    }

    public String getMediaString(){
        if(atendimentos.size() >0 ){
            double media = getMedia();

            double minuto = media / 60L;
            double segundo = media % 60L;

            return String.format("%02d:%02d", (int)minuto, (int)segundo);
        }

        return "";
    }

    public double getMedia() {
        if (atendimentos.size() > 0) {
            double soma = 0;
            for (Atendimento atendimento : atendimentos) {
                String minuto = atendimento.getTempoAtendimento().split(":")[0];
                String segundo = atendimento.getTempoAtendimento().split(":")[1];
                soma += new Double(minuto) * 60;
                soma += new Double(segundo);
            }
            double media = soma / atendimentos.size();
            return media;
        }
        return 0;
    }
}
