package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.persistencies.Persistencia;

@IgnoreExtraProperties
public abstract class CustomObject implements Serializable {
    protected String id;
    protected StatusEnum status = StatusEnum.ATIVO;
    protected String usuarioInclusao;
    protected String usuarioAlteracao;
    protected Date dataCriacao;
    protected Date dataAlteracao;

    public CustomObject(){
        if(Persistencia.getInstance().getUsuarioAtual() != null){
            usuarioInclusao = Persistencia.getInstance().getUsuarioAtual().toString();
        }
        dataCriacao = new Date();
    }

    @Exclude
    protected boolean edicao = false;

    @Exclude
    protected String mensagem;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Exclude
    public String getMensagem() {
        return mensagem;
    }

    @Exclude
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Exclude
    public boolean isEdicao() {
        return edicao;
    }

    @Exclude
    public void setEdicao(boolean edicao) {
        this.edicao = edicao;
    }

    public abstract boolean validar();

    public boolean salvar(){
        if(validar()){

            if(edicao){
                this.dataAlteracao = new Date();
                this.usuarioAlteracao = Persistencia.getInstance().getUsuarioAtual().toString();
            }

            Persistencia.getInstance().persistirObjeto(this);

            if(this.id != null && this.id.length() > 0){
                this.mensagem = "Registro salvo com sucesso!";
                return  true;
            }
            else
                this.mensagem = "Falha ao salvar o registro!";
        }

        return false;
    }

    public void remover(){
//        if(validarRemocao()){
            Persistencia.getInstance().removerObjeto(this);
//            this.mensagem = "Registro removido com sucesso!";
//            return true;
//        }
//
//        return false;
    }

    public<T extends CustomObject> T buscaObjetoNaLista(ArrayList<T> lista) {
        for (T object : lista) {
            if (object.getId().equals(this.getId()))
                return (T) object;
        }
        return null;
    }

    public boolean equalsClass(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }

    public String getUsuarioInclusao() {
        return usuarioInclusao;
    }

    public void setUsuarioInclusao(String usuarioInclusao) {
        this.usuarioInclusao = usuarioInclusao;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }
}