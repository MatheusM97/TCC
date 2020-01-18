package br.ufms.nafmanager.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.usuario.UsuarioInserir;
import br.ufms.nafmanager.adapters.MaskEditUtil;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class LoginActivity extends CustomActivity {

    private EditText login;
    private EditText senha;
    private Button btn_login;
    private Button btn_registrar;
    private Usuario usuarioAtual;
    private List<Acesso> acList;
    private int timeout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        this.login = (EditText) findViewById(R.id.et_login);
        this.senha = (EditText) findViewById(R.id.et_senha);
        this.btn_login = (Button) findViewById(R.id.btn_login);
        this.btn_registrar = (Button) findViewById(R.id.btn_registrar_se);

        carregaComponentes();
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

        if(timeout >= 30) {
            hideDialog();
            Toast.makeText(this, "Tempo esgotado para conexão com o servidor!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(usuarioAtual.temId()) {
            hideDialog();
            iniciar();
        }
        else
            validarLogin();
    }

    private void iniciar() {
        if (usuarioAtual.validarStatus()) {
            Intent novaIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(novaIntent);    
        }
        else
            Toast.makeText(this, usuarioAtual.getMensagem(), Toast.LENGTH_SHORT).show();
    }

//    private void controlaAcessos() {
//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LoginActivity.this);
//
//        builderSingle.setTitle("Select Item");
//        ArrayAdapter<Acesso> arrayAdapter = new ArrayAdapter<Acesso>(this, android.R.layout.simple_list_item_1, acList);
//
//        builderSingle.setPositiveButton("Selecionar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent novaIntent = new Intent(getBaseContext(), MainActivity.class);
//                startActivity(novaIntent);
//            }
//        });
//
//        builderSingle.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                dialog.dismiss();
//            }
//        });
//
//        builderSingle.show();

//                Intent novaIntent = new Intent(getBaseContext(), MainActivity.class);
//                novaIntent.putExtra("usuarioNome", usuarioLogado.getNome());
//                novaIntent.putExtra("usuarioId", usuarioLogado.getId());
//
//                Acesso ac = acList.get(0);
//                if (ac.getUnidadeId() != null && !ac.getUnidadeId().isEmpty())
//                    novaIntent.putExtra("universidadeId", ac.getUniversidadeId());
//                if (ac.getUniversidadeNome() != null && !ac.getUniversidadeNome().isEmpty())
//                    novaIntent.putExtra("universidadeNome", ac.getUniversidadeNome());
//                if (ac.getUnidadeId() != null && !ac.getUnidadeId().isEmpty())
//                    novaIntent.putExtra("unidadeId", ac.getUnidadeId());
//                if (ac.getUnidadeNome() != null && !ac.getUnidadeNome().isEmpty())
//                    novaIntent.putExtra("unidadeNome", ac.getUnidadeNome());
//
//                Persistencia.getInstance().carregaAcesso(ac.getId());
//                startActivity(novaIntent);
//
//    }
//
    protected ProgressDialog progressDialog;

    protected void showDialog() {
        this.progressDialog = new ProgressDialog(LoginActivity.this);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setContentView(R.layout.layout_carregando);
        this.progressDialog.show();
    }

    protected void hideDialog(){
        this.progressDialog.dismiss();
    }

}