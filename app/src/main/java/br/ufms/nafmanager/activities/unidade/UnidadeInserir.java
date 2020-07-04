package br.ufms.nafmanager.activities.unidade;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.adapters.UsuarioAdapter;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.UnidadeTipo;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UnidadeInserir extends AppCompatActivity {

    private EditText unidadeNome;
    private Spinner spinnerTipo;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private Spinner spinnerRegiao;
    private Button btn_inserirUnidade;
    private ArrayList<Estado> estadoLista;
    private ArrayList<Cidade> cidadeLista;
    private ArrayList<Regiao> regiaoLista;
    private boolean edicao = false;
    private Unidade unidade;
    private List<UnidadeTipo> tipoLista = new ArrayList<>();
    private ArrayAdapter<Cidade> cidadeAdptr;
    private ArrayAdapter<Estado> estAdp;
    private ArrayAdapter<UnidadeTipo> adp;
    private ArrayAdapter<Regiao> regAdp;
    private boolean copiandoTela = false;

    private ListView lvRepresentantes;
    private UsuarioAdapter usuarioAdapter;
    private TextView tvRepresentante;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_inserir);

        this.edicao = false;
        if(getIntent().getSerializableExtra("unidade") != null){
            this.unidade = (Unidade) getIntent().getSerializableExtra("unidade");
            this.edicao = true;
        }

       vincularComponentes();

        if(edicao && unidade != null){
            carregarTela();
        }

        controlaAcesso();
    }

    private void controlaAcesso() {
        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

        spinnerRegiao.setEnabled(false);

        if(acessoLogado.getNivelAcesso() == 7L){
            spinnerRegiao.setEnabled(true);
        }
    }

    private void vincularComponentes() {
        tipoLista = new ArrayList<>();
        estadoLista = Persistencia.getInstance().getEstados();
        cidadeLista = new ArrayList<>();

        this.unidadeNome = findViewById(R.id.et_unidadeNome);
        this.unidadeNome.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard();
                    textView.clearFocus();
                    spinnerTipo.requestFocus();
                    spinnerTipo.performClick();
                }
                return true;
            }
        });


        regiaoLista = Persistencia.getInstance().getRegioes();
        spinnerRegiao = (Spinner) findViewById(R.id.sp_regiaoFiscal);

        regAdp = new ArrayAdapter<Regiao>(this, android.R.layout.simple_spinner_dropdown_item, this.regiaoLista);
        regAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(regAdp);
        spinnerRegiao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerRegiao.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });

        spinnerRegiao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!copiandoTela){
                    estadoLista = Persistencia.getInstance().getEstadosByRegiao((Regiao) parent.getSelectedItem());
                    setAdapterEstado();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        spinnerTipo = (Spinner) findViewById(R.id.sp_unidadeTipo);
        tipoLista = Persistencia.getInstance().getUnidadesTipo();

        adp = new ArrayAdapter<UnidadeTipo>(this, android.R.layout.simple_spinner_dropdown_item, tipoLista);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTipo.setAdapter(adp);
        spinnerTipo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerTipo.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });

        spinnerEstado = (Spinner) findViewById(R.id.sp_estadoNome);
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(copiandoTela){
                    copiandoTela = false;
                }
                else{
                    cidadeLista = Persistencia.getInstance().getCidades((Estado) parent.getSelectedItem());
                    setAdapterCidade();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerEstado.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerEstado.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });

        this.spinnerCidade = (Spinner) findViewById(R.id.sp_cidadeNome);
        this.spinnerCidade.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerCidade.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });


        this.btn_inserirUnidade = (Button) findViewById(R.id.btn_inserirUnidade);
        this.btn_inserirUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserir();
            }
        });

        this.lvRepresentantes = findViewById(R.id.lv_representanteUnidade);
        this.lvRepresentantes.setVisibility(View.INVISIBLE);

        this.tvRepresentante = findViewById(R.id.tv_representante);
        this.tvRepresentante.setVisibility(View.INVISIBLE);
    }

    private void inserir(){
        copiarTela();
        if(unidade.salvar()){
            Persistencia.getInstance().setUnidadeAtual(unidade);
            finish();
        }

        Toast.makeText(this, unidade.getMensagem(), Toast.LENGTH_SHORT).show();
    }

    private void carregarTela() {
        this.unidadeNome.setText(unidade.getNome());

        UnidadeTipo uTipo = new UnidadeTipo();
        uTipo.setId(unidade.getTipoId());
        uTipo = uTipo.buscaObjetoNaLista(Persistencia.getInstance().getUnidadesTipo());

        this.spinnerTipo.setSelection(adp.getPosition(uTipo));

        Regiao reg = new Regiao();
        reg.setId(unidade.getRegiaoId());
        reg = reg.buscaObjetoNaLista(Persistencia.getInstance().getRegioes());
        this.spinnerRegiao.setSelection(regAdp.getPosition(reg));

        Cidade cid = new Cidade();
        cid.setId(unidade.getCidadeId());
        cid = cid.buscaObjetoNaLista(Persistencia.getInstance().getCidades());

        Estado est = new Estado();
        est.setId(cid.getEstadoId());
        est = est.buscaObjetoNaLista(Persistencia.getInstance().getEstados());
        this.estadoLista = Persistencia.getInstance().getEstadosByRegiao(reg);
        this.setAdapterEstado();
        this.spinnerEstado.setSelection(estAdp.getPosition(est));

        this.cidadeLista = Persistencia.getInstance().getCidades(est);
        this.setAdapterCidade();
        this.spinnerCidade.setSelection(cidadeAdptr.getPosition(Persistencia.getInstance().getCidade(unidade.getCidadeId())));
        this.copiandoTela = true;

        usuarioAdapter = new UsuarioAdapter(this, Persistencia.getInstance().getUnidadeAtual().getRepresentantes());
        lvRepresentantes.setAdapter(usuarioAdapter);

        lvRepresentantes.setVisibility(View.VISIBLE);
        tvRepresentante.setVisibility(View.VISIBLE);
    }

    private void copiarTela() {
        if(!edicao && unidade == null){
            unidade = new Unidade();
        }

        if (unidadeNome.getText() != null && unidadeNome.getText().length() > 0) {
            unidade.setNome(unidadeNome.getText().toString());
        }

        if (spinnerRegiao.getSelectedItem() != null && spinnerRegiao.getSelectedItem().toString().length() > 0) {
            Regiao reg = (Regiao) spinnerRegiao.getSelectedItem();
            unidade.setRegiaoId(reg.getId());
        }

        if (spinnerTipo.getSelectedItem() != null && spinnerTipo.getSelectedItem().toString().length() > 0) {
            UnidadeTipo ut = (UnidadeTipo) spinnerTipo.getSelectedItem();
            unidade.setTipoId(ut.getId());
        }

        if (spinnerCidade.getSelectedItem() != null && spinnerCidade.getSelectedItem().toString().length() > 0) {
            Cidade cidade = (Cidade) spinnerCidade.getSelectedItem();
            unidade.setCidadeId(cidade.getId());
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void setAdapterEstado() {
        estAdp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estadoLista);
        estAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(estAdp);
    }

    private void setAdapterCidade() {
        cidadeAdptr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeLista);
        cidadeAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCidade.setAdapter(cidadeAdptr);
    }
}
