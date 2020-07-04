package br.ufms.nafmanager.model;

import java.util.ArrayList;

public class Relatorios {
    private String universidadeNome;
    private String universidadeId;
    private ArrayList<Participante> participantes = new ArrayList<>();

    public Relatorios(){}

    public Relatorios(String universidadeId, String universidadeNome){
        this.universidadeId = universidadeId;
        this.universidadeNome = universidadeNome;
    }

    public String getUniversidadeNome() {
        return universidadeNome;
    }

    public void setUniversidadeNome(String universidadeNome) {
        this.universidadeNome = universidadeNome;
    }

    public String getUniversidadeId() {
        return universidadeId;
    }

    public void setUniversidadeId(String universidadeId) {
        this.universidadeId = universidadeId;
    }

    public ArrayList<Participante> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(ArrayList<Participante> participantes) {
        this.participantes = participantes;
    }

    public void addParticipante(Participante participante){
        if(participantes == null){
            participantes = new ArrayList<>();
        }

        participantes.add(participante);
    }

    public String getMedia(){
        if(participantes.size() >0 ){
            double soma = 0;
            int qtdAtendimentos = 0;
            for (Participante participante: participantes) {
                double media = participante.getMedia();
                qtdAtendimentos+= participante.getAtendimentos().size();

                soma+= media;
            }
            double media = soma /qtdAtendimentos;

            double minuto = media / 60L;
            double segundo = media % 60L;

            return String.format("%02d:%02d", (int)minuto, (int)segundo);
        }

        return "";
    }
}