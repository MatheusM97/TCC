package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;

import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.persistencies.Persistencia;

public class Acesso extends CustomObject {
    private String usuarioId;
    private String universidadeId;
    private String unidadeId;
    private String regiaoId;
    private AcessoTipoEnum tipo;
    private boolean aluno = false;
    private boolean professor = false;
    private boolean representante = false;
    private boolean moderador = false;
    private String supervisorId;

    @Exclude
    private boolean solicitando = false;

    public Acesso(){super();}

    public Acesso(String usuarioId, String usuarioNome, String universidadeId, String universidadeNome){
        this.usuarioId = usuarioId;
        this.universidadeId = universidadeId;
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

    public AcessoTipoEnum getTipo() {
        return tipo;
    }

    @Exclude
    public Long getTipoValor(){
        return tipo.getValor();
    }

    public void setTipo(AcessoTipoEnum tipo) {
        this.tipo = tipo;
    }

    public String getRegiaoId() {
        return regiaoId;
    }

    public void setRegiaoId(String regiaoId) {
        this.regiaoId = regiaoId;
    }

    public boolean isAluno() {
        return aluno;
    }

    public void setAluno(boolean aluno) {
        this.aluno = aluno;
    }

    public boolean isRepresentante() {
        return representante;
    }

    public void setRepresentante(boolean representante) {
        this.representante = representante;
    }

    public boolean isModerador() {
        return moderador;
    }

    public void setModerador(boolean moderador) {
        this.moderador = moderador;
    }

    public boolean isProfessor() {
        return professor;
    }

    public void setProfessor(boolean professor) {
        this.professor = professor;
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    @Exclude
    public boolean isSolicitando() {
        return solicitando;
    }

    @Exclude
    public void setSolicitando(boolean solicitando) {
        this.solicitando = solicitando;
    }

    @Override
    public String toString(){
        return "ACESSO [ usuarioId: " + usuarioId + ", papeis: " + listaPapeisAtivos() + "]";
    }

    @Override
    public boolean validar() {
        this.mensagem = null;

        if (this.getUsuarioId() == null || this.getUsuarioId().length() <= 0) {
            this.mensagem = "É necessário informar o usuário";
            return false;
        }

        if ((this.isAluno() || this.isProfessor())&& (this.getUniversidadeId() == null || this.getUniversidadeId().length() <= 0)) {
            this.mensagem = "É necessário informar a universidade";
            return false;
        }

        if (this.isRepresentante() && (this.getUnidadeId() == null || this.getUnidadeId().length() <= 0) &&
                (this.getUniversidadeId() == null || this.getUniversidadeId().length() <=0) && (this.getRegiaoId() == null || this.getRegiaoId().length() <= 0) ) {
            this.mensagem = "É necessário informar o vínculo de representante (unidade/universidade/região)";
            return false;
        }

        if (!this.isRepresentante() && !this.isProfessor() && !this.isAluno() && !this.isModerador()) {
            this.mensagem = "É necessário informar um papel.";
            return false;
        }

        return true;
    }

    public void validarDuplicidade(){
        Persistencia.getInstance().verificarAcessoDuplicado(this, StatusEnum.ATIVO);
    }

    @Override
    public boolean validarRemocao() {
        return true;
    }

    public String listaPapeisAtivos(){
        String papeis = null;

        if(isModerador())
            papeis = "Moderador";

        if(isRepresentante())
            papeis = papeis == null? "Representante" : papeis + " | Representante";

        if(isProfessor())
            papeis = papeis == null? "Professor" : papeis + " | Professor";

        if(isAluno())
            papeis = papeis == null? "Aluno" : papeis + " | Aluno";

        return papeis;
    }

    @Exclude
    public Long getNivelAcesso(){
        if(moderador)
            return 7L;

        if(representante && regiaoId != null && regiaoId.length() > 0)
            return 6L;

        if(representante && unidadeId != null && unidadeId.length() > 0)
            return 5L;

        if(representante && universidadeId != null && universidadeId.length() > 0)
            return 4L;

        if(professor)
            return 2L;

        if(aluno)
            return 1L;

        return 0L;
    }
}
