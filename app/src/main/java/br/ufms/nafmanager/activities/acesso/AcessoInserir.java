package br.ufms.nafmanager.activities.acesso;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoInserir extends AppCompatActivity {

    private Button btn_inserirAcesso;
    private Spinner spnUsuario;
    private Spinner spnUniversidade;
    private Spinner spnUnidade;
    private List<Usuario> usuarioLista;
    private List<Universidade> universidadeLista;
    private List<Unidade> unidadeLista;
    private CheckBox cbParticipante;
    private CheckBox cbSupervisor;
    private CheckBox cbCoordenador;
    private CheckBox cbRepresentante;
    private TextView tvUnidade;
    private TextView tvUniversidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_inserir);

        spnUsuario = (Spinner) findViewById(R.id.sp_acesso_usuario);
        spnUniversidade = (Spinner) findViewById(R.id.sp_acesso_universidade);
        cbParticipante = (CheckBox) findViewById(R.id.cb_acesso_participante);
        cbSupervisor = (CheckBox) findViewById(R.id.cb_acesso_supervisor);
        cbCoordenador = (CheckBox) findViewById(R.id.cb_acesso_coordenador);
        cbRepresentante = (CheckBox) findViewById(R.id.cb_acesso_representante);
        tvUnidade = (TextView) findViewById(R.id.tv_acesso_unidade);
        tvUniversidade = (TextView) findViewById(R.id.tv_acesso_universidade);

        universidadeLista = new ArrayList<Universidade>();
        universidadeLista.add(new Universidade("", "Selecione"));
        List<Universidade> unvBanco =  Persistencia.getInstance().getUniversidades();
        for (Universidade unv : unvBanco) {
            this.universidadeLista.add(unv);
        }

        ArrayAdapter<Universidade> adapter = new ArrayAdapter<Universidade>(this, android.R.layout.simple_spinner_dropdown_item, universidadeLista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUniversidade.setAdapter(adapter);

        usuarioLista = new ArrayList<Usuario>();
        usuarioLista.add(new Usuario("", "Selecione"));
        List<Usuario> usrBanco =  Persistencia.getInstance().getUsuarios();
        for (Usuario uns :usrBanco) {
            this.usuarioLista.add(uns);
        }

        ArrayAdapter<Usuario> usAdapter = new ArrayAdapter<Usuario>(this, android.R.layout.simple_spinner_dropdown_item, usuarioLista);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUsuario.setAdapter(usAdapter);

        cbRepresentante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbRepresentante.isChecked()) {
                    spnUnidade.setVisibility(View.VISIBLE);
                    tvUnidade.setVisibility(View.VISIBLE);
                    spnUniversidade.setVisibility(View.INVISIBLE);
                    tvUniversidade.setVisibility(View.INVISIBLE);
                    spnUniversidade.setSelection(0);
                } else {
                    spnUniversidade.setVisibility(View.VISIBLE);
                    tvUniversidade.setVisibility(View.VISIBLE);
                    spnUnidade.setVisibility(View.INVISIBLE);
                    tvUnidade.setVisibility(View.INVISIBLE);
                    spnUnidade.setSelection(0);
                }
            }
        });

        unidadeLista = new ArrayList<Unidade>();
        unidadeLista.add(new Unidade("", "Selecione"));
        for (Unidade uns : Persistencia.getInstance().getUnidades()) {
            this.unidadeLista.add(uns);
        }

        spnUnidade = (Spinner) findViewById(R.id.sp_acesso_unidade);
        ArrayAdapter<Unidade> undAdp = new ArrayAdapter<Unidade>(this, android.R.layout.simple_spinner_dropdown_item, unidadeLista);
        undAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUnidade.setAdapter(undAdp);

        btn_inserirAcesso = (Button) findViewById(R.id.btn_acesso_inserir);
        btn_inserirAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Acesso acesso = new Acesso();

                    if (spnUsuario.getSelectedItem() != null && ((Usuario) spnUsuario.getSelectedItem()).getId() != null) {
                        Usuario usr = (Usuario) spnUsuario.getSelectedItem();
                        acesso.setUsuarioId(usr.getId());
                    }

                    if (spnUniversidade.getSelectedItem() != null && ((Universidade) spnUniversidade.getSelectedItem()).getId() != null) {
                        Universidade uni = (Universidade) spnUniversidade.getSelectedItem();
                        acesso.setUniversidadeId(uni.getId());
                        acesso.setUnidadeId(uni.getUnidadeId());
                    }

                    if(spnUnidade.getSelectedItem() != null){
                        Unidade unid = (Unidade) spnUnidade.getSelectedItem();
                        if(unid.getId() != null && unid.getId().length() > 0){
                            acesso.setUnidadeId(unid.getId());
                        }
                    }

                    acesso.setParticipante(cbParticipante.isChecked());
                    acesso.setSupervisor(cbSupervisor.isChecked());
                    acesso.setCoordenador(cbCoordenador.isChecked());
                    acesso.setRepresentante(cbRepresentante.isChecked());

                    if (validar(acesso)) {
                        Persistencia.getInstance().persistirObjeto(acesso);
                        if (acesso.getId() != null && acesso.getId().length() > 0) {
                            Toast.makeText(AcessoInserir.this, "Acesso salvo com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AcessoInserir.this, "Falha ao salvar o acesso!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(AcessoInserir.this, "Falha ao salvar o acesso!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        spnUniversidade.setVisibility(View.VISIBLE);
        tvUniversidade.setVisibility(View.VISIBLE);
        spnUnidade.setVisibility(View.INVISIBLE);
        tvUnidade.setVisibility(View.INVISIBLE);
    }

    private boolean validar(Acesso acesso) {
        if (acesso.getUsuarioId() == null || acesso.getUsuarioId().length() <= 0) {
            Toast.makeText(this, "É necessário informar o usuário", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((acesso.isCoordenador() || acesso.isParticipante() || acesso.isSupervisor() )&& (acesso.getUniversidadeId() == null || acesso.getUniversidadeId().length() <= 0)) {
            Toast.makeText(this, "É necessário informar a universidade", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (acesso.isRepresentante() && (acesso.getUnidadeId() == null || acesso.getUnidadeId().length() <= 0)) {
            Toast.makeText(this, "É necessário informar a unidade", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!acesso.isRepresentante() && !acesso.isSupervisor() && !acesso.isParticipante() && !acesso.isCoordenador()) {
            Toast.makeText(this, "É necessário informar um acesso", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (acesso.isRepresentante() && (acesso.isCoordenador() || acesso.isParticipante() || acesso.isSupervisor())) {
            Toast.makeText(this, "O acesso de Representante não pode ser somado aos demais", Toast.LENGTH_LONG).show();
        }
        return true;
    }
}
