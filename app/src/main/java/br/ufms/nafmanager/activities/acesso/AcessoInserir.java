package br.ufms.nafmanager.activities.acesso;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.AcessoTipoEnum;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class AcessoInserir extends CustomActivity {

    private Button btn_inserirAcesso;
    private Button btn_aprovarAcesso;
    private ArrayList<AcessoTipoEnum> acessoTipo = new ArrayList<>();
    private EditText etUsuario;
    private Spinner spnTipo;
    private Spinner spnUniversidade;
    private Spinner spnUnidade;
    private Spinner spnRegiao;
    private List<Usuario> usuarioLista;
    private List<Universidade> universidadeLista;
    private List<Unidade> unidadeLista;
    private List<Regiao> regiaoLista;
    private CheckBox cbAluno;
    private CheckBox cbProfessor;
    private CheckBox cbRepresentanteUniversidade;
    private CheckBox cbRepresentante;
    private CheckBox cbModerador;
    private TextView tvUnidade;
    private TextView tvUniversidade;
    private TextView tvRegiao;
    private boolean edicao = false;
    private ArrayList<Usuario> usuarios;
    private ArrayAdapter<AcessoTipoEnum> acessoAdp;
    private ArrayAdapter<Universidade> universidadeAdp;
    private ArrayAdapter<Unidade> unidadeAdp;
    private ArrayAdapter<Regiao> regiaoAdp;
    private Acesso acesso;
    private Usuario usuario;
    private boolean solicitando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_inserir);

        this.edicao = false;
        this.solicitando = false;
        if (getIntent().getSerializableExtra("acesso") != null) {
            this.acesso = (Acesso) getIntent().getSerializableExtra("acesso");
            this.edicao = true;

            if (acesso.isSolicitando()) {
                this.solicitando = true;
            }
        }

        vincularComponentes();

        controlaAcesso();

        if (edicao && acesso != null) {
            carregarTela();
        }

        btn_aprovarAcesso.setVisibility(View.INVISIBLE);

        if (solicitando) {
            etUsuario.setEnabled(false);
            spnTipo.setEnabled(false);
            spnRegiao.setEnabled(false);
            spnUnidade.setEnabled(false);
            spnUniversidade.setEnabled(false);

            cbAluno.setEnabled(false);
            cbProfessor.setEnabled(false);
            cbRepresentanteUniversidade.setEnabled(false);
            cbRepresentante.setEnabled(false);
            cbModerador.setEnabled(false);

            btn_inserirAcesso.setVisibility(View.INVISIBLE);
            btn_aprovarAcesso.setVisibility(View.VISIBLE);
        }
    }

    private void aprovar() {
        edicao = false;
        acesso.setStatus(StatusEnum.ATIVO);
        validar();
    }


    private void validarAcesso() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                validouDuplicidade();
            }
        }, 500);
    }

    private void validouDuplicidade() {
        if (Persistencia.getInstance().isPesquisouAcessoJahCadastrado()) {
            if (!Persistencia.getInstance().isPodeGravarAcesso()) {
                Toast.makeText(this, "Acesso já cadastrado!", Toast.LENGTH_SHORT).show();
                hideDialog();
            } 
            else{
                inserir();
            }
        } else
            validarAcesso();
    }

    private void validar() {
        copiarTela();
        if(acesso.getMensagem() != null && acesso.getMensagem().length() >0){
            Toast.makeText(this, acesso.getMensagem(), Toast.LENGTH_SHORT).show();
        }
        else{
            showDialog();
            if (edicao) {
                Persistencia.getInstance().setPesquisouAcessoJahCadastrado(true);
                Persistencia.getInstance().setPodeGravarAcesso(true);
            } else {
                acesso.validarDuplicidade();
            }

            validarAcesso();
        }
    }

    private void inserir() {
        if (acesso.salvar()) {
            Persistencia.getInstance().setAcessoCarregado(acesso);
            hideDialog();

            if(solicitando){
                Toast.makeText(this, "Acesso aprovado com sucesso", Toast.LENGTH_SHORT).show();
            }
            finish();
        }

        Toast.makeText(this, acesso.getMensagem(), Toast.LENGTH_SHORT).show();
    }

    private void copiarTela() {
        if (!edicao && (acesso == null || acesso.getId() == null)) {
            acesso = new Acesso();
        }

        acesso.setEdicao(edicao);

        if (usuario.getId() != null && usuario.getId().length() > 0)
            acesso.setUsuarioId(usuario.getId());

        if (spnTipo.getSelectedItem() != null && spnTipo.getSelectedItem().toString().length() > 0)
            acesso.setTipo(AcessoTipoEnum.getEnumByLabel(spnTipo.getSelectedItem().toString()));

        if (spnRegiao.getSelectedItem() != null && ((Regiao) spnRegiao.getSelectedItem()).getId().length() > 0) {
            Regiao regiao = (Regiao) spnRegiao.getSelectedItem();
            acesso.setRegiaoId(regiao.getId());
        }

        if (spnUnidade.getSelectedItem() != null && ((Unidade) spnUnidade.getSelectedItem()).getId().length() > 0) {
            Unidade unidade = (Unidade) spnUnidade.getSelectedItem();
            acesso.setUnidadeId(unidade.getId());
        }

        if (spnUniversidade.getSelectedItem() != null && ((Universidade) spnUniversidade.getSelectedItem()).getId().length() > 0) {
            Universidade universidade = (Universidade) spnUniversidade.getSelectedItem();
            acesso.setUniversidadeId(universidade.getId());
        }

        acesso.setModerador(false);
        acesso.setRepresentante(false);
        acesso.setProfessor(false);
        acesso.setAluno(false);

        if (acesso.getTipoValor().equals(AcessoTipoEnum.MODERADOR.getValor())) {
            if (cbModerador.isChecked())
                acesso.setModerador(true);
        }

        if (acesso.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())) {
            if (cbRepresentante.isChecked())
                acesso.setRepresentante(true);
        }

        if (acesso.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor())) {
            if (cbRepresentante.isChecked())
                acesso.setRepresentante(true);
        }

        if (acesso.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor())) {
            if (cbRepresentanteUniversidade.isChecked())
                acesso.setRepresentante(true);

            if (cbProfessor.isChecked())
                acesso.setProfessor(true);

            if (cbAluno.isChecked())
                acesso.setAluno(true);
        }

        acesso.validar();
    }

    private void controlaTipoAcesso() {
        if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.UNIDADE.toString())) {
            tvUniversidade.setVisibility(View.INVISIBLE);
            spnUniversidade.setVisibility(View.INVISIBLE);
            spnUniversidade.setSelection(0);

            spnRegiao.setVisibility(View.INVISIBLE);
            tvRegiao.setVisibility(View.INVISIBLE);
            spnRegiao.setSelection(0);

            tvUnidade.setVisibility(View.VISIBLE);
            spnUnidade.setVisibility(View.VISIBLE);
        } else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.UNIVERSIDADE.toString())) {
            tvUnidade.setVisibility(View.INVISIBLE);
            spnUnidade.setVisibility(View.INVISIBLE);
            spnUnidade.setSelection(0);

            spnRegiao.setVisibility(View.INVISIBLE);
            tvRegiao.setVisibility(View.INVISIBLE);
            spnRegiao.setSelection(0);

            tvUniversidade.setVisibility(View.VISIBLE);
            spnUniversidade.setVisibility(View.VISIBLE);
        } else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.REGIAO.toString())) {
            tvUniversidade.setVisibility(View.INVISIBLE);
            spnUniversidade.setVisibility(View.INVISIBLE);
            spnUniversidade.setSelection(0);

            tvUnidade.setVisibility(View.INVISIBLE);
            spnUnidade.setVisibility(View.INVISIBLE);
            spnUnidade.setSelection(0);

            spnRegiao.setVisibility(View.VISIBLE);
            tvRegiao.setVisibility(View.VISIBLE);
        } else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.MODERADOR.toString())) {
            tvUniversidade.setVisibility(View.INVISIBLE);
            spnUniversidade.setVisibility(View.INVISIBLE);
            spnUniversidade.setSelection(0);

            tvUnidade.setVisibility(View.INVISIBLE);
            spnUnidade.setVisibility(View.INVISIBLE);
            spnUnidade.setSelection(0);

            spnRegiao.setVisibility(View.INVISIBLE);
            tvRegiao.setVisibility(View.INVISIBLE);
            spnRegiao.setSelection(0);
        }

        controlaAcesso();
    }

    private void controlaAcesso() {
        cbAluno.setVisibility(View.INVISIBLE);
        cbProfessor.setVisibility(View.INVISIBLE);
        cbRepresentante.setVisibility(View.INVISIBLE);
        cbRepresentanteUniversidade.setVisibility(View.INVISIBLE);
        cbModerador.setVisibility(View.INVISIBLE);

        Acesso ac = Persistencia.getInstance().getAcessoAtual();

        if (spnTipo.getSelectedItem().toString() != null) {

            if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.MODERADOR.getLabel())) {
                if (ac.getNivelAcesso() == 7L) {
                    cbModerador.setVisibility(View.VISIBLE);
                }
            } else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.REGIAO.getLabel())) {
                if (ac.getNivelAcesso() >= 6L) {
                    cbRepresentante.setVisibility(View.VISIBLE);
                }
            } else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.UNIDADE.getLabel())) {
                if (ac.getNivelAcesso() >= 5L) {
                    cbRepresentante.setVisibility(View.VISIBLE);
                }
            } else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.UNIVERSIDADE.getLabel())) {
                if (ac.getNivelAcesso() >= 4L) {
                    cbRepresentanteUniversidade.setVisibility(View.VISIBLE);
                }

                if (ac.getNivelAcesso() >= 2L) {
                    cbProfessor.setVisibility(View.VISIBLE);
                }

                if (ac.getNivelAcesso() >= 1L) {
                    cbAluno.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void vincularComponentes() {
        usuario = new Usuario();

        etUsuario = findViewById(R.id.sp_acesso_usuario);
        spnUniversidade = findViewById(R.id.sp_acesso_universidade);
        spnUnidade = findViewById(R.id.sp_acesso_unidade);
        spnRegiao = findViewById(R.id.sp_acesso_regiao);

        tvUnidade = findViewById(R.id.tv_acesso_unidade);
        tvUniversidade = findViewById(R.id.tv_acesso_universidade);
        tvRegiao = findViewById(R.id.tv_acesso_regiao);

        cbAluno = findViewById(R.id.cb_acesso_participante);
        cbAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAluno.isChecked()) {
                    cbRepresentanteUniversidade.setChecked(false);
                    cbProfessor.setChecked(false);
                }
            }
        });

        cbProfessor = findViewById(R.id.cb_acesso_professor);
        cbProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbProfessor.isChecked()) {
                    cbAluno.setChecked(false);
                    cbRepresentanteUniversidade.setChecked(false);
                }
            }
        });

        cbRepresentanteUniversidade = findViewById(R.id.cb_acesso_representanteUniversidade);
        cbRepresentanteUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbAluno.setChecked(false);
                cbProfessor.setChecked(false);
            }
        });

        cbRepresentante = findViewById(R.id.cb_acesso_representante);

        cbModerador = findViewById(R.id.cb_acesso_master);

        btn_inserirAcesso = findViewById(R.id.btn_acesso_inserir);

        btn_aprovarAcesso = findViewById(R.id.btn_acesso_aprovar);

        etUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleSearchDialogCompat(AcessoInserir.this, "Usuários", "Digite aqui...", null, getUsuarios(),
                        new SearchResultListener<Usuario>() {
                            @Override
                            public void onSelected(BaseSearchDialogCompat dialog,
                                                   Usuario item, int position) {
                                etUsuario.setText(item.getTitle());
                                usuario = item;
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        this.spnTipo = findViewById(R.id.sp_acessoTipo);
        acessoTipo = new ArrayList<>();
        acessoTipo.add(AcessoTipoEnum.UNIVERSIDADE);
        acessoTipo.add(AcessoTipoEnum.UNIDADE);
        acessoTipo.add(AcessoTipoEnum.REGIAO);
        acessoTipo.add(AcessoTipoEnum.MODERADOR);

        acessoAdp = new ArrayAdapter<AcessoTipoEnum>(this, android.R.layout.simple_spinner_dropdown_item, acessoTipo);
        this.spnTipo.setAdapter(acessoAdp);
        this.spnTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                controlaTipoAcesso();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        universidadeLista = new ArrayList<>();
        universidadeLista.add(new Universidade("", "Selecione"));
        for (Universidade unv : getUniversidadeAtualizada()) {
            this.universidadeLista.add(unv);
        }

        universidadeAdp = new ArrayAdapter<Universidade>(this, android.R.layout.simple_spinner_dropdown_item, universidadeLista);
        universidadeAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUniversidade.setAdapter(universidadeAdp);

        unidadeLista = new ArrayList<Unidade>();
        unidadeLista.add(new Unidade("", "Selecione"));
        for (Unidade uns : getUnidadeAtualizada()) {
            this.unidadeLista.add(uns);
        }

        unidadeAdp = new ArrayAdapter<Unidade>(this, android.R.layout.simple_spinner_dropdown_item, unidadeLista);
        unidadeAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUnidade.setAdapter(unidadeAdp);

        regiaoLista = new ArrayList<Regiao>();
        regiaoLista.add(new Regiao("", "Selecione"));
        for (Regiao reg : getRegiaoAtualizada()) {
            this.regiaoLista.add(reg);
        }

        regiaoAdp = new ArrayAdapter<Regiao>(this, android.R.layout.simple_spinner_dropdown_item, regiaoLista);
        regiaoAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRegiao.setAdapter(regiaoAdp);

        btn_inserirAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });

        btn_aprovarAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aprovar();
            }
        });

        etUsuario.setEnabled(true);
        if (edicao) {
            etUsuario.setEnabled(false);
        }
    }

//    private void aguardandoAprovacao() {
//        if (Persistencia.getInstance().isPodeFinalizarTela()) {
//            finish();
//        } else {
//            aguardandoFinalizar();
//        }
//    }
//
//    public void aguardandoFinalizar() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                aguardandoAprovacao();
//            }
//        }, 500);
//    }

    private ArrayList<Usuario> getUsuarios() {
        ArrayList<Usuario> items = new ArrayList<>();
        for (Usuario usr : Persistencia.getInstance().getUsuarios()) {
            items.add(usr);
        }

        return items;
    }

    private void carregarTela() {
        Usuario usuario = new Usuario();
        usuario.setId(this.acesso.getUsuarioId());
        usuario = usuario.buscaObjetoNaLista(Persistencia.getInstance().getUsuariosComAcesso());

        if (usuario.getNome() != null && usuario.getNome().length() > 0) {
            this.etUsuario.setText(usuario.getTitle());
        }

        if (this.acesso.getTipoValor() != null){
            this.spnTipo.setSelection(acessoAdp.getPosition(this.acesso.getTipo()));
        }

        if (AcessoTipoEnum.UNIDADE.equals(this.acesso.getTipo())) {
            Unidade und = new Unidade();
            und.setId(this.acesso.getUnidadeId());
            und = und.buscaObjetoNaLista(Persistencia.getInstance().getUnidades());
            this.spnUnidade.setSelection(unidadeAdp.getPosition(und));
        } else if (AcessoTipoEnum.UNIVERSIDADE.equals(this.acesso.getTipo())) {
            Universidade uni = new Universidade();
            uni.setId(this.acesso.getUniversidadeId());
            uni = uni.buscaObjetoNaLista(Persistencia.getInstance().getUniversidades());
            this.spnUniversidade.setSelection(universidadeAdp.getPosition(uni));
        } else if (AcessoTipoEnum.REGIAO.equals(this.acesso.getTipo())) {
            Regiao reg = new Regiao();
            reg.setId(this.acesso.getRegiaoId());
            reg = reg.buscaObjetoNaLista(Persistencia.getInstance().getRegioes());
            this.spnRegiao.setSelection(regiaoAdp.getPosition(reg));
        }

        if (this.acesso.isModerador())
            this.cbModerador.setChecked(true);

        if (this.acesso.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor()) ||
                this.acesso.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor())) {
            if (this.acesso.isRepresentante())
                this.cbRepresentante.setChecked(true);
        } else if (this.acesso.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor())) {
            if (this.acesso.isRepresentante())
                this.cbRepresentanteUniversidade.setChecked(true);
        }

        if (this.acesso.isProfessor())
            this.cbProfessor.setChecked(true);

        if (this.acesso.isAluno())
            this.cbAluno.setChecked(true);
    }

    private ArrayList<Regiao> getRegiaoAtualizada() {
        ArrayList<Regiao> regs = new ArrayList<>();

        Acesso acesso = Persistencia.getInstance().getAcessoAtual();

        if (acesso.getNivelAcesso() == 7L) {
            regs = Persistencia.getInstance().getRegioes();
        } else if (acesso.getNivelAcesso() == 6L) {
            for (Regiao reg : Persistencia.getInstance().getRegioes()) {
                if (reg.getId().equals(acesso.getRegiaoId()))
                    regs.add(reg);
            }
        }

        return regs;
    }

    private ArrayList<Unidade> getUnidadeAtualizada() {
        ArrayList<Unidade> regs = new ArrayList<>();

        Acesso acesso = Persistencia.getInstance().getAcessoAtual();

        if (acesso.getNivelAcesso() == 5L) {
            for (Unidade reg : Persistencia.getInstance().getUnidades()) {
                if (reg.getId().equals(acesso.getUnidadeId()))
                    regs.add(reg);
            }
        }
        else if (acesso.getNivelAcesso() == 6L) {
            for(Unidade unidade: Persistencia.getInstance().getUnidades()){
                if(unidade.getRegiaoId().equals(acesso.getRegiaoId())){
                    regs.add(unidade);
                }
            }
        }
        else if (acesso.getNivelAcesso() == 7L){
            regs = Persistencia.getInstance().getUnidades();
        }

        return regs;
    }

    private ArrayList<Universidade> getUniversidadeAtualizada() {
        ArrayList<Universidade> reg = new ArrayList<>();
        Acesso acesso = Persistencia.getInstance().getAcessoAtual();

        if (acesso.getNivelAcesso() == 2L) {
            for (Universidade un : Persistencia.getInstance().getUniversidades()) {
                if (un.getId().equals(acesso.getUniversidadeId())) {
                    reg.add(un);
                }
            }
        }
        if (acesso.getNivelAcesso() == 4L) {
            for (Universidade un : Persistencia.getInstance().getUniversidades()) {
                if (un.getId().equals(acesso.getUniversidadeId())) {
                    reg.add(un);
                }
            }
        }
        else if (acesso.getNivelAcesso() == 5L) {
            for(Universidade universidade: Persistencia.getInstance().getUniversidades()){
                if(universidade.getUnidadeId().equals(acesso.getUnidadeId())){
                    reg.add(universidade);
                }
            }
        }
        else if (acesso.getNivelAcesso() == 6L){
            ArrayList<String> unidId = new ArrayList<>();

            for(Unidade unidade: Persistencia.getInstance().getUnidades()){
                if(unidade.getRegiaoId().equals(acesso.getRegiaoId())){
                    unidId.add(unidade.getId());
                }
            }

            for(Universidade universidade: Persistencia.getInstance().getUniversidades()){
                if(unidId.contains(universidade.getUnidadeId())){
                    reg.add(universidade);
                }
            }
        }
        else if (acesso.getNivelAcesso() == 7L){
            reg = Persistencia.getInstance().getUniversidades();
        }

        return reg;
    }
}
