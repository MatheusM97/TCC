package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.persistencies.Persistencia;

@IgnoreExtraProperties
public class Usuario extends CustomObject{
    private String cpf;
    private String senha;
    private String nome;
    private String email;
    private String telefone;

    @Exclude
    private List<Acesso> acessos = new ArrayList<>();

    public Usuario(){super();}

    public Usuario(String id, String nome){
        super();
        this.id = id;
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Exclude
    public List<Acesso> getAcessos() {
        return acessos;
    }

    @Exclude
    public void setAcessos(List<Acesso> acessos) {
        this.acessos = acessos;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }

    @Override
    public String toString(){
        return nome;
    }

    @Override
    public boolean validar() {
        return true;
    }

    public boolean validarLogin(){
        if(this.cpf == null || this.cpf.length() == 0){
            this.mensagem = "Informe o CPF";
            return false;
        }

        if(this.senha == null || this.senha.length() == 0){
            this.mensagem = "Informe a senha";
            return false;
        }

        return true;
    }

    public void realizarLogin(){
        Persistencia.getInstance().getAutenticar(this);
    }

    public boolean validarStatus(){
        switch (getStatus()){
            case ATIVO:
                return true;
            default:
                this.mensagem = "Usuário se encontra " + getStatus();
                return false;
        }
    }

    public boolean temId(){
        if(this.id != null && this.id.length() > 0)
            return true;

        return false;
    }

    @Override
    public boolean validarRemocao() {
        return true;
    }
}
