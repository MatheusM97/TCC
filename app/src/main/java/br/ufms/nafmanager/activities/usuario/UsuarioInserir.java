package br.ufms.nafmanager.activities.usuario;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.adapters.MaskEditUtil;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UsuarioInserir extends CustomActivity {

    private EditText usuarioCpf;
    private EditText usuarioSenha;
    private EditText usuarioSenha2;
    private EditText usuarioNome;
    private EditText usuarioTelefone;
    private EditText usuarioEmail;
    private TextView tvSenha;
    private TextView tvSenha2;
    private TextView tvUniversidade;
    private Spinner spinnerUnv;
    private Button btnCadastrarUsuario;
    private List<Universidade> universidadeLista;
    private Usuario usuario;
    private boolean edicao = false;
    private boolean auto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_inserir);

        this.edicao = false;
        if(getIntent().getSerializableExtra("usuario") != null){
            this.usuario = (Usuario) getIntent().getSerializableExtra("usuario");
            this.edicao = true;
        }

        if(Persistencia.getInstance().getUsuarioAtual() == null || Persistencia.getInstance().getUsuarioAtual().equals(new Usuario())){
            auto = true;
        }

        vincularComponentes();

        if(edicao && usuario != null){
            carregarTela();
        }

        controlaAcesso();
    }

    private void controlaAcesso() {
        if(!auto && !edicao){
            tvSenha.setVisibility(View.INVISIBLE);
            tvSenha2.setVisibility(View.INVISIBLE);
            usuarioSenha.setVisibility(View.INVISIBLE);
            usuarioSenha2.setVisibility(View.INVISIBLE);
        }
    }

    private void vincularComponentes() {
        this.usuarioCpf = (EditText) findViewById(R.id.et_usuarioCpf);
        this.tvSenha =  findViewById(R.id.tv_usuarioSenha);
        this.tvSenha2 = findViewById(R.id.tv_usuarioSenha2);
        this.usuarioSenha = (EditText) findViewById(R.id.et_usuarioSenha);
        this.usuarioSenha2 = (EditText) findViewById(R.id.et_usuarioSenha2);
        this.usuarioNome = (EditText) findViewById(R.id.et_usuarioNome);
        this.usuarioEmail = (EditText) findViewById(R.id.et_usuarioEmail);
        this.usuarioTelefone = (EditText) findViewById(R.id.et_usuarioTelefone);
        this.btnCadastrarUsuario = (Button) findViewById(R.id.btn_criarUsuario);
        this.usuarioCpf.addTextChangedListener(MaskEditUtil.mask(usuarioCpf, MaskEditUtil.FORMAT_CPF));
        this.usuarioTelefone.addTextChangedListener(MaskEditUtil.mask(usuarioTelefone, MaskEditUtil.FORMAT_FONE));

        this.btnCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preValidar();
            }
        });

        if(edicao) {
            this.usuarioCpf.setEnabled(false);
        }
    }

    private void carregarTela() {
        this.usuarioCpf.setText(usuario.getCpf());
        this.usuarioSenha.setText(usuario.getSenha());
        this.usuarioSenha2.setText(usuario.getSenha());
        this.usuarioNome.setText(usuario.getNome());
        this.usuarioTelefone.setText(usuario.getTelefone());
        this.usuarioEmail.setText(usuario.getEmail());
    }

    private void copiarTela(){
        if (!edicao && (usuario == null || usuario.getId() == null)) {
            usuario = new Usuario();
        }
        if (this.usuarioCpf.getText() != null && this.usuarioCpf.getText().length() > 0) {
            usuario.setCpf(this.usuarioCpf.getText().toString());
        }

        if (this.usuarioSenha.getText() != null && this.usuarioSenha.getText().length() > 0) {
            usuario.setSenha(this.usuarioSenha.getText().toString());
        }

        if(this.usuarioSenha2.getText() != null && this.usuarioSenha2.getText().length() > 0){
            usuario.setSenha2(this.usuarioSenha2.getText().toString());
        }

        if (this.usuarioNome.getText() != null && this.usuarioNome.getText().length() > 0) {
            usuario.setNome(this.usuarioNome.getText().toString());
        }

        if (this.usuarioEmail.getText() != null && this.usuarioEmail.getText().length() > 0) {
            usuario.setEmail(this.usuarioEmail.getText().toString());
        }

        if (this.usuarioTelefone.getText() != null && this.usuarioTelefone.getText().length() > 0) {
            usuario.setTelefone(this.usuarioTelefone.getText().toString());
        }

        if(!auto && !edicao) {
            usuario.setSenha(usuario.getCpfSomenteDigitos());
            usuario.setSenha2(usuario.getCpfSomenteDigitos());
        }
    }

    public void preValidar() {
        this.copiarTela();
        this.usuario.validarSenhas();


        if(usuario.getMensagem() != null && usuario.getMensagem().trim().length() > 0){
            inserir();
        }

        if(!edicao){
            validarJaInserido();
        }
        else{
            inserir();
        }
    }

    private void inserir(){
        if(usuario.getMensagem() == null || usuario.getMensagem().length() <= 0){
            if(usuario.salvar()){
                Persistencia.getInstance().setUsuarioCarregado(usuario);
                finish();
            }
        }

        Toast.makeText(this, usuario.getMensagem(), Toast.LENGTH_SHORT).show();
    }

    private void validarJaInserido() {
        showDialog();
        Persistencia.getInstance().verificaUsuarioCadastrado(usuario);
        verificaUsuario();
    }

    public void verificaUsuario(){
        if(Persistencia.getInstance().isPesquisouUsuarioJahCadastrado()){
            if(Persistencia.getInstance().getUsuarioVerificado() != null && Persistencia.getInstance().getUsuarioVerificado().getId() != null){
                usuario.setMensagem(Persistencia.getInstance().getUsuarioVerificado().getMensagem());
            }

            Persistencia.getInstance().setPesquisouUsuarioJahCadastrado(false);
            Persistencia.getInstance().setUsuarioVerificado(null);

            hideDialog();
            inserir();
            return;
        }
        else
            aguardaResolucao();
    }
    private void aguardaResolucao() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verificaUsuario();
            }
        },100);
    }
}
