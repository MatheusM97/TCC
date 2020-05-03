package br.ufms.nafmanager.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.usuario.UsuarioInserir;
import br.ufms.nafmanager.adapters.AcessoAdapter;
import br.ufms.nafmanager.adapters.MaskEditUtil;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class LoginActivity extends CustomActivity {

    private EditText login;
    private EditText senha;
    private Button btn_login;
    private Button btn_registrar;
    private Usuario usuarioAtual;
    private AcessoAdapter acessoAdapter;
    private int timeout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        this.login = (EditText) findViewById(R.id.et_login);
        this.senha = (EditText) findViewById(R.id.et_senha);
        this.btn_login = (Button) findViewById(R.id.btn_login);
        this.btn_registrar = (Button) findViewById(R.id.btn_registrar_se);

        Persistencia.getInstance().setUsuarioAtual(null);
        Persistencia.getInstance().setAcessoAtual(null);
        carregaComponentes();

//        realizarLogin();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Persistencia.getInstance().setUsuarioAtual(null);
        Persistencia.getInstance().setAcessoAtual(null);
    }

    private void carregaComponentes() {
        senha.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    realizarLogin();
                    return true;
                }
                return false;
            }

        });

        this.login.addTextChangedListener(MaskEditUtil.mask(login, MaskEditUtil.FORMAT_CPF));

        this.btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });

        this.btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent novaIntent = new Intent(getBaseContext(), UsuarioInserir.class);
                startActivity(novaIntent);
            }
        });
    }

    private void realizarLogin() {
        this.usuarioAtual = new Usuario();

        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        if (this.login.getText() != null && this.login.getText().length() > 0)
            this.usuarioAtual.setCpf(this.login.getText().toString());
        if (this.senha.getText() != null && this.login.getText().length() > 0)
            this.usuarioAtual.setSenha(this.senha.getText().toString());

//        this.usuarioAtual.setCpf("111.111.111-11");//admin
//        this.usuarioAtual.setCpf("018.758.051-09");//aluno
//        this.usuarioAtual.setCpf("555.555.555-55"); //Professor
//        this.usuarioAtual.setCpf("333.333.333-33"); //representante universidade
//        this.usuarioAtual.setCpf("444.444.444-44"); //representante da regiao

//        this.usuarioAtual.setSenha("admin.!@#");

//        this.usuarioAtual.setCpf("732.898.260-82");
//        this.usuarioAtual.setSenha("73289826082");

        //TODO: remover Acesso!

        if(usuarioAtual.validarLogin()){
            usuarioAtual.realizarLogin();
        }
        else{
            Toast.makeText(this, usuarioAtual.getMensagem(), Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog();
        timeout = 0;
        validarLogin();
    }

    private void validarLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timeout++;
                System.out.println("timeout login: " +timeout);
                usuarioLogado();
            }
        }, 500);
    }

    private void usuarioLogado() {
        usuarioAtual = Persistencia.getInstance().getUsuarioAtual();

        if(usuarioAtual.getMensagem() != null && usuarioAtual.getMensagem().length() > 0){
            hideDialog();
            Toast.makeText(this, usuarioAtual.getMensagem(), Toast.LENGTH_SHORT).show();
            return;
        }

        if(timeout >= 60) {
            mensagemTimeout();
            return;
        }

        if(usuarioAtual.temId() && usuarioAtual.getAcessos() != null ){
             timeout = 0;
             aguardaCarregarUnivs();
        }
        else
            validarLogin();
    }

    private void iniciar() {
        if (usuarioAtual.validarStatus()) {
            iniciarMain();
        }
        else
            Toast.makeText(this, usuarioAtual.getMensagem(), Toast.LENGTH_SHORT).show();
    }

    public void aguardaCarregarUnivs(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timeout++;
                checkUnivs();
            }
        }, 500);
    }

    private void checkUnivs() {
        if(!Persistencia.getInstance().carregouUniversidades){
            aguardaCarregarUnivs();
        }

        if(Persistencia.getInstance().carregouUniversidades){
            selecionaAcessos();
        }

        if(timeout == 40){
            mensagemTimeout();
        }
    }

    private void selecionaAcessos() {
        if (usuarioAtual.getAcessos().size() > 1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);

            LayoutInflater inflater = getLayoutInflater();
            View convertView = (View) inflater.inflate(R.layout.lista_selecionar_acesso, null);
            alertDialog.setView(convertView);

            ListView lv = (ListView) convertView.findViewById(R.id.lv_selecionarAcesso);

            final AlertDialog alert = alertDialog.create();

            alert.setTitle(" Selecione o acesso"); // Title

            acessoAdapter = new AcessoAdapter(LoginActivity.this, usuarioAtual.getAcessos());

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Persistencia.getInstance().setAcessoAtual(acessoAdapter.getItem(position));
                    iniciar();
                    alert.hide();
                }
            });

            lv.setAdapter(acessoAdapter);
            alert.show();
        }
        else{
            if(usuarioAtual.getAcessos().size() > 0){
                Persistencia.getInstance().setAcessoAtual(usuarioAtual.getAcessos().get(0));
            }
            else{
                Persistencia.getInstance().setAcessoAtual(null);
                Toast.makeText(this, "Nenhum ACESSO encontrado! Favor solicitar um acesso.", Toast.LENGTH_LONG).show();
            }

            iniciar();
        }

        login.setText("");
        senha.setText("");
        hideDialog();
    }

    private void iniciarMain() {
        System.out.println("INICIADO MAIN COM ACESSO: " + Persistencia.getInstance().getAcessoAtual());
        Intent novaIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(novaIntent);
    }

    private void mensagemTimeout() {
        hideDialog();
        Toast.makeText(this, "Tempo esgotado para conexão com o servidor!", Toast.LENGTH_SHORT).show();
    }
}