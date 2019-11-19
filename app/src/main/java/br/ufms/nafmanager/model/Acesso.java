package br.ufms.nafmanager.model;

public class Acesso extends CustomObject {
    private String usuarioId;
    private String usuarioNome;
    private String universidadeId;
    private String universidadeNome;
    private String unidadeId;
    private String unidadeNome;
    private boolean participante = false;
    private boolean supervisor = false;
    private boolean coordenador = false;
    private boolean representante = false;
    private boolean master = false;

    public Acesso(){super();}

    public Acesso(String usuarioId, String usuarioNome, String universidadeId, String universidadeNome){
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.universidadeId = universidadeId;
        this.universidadeNome = universidadeNome;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public String getUniversidadeId() {
        return universidadeId;
    }

    public void setUniversidadeId(String universidadeId) {
        this.universidadeId = universidadeId;
    }

    public String getUniversidadeNome() {
        return universidadeNome;
    }

    public void setUniversidadeNome(String universidadeNome) {
        this.universidadeNome = universidadeNome;
    }

    public String getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(String unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getUnidadeNome() {
        return unidadeNome;
    }

    public void setUnidadeNome(String unidadeNome) {
        this.unidadeNome = unidadeNome;
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
    public String toString(){
        return usuarioNome;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }
}
