package br.ufms.nafmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.adapters.MaskEditUtil;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UsuarioInserir extends AppCompatActivity {

    private EditText usuarioCpf;
    private EditText usuarioSenha;
    private EditText usuarioNome;
    private EditText usuarioTelefone;
    private EditText usuarioEmail;
    private Spinner spinnerUnv;
    private Button btnCadastrarUsuario;
    private List<Universidade> universidadeLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_inserir);
        this.usuarioCpf = (EditText) findViewById(R.id.et_usuarioCpf);
        this.usuarioSenha = (EditText) findViewById(R.id.et_usuarioSenha);
        this.usuarioNome = (EditText) findViewById(R.id.et_usuarioNome);
        this.usuarioEmail = (EditText) findViewById(R.id.et_usuarioEmail);
        this.usuarioTelefone = (EditText) findViewById(R.id.et_usuarioTelefone);
        this.btnCadastrarUsuario = (Button) findViewById(R.id.btn_criarUsuario);
        this.usuarioCpf.addTextChangedListener(MaskEditUtil.mask(usuarioCpf, MaskEditUtil.FORMAT_CPF));
        this.usuarioTelefone.addTextChangedListener(MaskEditUtil.mask(usuarioTelefone, MaskEditUtil.FORMAT_FONE));

        this.spinnerUnv = (Spinner) findViewById(R.id.sp_usuario_universidade);
        this.universidadeLista = new ArrayList<Universidade>();
        this.universidadeLista.add(new Universidade("","Selecione"));
        for(Universidade unv : Persistencia.getInstance().getUniversidades()){
            this.universidadeLista.add(unv);
        }

        ArrayAdapter<Universidade> adapter = new ArrayAdapter<Universidade>(this, android.R.layout.simple_spinner_dropdown_item, universidadeLista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerUnv.setAdapter(adapter);

        this.btnCadastrarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirUsuario();
            }
        });
    }


    public void inserirUsuario() {
        Usuario usuario = new Usuario();
        usuario.setCpf(this.usuarioCpf.getText().toString());
        usuario.setSenha(this.usuarioSenha.getText().toString());
        usuario.setNome(this.usuarioNome.getText().toString());
        usuario.setEmail(this.usuarioEmail.getText().toString());
        usuario.setTelefone(this.usuarioTelefone.getText().toString());

        Persistencia.getInstance().persistirObjeto(usuario);
        if (usuario.getId() != null && usuario.getId().length() > 0) {
            if (spinnerUnv.getSelectedItem() != null && spinnerUnv.getSelectedItem().toString().length() > 0
                    && !((Universidade) spinnerUnv.getSelectedItem()).getId().isEmpty()) {
                Universidade univ;
                univ = (Universidade) spinnerUnv.getSelectedItem();
                Acesso acesso = new Acesso(usuario.getId(), usuario.getNome(), univ.getId(), univ.getNome());
                acesso.setUnidadeId(univ.getUnidadeId());
                acesso.setUnidadeNome(univ.getUnidadeNome());
                acesso.setParticipante(true);
                acesso.setStatus("rascunho");
                Persistencia.getInstance().persistirObjeto(acesso);
                if (acesso.getId() != null && acesso.getId().length() > 0) {
                    Toast.makeText(this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Usuário salvo, porém acesso não pode ser definido", Toast.LENGTH_LONG);
            }
            this.finish();
        } else {
            Toast.makeText(this, "Não foi possível cadastrar!", Toast.LENGTH_SHORT).show();
        }
    }
}
