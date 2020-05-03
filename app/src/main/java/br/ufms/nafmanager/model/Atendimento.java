package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Atendimento extends CustomObject {
    @Exclude
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

    private Date dataAtendimento;
    private String tempoAtendimento;
    private Boolean conclusivo;
    private ArrayList<String> atendimentoTipoId;
    private String atendidoTipoId;
    private String acessoId;
    private String atendidoNome;
    private String atendidoDocumento;
    private TipoDocumentoEnum atendidoTipoDocumento;
    private String atendidoFone;
    private boolean retroativo;

    @Exclude
    private Date atendimentoInicio;

    public Atendimento(){
        super();
        this.atendimentoInicio = new Date();
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

    public ArrayList<String> getAtendimentoTipoId() {
        return atendimentoTipoId;
    }

    public void setAtendimentoTipoId(ArrayList<String> atendimentoTipoId) {
        this.atendimentoTipoId = atendimentoTipoId;
    }

    public Boolean getConclusivo() {
        return conclusivo;
    }

    public void setConclusivo(Boolean conclusivo) {
        this.conclusivo = conclusivo;
    }

    public String getAtendidoTipoId() {
        return atendidoTipoId;
    }

    public void setAtendidoTipoId(String atendidoTipoId) {
        this.atendidoTipoId = atendidoTipoId;
    }

    public String getAcessoId() {
        return acessoId;
    }

    public void setAcessoId(String acessoId) {
        this.acessoId = acessoId;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
        return true;

        return false;
    }

    @Override
    public boolean validar() {
        if(atendimentoTipoId == null || atendimentoTipoId.size() == 0){
            setMensagem("É necessário informar ao menos um atendimento!");
            return false;
        }

        if(acessoId == null || acessoId.trim().length() == 0){
            setMensagem("Houve um problema ao vincular o acesso no atendimento!");
            return false;
        }

        if(atendidoTipoId == null || atendidoTipoId.trim().length() == 0){
            setMensagem("É necessário informar tipo do atendido!");
            return false;
        }
        return true;
    }

    @Override
    public boolean salvar(){
        long diferenca = Math.abs(new Date().getTime() - atendimentoInicio.getTime());
        this.setTempoAtendimento(TimeUnit.SECONDS.convert(diferenca, TimeUnit.MILLISECONDS));

        Date data = new Date();
        data.setHours(00);
        data.setMinutes(00);

        if(dataAtendimento.compareTo(data) < 1){
            retroativo = true;
        }

        return super.salvar();
    }

    public String getAtendidoNome() {
        return atendidoNome;
    }

    public void setAtendidoNome(String atendidoNome) {
        this.atendidoNome = atendidoNome;
    }

    public String getAtendidoDocumento() {
        return atendidoDocumento;
    }

    public void setAtendidoDocumento(String atendidoDocumento) {
        this.atendidoDocumento = atendidoDocumento;
    }

    public TipoDocumentoEnum getAtendidoTipoDocumento() {
        return atendidoTipoDocumento;
    }

    public void setAtendidoTipoDocumento(TipoDocumentoEnum atendidoTipoDocumento) {
        this.atendidoTipoDocumento = atendidoTipoDocumento;
    }

    public String getAtendidoFone() {
        return atendidoFone;
    }

    public void setAtendidoFone(String atendidoFone) {
        this.atendidoFone = atendidoFone;
    }

    @Override
    public boolean validarRemocao() {
        return true;
    }

    public boolean isRetroativo() {
        return retroativo;
    }

    public void setRetroativo(boolean retroativo) {
        this.retroativo = retroativo;
    }
}
