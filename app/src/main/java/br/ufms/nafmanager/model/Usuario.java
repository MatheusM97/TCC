package br.ufms.nafmanager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Usuario  extends CustomObject{
    private String cpf;
    private String senha;
    private String nome;
    private String email;
    private String telefone;
    private List<String> universidadesVinculadas;
    private List<String> unidadesVinculadas;
    private Map<String,Boolean> permissoes;

    public Usuario(){
        this.status = "rascunho";
        this.unidadesVinculadas = new ArrayList<>();
        this.universidadesVinculadas = new ArrayList<>();
        this.permissoes = new HashMap<>();
        this.permissoes.put("participante", true);
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

    public List<String> getUniversidadesVinculadas() {
        return universidadesVinculadas;
    }

    public void setUniversidadesVinculadas(List<String> universidadesVinculadas) {
        this.universidadesVinculadas = universidadesVinculadas;
    }

    public List<String> getUnidadesVinculadas() {
        return unidadesVinculadas;
    }

    public void setUnidadesVinculadas(List<String> unidadesVinculadas) {
        this.unidadesVinculadas = unidadesVinculadas;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass().getName().equals(this.getClass().getName()))
            return true;

        return false;
    }
}
