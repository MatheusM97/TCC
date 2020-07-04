package br.ufms.nafmanager.activities.relatorios;

import java.util.Date;

import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.TipoDocumentoEnum;

public class FiltroRanking {
    private Date dataInicial;
    private Date dataFinal;
    private String tipo;
    private AtendimentoTipo atendimentoTipo;
    private TipoDocumentoEnum tipoDocumento;
    private String regiaoId;
    private String unidadeId;
    private String universidadeId;
    private String participanteId;
    private String conclusivo;

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public AtendimentoTipo getAtendimentoTipo() {
        return atendimentoTipo;
    }

    public void setAtendimentoTipo(AtendimentoTipo atendimentoTipo) {
        this.atendimentoTipo = atendimentoTipo;
    }

    public TipoDocumentoEnum getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoEnum tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(String unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getUniversidadeId() {
        return universidadeId;
    }

    public void setUniversidadeId(String universidadeId) {
        this.universidadeId = universidadeId;
    }

    public String getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(String participanteId) {
        this.participanteId = participanteId;
    }

    public String getConclusivo() {
        return conclusivo;
    }

    public void setConclusivo(String  conclusivo) {
        this.conclusivo = conclusivo;
    }

    public String getRegiaoId() {
        return regiaoId;
    }

    public void setRegiaoId(String regiaoId) {
        this.regiaoId = regiaoId;
    }
}
