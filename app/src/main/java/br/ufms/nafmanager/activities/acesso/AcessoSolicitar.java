package br.ufms.nafmanager.activities.acesso;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoSolicitar extends CustomActivity {

    private Button btn_inserirAcesso;
    private Spinner spnTipo;
    private Spinner spnUniversidade;
    private Spinner spnUnidade;
    private Spinner spnRegiao;
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
    private ArrayAdapter<Universidade> universidadeAdp;
    private ArrayAdapter<Unidade> unidadeAdp;
    private ArrayAdapter<Regiao> regiaoAdp;
    private Acesso acesso;
    private Acesso acessoLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_solicitar);

        vincularComponentes();

        controlaAcesso();

        if(Persistencia.getInstance().getAcessoAtual()!= null)
            acessoLogado = Persistencia.getInstance().getAcessoAtual();
    }

    private void inserir(){
        copiarTela();
        if(acesso.getMensagem() != null && acesso.getMensagem().length() >0){
            Toast.makeText(this, acesso.getMensagem(), Toast.LENGTH_SHORT).show();
        }
        else{
            showDialog();

            acesso.setSolicitando(true);
            Persistencia.getInstance().verificarAcessoExistente(acesso);
            aguardaValidarCadastroExistente();
        }
    }

    private void verificaCadastroExistente() {
        if(Persistencia.getInstance().isPesquisouAcessoJahCadastrado()){
            if(Persistencia.getInstance().isPodeGravarAcesso()){
                Persistencia.getInstance().verificarAcessoSolicitado(acesso);
                aguardaValidarCadastroJaSolicitado();
            }
            else{
                Toast.makeText(this, "Acesso já existente", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }
        else{
            aguardaValidarCadastroExistente();
        }
    }

    public void aguardaValidarCadastroExistente(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verificaCadastroExistente();
            }
        }, 500);
    }

    private void verificaCadastroJaSoliciado() {
        if(Persistencia.getInstance().isPesquisouAcessoJahSolicitado()){
            if(Persistencia.getInstance().isPodeGravarAcesso()){
                if(acesso.salvar()){
                    hideDialog();
                    Toast.makeText(this, "Solicitação efetuada com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            else{
                Toast.makeText(this, "Acesso já solicitado", Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }
        else{
            aguardaValidarCadastroJaSolicitado();
        }
    }

    public void aguardaValidarCadastroJaSolicitado(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verificaCadastroJaSoliciado();
            }
        }, 500);
    }

    private void copiarTela() {
        acesso = new Acesso();
        acesso.setStatus(StatusEnum.RASCUNHO);

        if(acessoLogado != null && acessoLogado.getId() != null && acessoLogado.getId().length() > 0){
            acesso.setUsuarioId(acessoLogado.getUsuarioId());
        }
        else{
            acesso.setUsuarioId(Persistencia.getInstance().getUsuarioAtual().getId());
        }

        if(spnTipo.getSelectedItem() != null && spnTipo.getSelectedItem().toString().length() >0)
            acesso.setTipo(AcessoTipoEnum.getEnumByLabel(spnTipo.getSelectedItem().toString()));

        if(spnRegiao.getSelectedItem() != null && ((Regiao)spnRegiao.getSelectedItem()).getId().length() > 0){
            Regiao regiao = (Regiao) spnRegiao.getSelectedItem();
            acesso.setRegiaoId(regiao.getId());
        }

        if(spnUnidade.getSelectedItem() != null && ((Unidade)spnUnidade.getSelectedItem()).getId().length() > 0){
            Unidade unidade = (Unidade) spnUnidade.getSelectedItem();
            acesso.setUnidadeId(unidade.getId());
        }

        if(spnUniversidade.getSelectedItem() != null && ((Universidade)spnUniversidade.getSelectedItem()).getId().length() > 0){
            Universidade universidade = (Universidade) spnUniversidade.getSelectedItem();
            acesso.setUniversidadeId(universidade.getId());
        }

        acesso.setModerador(false);
        acesso.setRepresentante(false);
        acesso.setProfessor(false);
        acesso.setAluno(false);

        if(acesso.getTipoValor().equals(AcessoTipoEnum.MODERADOR.getValor())) {
            if (cbModerador.isChecked())
                acesso.setModerador(true);
        }

        if(acesso.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())){
            if(cbRepresentante.isChecked())
                acesso.setRepresentante(true);
        }

        if(acesso.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor())){
            if(cbRepresentante.isChecked())
                acesso.setRepresentante(true);
        }

        if(acesso.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor())){
            if(cbRepresentanteUniversidade.isChecked())
                acesso.setRepresentante(true);

            if(cbProfessor.isChecked())
                acesso.setProfessor(true);

            if(cbAluno.isChecked())
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
        }
        else if(spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.MODERADOR.toString())){
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
        cbRepresentanteUniversidade.setVisibility(View.INVISIBLE);
        cbRepresentante.setVisibility(View.INVISIBLE);
        cbModerador.setVisibility(View.INVISIBLE);

        if (spnTipo.getSelectedItem().toString() != null) {

            cbModerador.setChecked(false);
            cbRepresentante.setChecked(false);
            cbRepresentanteUniversidade.setChecked(false);
            cbProfessor.setChecked(false);
            cbAluno.setChecked(false);

            if(spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.MODERADOR.getLabel())){
                cbModerador.setVisibility(View.VISIBLE);
                cbRepresentanteUniversidade.setChecked(false);
                cbRepresentante.setChecked(false);
            }
            else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.REGIAO.getLabel())) {
                cbRepresentanteUniversidade.setChecked(false);
                cbRepresentante.setVisibility(View.VISIBLE);
            }else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.UNIDADE.getLabel())) {
                cbRepresentanteUniversidade.setChecked(false);
                cbRepresentante.setVisibility(View.VISIBLE);
            }else if (spnTipo.getSelectedItem().toString().equals(AcessoTipoEnum.UNIVERSIDADE.getLabel())){
                cbRepresentante.setChecked(false);
                cbRepresentanteUniversidade.setVisibility(View.VISIBLE);
                cbProfessor.setVisibility(View.VISIBLE);
                cbAluno.setVisibility(View.VISIBLE);
          }
        }
    }

    private void vincularComponentes() {
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
                if(cbAluno.isChecked()){
                    cbRepresentanteUniversidade.setChecked(false);
                    cbProfessor.setChecked(false);
                }
            }
        });

        cbProfessor = findViewById(R.id.cb_acesso_professor);
        cbProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbProfessor.isChecked()) {
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

        this.spnTipo = findViewById(R.id.sp_acessoTipo);
        ArrayList<AcessoTipoEnum> acessoTipo = new ArrayList<>();
        acessoTipo.add(AcessoTipoEnum.UNIDADE);
        acessoTipo.add(AcessoTipoEnum.UNIVERSIDADE);
        acessoTipo.add(AcessoTipoEnum.REGIAO);
        acessoTipo.add(AcessoTipoEnum.MODERADOR);

        ArrayAdapter<AcessoTipoEnum> acessoAdp = new ArrayAdapter<AcessoTipoEnum>(this, android.R.layout.simple_spinner_dropdown_item, acessoTipo);
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
        for (Universidade unv : Persistencia.getInstance().getUniversidades()) {
            this.universidadeLista.add(unv);
        }

        universidadeAdp = new ArrayAdapter<Universidade>(this, android.R.layout.simple_spinner_dropdown_item, universidadeLista);
        universidadeAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUniversidade.setAdapter(universidadeAdp);

        unidadeLista = new ArrayList<Unidade>();
        unidadeLista.add(new Unidade("", "Selecione"));
        for (Unidade uns : Persistencia.getInstance().getUnidades()) {
            this.unidadeLista.add(uns);
        }

        unidadeAdp = new ArrayAdapter<Unidade>(this, android.R.layout.simple_spinner_dropdown_item, unidadeLista);
        unidadeAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnUnidade.setAdapter(unidadeAdp);

        regiaoLista = new ArrayList<Regiao>();
        regiaoLista.add(new Regiao("", "Selecione"));
        for (Regiao reg : Persistencia.getInstance().getRegioes()) {
            this.regiaoLista.add(reg);
        }

        regiaoAdp = new ArrayAdapter<Regiao>(this, android.R.layout.simple_spinner_dropdown_item, regiaoLista);
        regiaoAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRegiao.setAdapter(regiaoAdp);

        btn_inserirAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserir();
            }
        });
    }
}
