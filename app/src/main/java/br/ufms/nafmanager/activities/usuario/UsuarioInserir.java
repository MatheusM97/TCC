package br.ufms.nafmanager.activities.usuario;

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
import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UsuarioInserir extends AppCompatActivity {

    private EditText usuarioCpf;
    private EditText usuarioSenha;
    private EditText usuarioSenha2;
    private EditText usuarioNome;
    private EditText usuarioTelefone;
    private EditText usuarioEmail;
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

        if (getIntent().getSerializableExtra("usuario") != null) {
            this.usuario = (Usuario) getIntent().getSerializableExtra("usuario");
            this.edicao = true;
        }

        if(getParent().getClass().getName().equals("")){
            auto = true;
        }

        this.usuarioCpf = (EditText) findViewById(R.id.et_usuarioCpf);
        this.usuarioSenha = (EditText) findViewById(R.id.et_usuarioSenha);
        this.usuarioSenha2 = (EditText) findViewById(R.id.et_usuarioSenha2);
        this.usuarioNome = (EditText) findViewById(R.id.et_usuarioNome);
        this.usuarioEmail = (EditText) findViewById(R.id.et_usuarioEmail);
        this.usuarioTelefone = (EditText) findViewById(R.id.et_usuarioTelefone);
        this.btnCadastrarUsuario = (Button) findViewById(R.id.btn_criarUsuario);
        this.usuarioCpf.addTextChangedListener(MaskEditUtil.mask(usuarioCpf, MaskEditUtil.FORMAT_CPF));
        this.usuarioTelefone.addTextChangedListener(MaskEditUtil.mask(usuarioTelefone, MaskEditUtil.FORMAT_FONE));

        this.spinnerUnv = (Spinner) findViewById(R.id.sp_usuario_universidade);
        this.universidadeLista = new ArrayList<Universidade>();
        this.universidadeLista.add(new Universidade("", "Selecione"));
        for (Universidade unv : Persistencia.getInstance().getUniversidades()) {
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

        if (edicao && usuario != null) {
            carregarTela();
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

    public void inserirUsuario() {

        if (!edicao && usuario.getId() == null && usuario.getId().length() == 0) {
            usuario = new Usuario();
        }
        if (this.usuarioCpf.getText() != null && this.usuarioCpf.getText().length() > 0) {
            usuario.setCpf(this.usuarioCpf.getText().toString());
        }

        if (this.usuarioSenha.getText() != null && this.usuarioSenha.getText().length() > 0) {
            usuario.setSenha(this.usuarioSenha.getText().toString());
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

        if (validar(usuario)) {
            Persistencia.getInstance().persistirObjeto(usuario);
            if (usuario.getId() != null && usuario.getId().length() > 0) {
                if (spinnerUnv.getSelectedItem() != null && spinnerUnv.getSelectedItem().toString().length() > 0
                        && !((Universidade) spinnerUnv.getSelectedItem()).getId().isEmpty()) {
                    Universidade univ;
                    univ = (Universidade) spinnerUnv.getSelectedItem();
                    Acesso acesso = new Acesso(usuario.getId(), usuario.getNome(), univ.getId(), univ.getNome());
                    acesso.setUnidadeId(univ.getUnidadeId());
                    acesso.setParticipante(true);

                    if(auto)
                        acesso.setStatus(StatusEnum.RASCUNHO);

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

    private boolean validar(Usuario usuario) {

        if (usuario.getCpf() == null || usuario.getCpf().length() <= 0) {
            Toast.makeText(this, "É necessário informar o CPF!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (usuario.getSenha() == null || usuario.getSenha().length() <= 0) {
            Toast.makeText(this, "É necessário informar a senha!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(this.usuarioSenha2 == null || this.usuarioSenha2.getText().length() <= 0){
            Toast.makeText(this, "É necessário confirmar a senha!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!this.usuarioSenha2.getText().toString().equals(usuario.getSenha())){
            Toast.makeText(this, "As senhas não são iguais!", Toast.LENGTH_SHORT).show();
        }

        if (usuario.getNome() == null || usuario.getNome().length() <= 0) {
            Toast.makeText(this, "É necessário informar o nome!", Toast.LENGTH_SHORT);
            return false;
        }

        if(!isValidCPF(usuario.getCpf())){
            Toast.makeText(this, "CPF inválido!", Toast.LENGTH_SHORT).show();
            return false;
        }

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
}