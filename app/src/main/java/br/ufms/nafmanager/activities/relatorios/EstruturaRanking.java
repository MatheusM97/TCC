package br.ufms.nafmanager.activities.relatorios;

public class EstruturaRanking {
    private String participanteId;
    private String universidadeId;
    private String unidadeId;
    private boolean finalizado;

    public String getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(String participanteId) {
        this.participanteId = participanteId;
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

    public boolean isFinalizado() {
        return finalizado;
    }

    public void setFinalizado(boolean finalizado) {
        this.finalizado = finalizado;
    }
}
