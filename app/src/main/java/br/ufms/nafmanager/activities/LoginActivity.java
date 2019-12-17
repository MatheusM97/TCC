package br.ufms.nafmanager.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.adapters.MaskEditUtil;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class LoginActivity extends AppCompatActivity {

    private EditText login;
    private EditText senha;
    private Button btn_login;
    private Button btn_registrar;
    private Usuario usuarioLogado;
    private ProgressDialog progressDialog;
    private List<Acesso> acList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Persistencia.getInstance().Iniciar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        this.login = (EditText) findViewById(R.id.et_login);
        this.senha = (EditText) findViewById(R.id.et_senha);
        this.btn_login = (Button) findViewById(R.id.btn_login);
        this.btn_registrar = (Button) findViewById(R.id.btn_registrar_se);

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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if (Persistencia.getInstance().getVersao() > 0) {
            this.usuarioLogado = new Usuario();
            if (login.getText() != null && login.getText().toString().length() > 0 &&
                    senha.getText() != null && senha.getText().toString().length() > 0) {
                Persistencia.getInstance().getAutenticar(login.getText().toString(), senha.getText().toString());
                showDialog();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        usuarioLogado = Persistencia.getInstance().getUsuarioLogado();
                        progressDialog.dismiss();

                        if (usuarioLogado != null && usuarioLogado.getId() != null && usuarioLogado.getId().length() > 0) {

                            login.setText("");
                            senha.setText("");

                            acList = Persistencia.getInstance().getAcessos();

                            controlaAcessos();

                        } else
                            Toast.makeText(LoginActivity.this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                    }
                }, 6000);
            } else {
                Toast.makeText(this, "É necessário informar o CPF e a senha", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent novaIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(novaIntent);
        }
    }

    private void controlaAcessos() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(LoginActivity.this);

        builderSingle.setTitle("Select Item");
        ArrayAdapter<Acesso> arrayAdapter = new ArrayAdapter<Acesso>(this, android.R.layout.simple_list_item_1, acList);

        builderSingle.setPositiveButton("Selecionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent novaIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(novaIntent);
            }
        });

        builderSingle.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        builderSingle.show();

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

    }

    public void showDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setContentView(R.layout.layout_carregando);
        this.progressDialog.show();
    }
}
