package br.ufms.nafmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UsuarioActivity extends AppCompatActivity {

    private EditText usuarioCpf;
    private EditText usuarioSenha;
    private EditText usuarioNome;
    private EditText usuarioTelefone;
    private EditText usuarioEmail;
    private Button btnCadastrarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_inserir);
        this.usuarioCpf = (EditText)findViewById(R.id.et_usuarioCpf);
        this.usuarioSenha = (EditText)findViewById(R.id.et_usuarioSenha);
        this.usuarioNome = (EditText)findViewById(R.id.et_usuarioNome);
        this.usuarioEmail = (EditText)findViewById(R.id.et_usuarioEmail);
        this.usuarioTelefone = (EditText)findViewById(R.id.et_usuarioTelefone);
        this.btnCadastrarUsuario = (Button)findViewById(R.id.btn_criarUsuario);
        this.btnCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirUsuario();
            }
        });
        }

    public void inserirUsuario(){
        Usuario usuario = new Usuario();
        usuario.setCpf(this.usuarioCpf.getText().toString());
        usuario.setSenha(this.usuarioSenha.getText().toString());
        usuario.setNome(this.usuarioNome.getText().toString());
        usuario.setEmail(this.usuarioEmail.getText().toString());
        usuario.setTelefone(this.usuarioTelefone.getText().toString());

        Persistencia.getInstance().persistirObjeto(usuario);
        if(usuario.getId() != null && usuario.getId().length() > 0){
            Toast.makeText(this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else{
            Toast.makeText(this, "Não foi possível cadastrar!", Toast.LENGTH_SHORT).show();
        }
    }
}
