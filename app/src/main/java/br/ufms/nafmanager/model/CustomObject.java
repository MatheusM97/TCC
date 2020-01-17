package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.persistencies.Persistencia;

@IgnoreExtraProperties
public abstract class CustomObject implements Serializable {
    protected String id;
    protected StatusEnum status = StatusEnum.ATIVO;

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

    public abstract boolean validar();

    public abstract boolean validarRemocao();

    public boolean salvar(){
        if(validar()){
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

    public boolean remover(){
        if(validarRemocao()){
            Persistencia.getInstance().removerObjeto(this);
            this.mensagem = "Registro removido com sucesso!";
            return true;
        }

        return false;
    }
}