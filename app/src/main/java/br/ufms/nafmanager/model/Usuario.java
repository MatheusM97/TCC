package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;

import br.ufms.nafmanager.adapters.MaskEditUtil;
import br.ufms.nafmanager.persistencies.Persistencia;
import ir.mirrajabi.searchdialog.core.Searchable;

@IgnoreExtraProperties
public class Usuario extends CustomObject implements Searchable {
    private String cpf;
    private String senha;
    @Exclude
    private String senha2;

    private String nome;
    private String email;
    private String telefone;

    @Exclude
    private ArrayList<Acesso> acessos;

    public Usuario(){super();}

    public Usuario(String nome){
        super();
        this.nome = nome;
    }

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
    public ArrayList<Acesso> getAcessos() {
        return acessos;
    }

    @Exclude
    public void setAcessos(ArrayList<Acesso> acessos) {
        this.acessos = acessos;
    }

    @Override
    public String toString(){
        return nome;
    }

    @Exclude
    public String getSenha2() {
        return senha2;
    }

    @Exclude
    public void setSenha2(String senha2) {
        this.senha2 = senha2;
    }

    @Override
    public boolean validar() {
        if (this.getCpf() == null || this.getCpf().length() <= 0) {
            this.mensagem = "É necessário informar o CPF!";
            return false;
        }

        if (this.getSenha() == null || this.getSenha().length() <= 0) {
            this.mensagem = "É necessário informar a senha!";
            return false;
        }

        if (this.getNome() == null || this.getNome().length() <= 0) {
            this.mensagem = "É necessário informar o nome!";
            return false;
        }

        if(!isValidCPF(this.getCpf())){
            this.mensagem = "CPF inválido!";
            return false;
        }

        return true;
    }

    public void validarSenhas(){
        if(this.senha2 == null || this.senha2.trim().length() <= 0 ){
            this.setMensagem("Confirme a senha!");
        }

        if(!senha2.equals(senha)){
            this.setMensagem("As senhas não são iguais!");
        }
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
        Persistencia.getInstance().carregaUnidades();
        Persistencia.getInstance().carregaUniversidades();
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


    public boolean isValidCPF(String cpf){
        String cpfDigitos = cpf.replaceAll("[^0-9]","");

        int digito1 = Integer.parseInt(cpfDigitos.substring(9,10));
        int digito2 = Integer.parseInt(cpfDigitos.substring(10,11));

        int digito1Calculado = calculoModulo(cpfDigitos, 9, 10);
        int digito2Calculado = calculoModulo(cpfDigitos, 10, 11);

        if(digito1 != digito1Calculado || digito2 != digito2Calculado)
            return false;
        return true;
    }

    public int calculoModulo(String valor, int quantidadeDigitos, int peso){
        String digitos = valor.replaceAll("[^0-9]", "");

        int soma = 0;
        for(int i = 0; i < quantidadeDigitos; i++){
            int valorInteiro = Integer.parseInt(digitos.substring(i, i+1));
            soma += peso * valorInteiro;
            peso--;
        }

        int resto = (soma * 10) % 11;

        if(resto < 10)
            return resto;

        return 0;
    }

    @Override
    @Exclude
    public String getTitle() {
        return getNome() + " (" + getEmail() + ")";
    }

    @Exclude
    public String getCpfMascarado(){
        return getCpf().substring(0,3) + ".***.**";
    }

    @Exclude
    public String getCpfSomenteDigitos(){
        if(this.cpf!= null){
           return MaskEditUtil.unmask(this.cpf);
        }
        return null;
    }
}
