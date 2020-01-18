package br.ufms.nafmanager.model;

public class Acesso extends CustomObject {
    private String usuarioId;
    private String universidadeId;
    private String unidadeId;
    private boolean participante = false;
    private boolean professor = false;
    private boolean supervisor = false;
    private boolean coordenador = false;
    private boolean representante = false;
    private boolean master = false;

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

    public boolean isParticipante() {
        return participante;
    }

    public void setParticipante(boolean participante) {
        this.participante = participante;
    }

    public boolean isSupervisor() {
        return supervisor;
    }

    public void setSupervisor(boolean supervisor) {
        this.supervisor = supervisor;
    }

    public boolean isCoordenador() {
        return coordenador;
    }

    public void setCoordenador(boolean coordenador) {
        this.coordenador = coordenador;
    }

    public boolean isRepresentante() {
        return representante;
    }

    public void setRepresentante(boolean representante) {
        this.representante = representante;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }

    @Override
    public boolean validar() {
        return true;
    }

    @Override
    public boolean validarRemocao() {
        return true;
    }
}
