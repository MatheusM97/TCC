package br.ufms.nafmanager.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Exclude
    public boolean possuiAcesso(Acesso acesso){
        if(acesso.getId() != null){
            for(Acesso ac: acessos){
                if(ac.getId().equals(acesso.getId())){
                    return true;
                }
            }
        }

        return false;
    }

    @Exclude
    public void addAcesso(Acesso acesso){
        if(this.acessos == null){
            this.acessos = new ArrayList<>();
        }
        this.acessos.add(acesso);
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

        if(!senha.equals(senha2)){
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
        Persistencia.getInstance().carregaRegioes();
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

    public static boolean isValidCPF(String cpf){
        String cpfDigitos = cpf.replaceAll("[^0-9]","");
        if(cpfDigitos.length() ==11){
            if(" 11111111111".equals(cpfDigitos) ||
                "22222222222".equals(cpfDigitos) ||
                "33333333333".equals(cpfDigitos) ||
                "44444444444".equals(cpfDigitos) ||
                "55555555555".equals(cpfDigitos) ||
                "66666666666".equals(cpfDigitos) ||
                "77777777777".equals(cpfDigitos) ||
                "88888888888".equals(cpfDigitos) ||
                "99999999999".equals(cpfDigitos) ||
                "00000000000".equals(cpfDigitos)  )
                return false;

            int digito1 = Integer.parseInt(cpfDigitos.substring(9,10));
            int digito2 = Integer.parseInt(cpfDigitos.substring(10,11));

            int digito1Calculado = calculoModulo(cpfDigitos, 9, 10);
            int digito2Calculado = calculoModulo(cpfDigitos, 10, 11);

            if(digito1 != digito1Calculado || digito2 != digito2Calculado)
                return false;
            return true;
        }
        else{
            return false;
        }
    }

    public static int calculoModulo(String valor, int quantidadeDigitos, int peso){
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

    public static boolean isValidCNPJ(String cnpj){
        String cnpjDigitos = cnpj.replaceAll("[^0-9]","");
        if(cnpjDigitos.length() == 14){
            if("11111111111111".equals(cnpjDigitos) ||
               "22222222222222".equals(cnpjDigitos) ||
               "33333333333333".equals(cnpjDigitos) ||
               "44444444444444".equals(cnpjDigitos) ||
               "55555555555555".equals(cnpjDigitos) ||
               "66666666666666".equals(cnpjDigitos) ||
               "77777777777777".equals(cnpjDigitos) ||
               "88888888888888".equals(cnpjDigitos) ||
               "99999999999999".equals(cnpjDigitos) ||
               "00000000000000".equals(cnpjDigitos)  )
                return false;

            int digito1 = Integer.parseInt(cnpjDigitos.substring(12,13));
            int digito2 = Integer.parseInt(cnpjDigitos.substring(13,14));

            int digito1Calculado = calculoModulo11(cnpjDigitos,12);
            int digito2Calculado = calculoModulo11(cnpjDigitos,13);

            if(digito1 != digito1Calculado || digito2 != digito2Calculado)
                return false;
            return true;
        }
        else{
            return false;
        }
    }

    public static int calculoModulo11(String cnpj, int quantidadeDigitos){
        String digitos = cnpj.replaceAll("[^0-9]", "");

        int soma = 0;
        int peso = 2;
        for(int i = quantidadeDigitos; i > 0; i--){
            if(peso == 10)
                peso = 2;

            int valorInteiro = Integer.parseInt(digitos.substring(i-1, i));
            soma += peso * valorInteiro;

            peso++;
        }

        int resto = (soma % 11);

        if(resto <= 1)
            return 0;

        return 11 - resto;
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

    public static String criarHashSha256(String senha){
        String senhaHex = "";
        MessageDigest algorithm = null;
        try {
            algorithm = MessageDigest.getInstance("SHA-256");
            byte messageDigest[] = algorithm.digest(senha.getBytes("UTF-8"));

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02X", 0xFF & b));
            }
            senhaHex = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return senhaHex;
    }

    public void criptografarSenha() {
        this.senha = criarHashSha256(this.senha);
    }
}
