package br.ufms.nafmanager.activities.universidade;

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

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.adapters.UsuarioAdapter;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.persistencies.Persistencia;


public class UniversidadeInserir extends AppCompatActivity {

    private EditText universidadeNome;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private Spinner spinnerUnidades;
    private ArrayList<Estado> estadoLista;
    private ArrayList<Cidade> cidadeLista;
    private ArrayList<Unidade> unidadeLista;
    private ArrayAdapter<Estado> estAdp;
    private ArrayAdapter<Cidade> cidAdp;
    private ArrayAdapter<Unidade> undAdp;
    private Button btn_inserirUniversidade;
    private boolean edicao = false;
    private Universidade universidade;
    private boolean copiandoTela = false;
    private ListView lvRepresentantes;
    private UsuarioAdapter usuarioAdapter;
    private TextView tvRepresentante;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universidade_inserir);

        this.edicao = false;
        if (getIntent().getSerializableExtra("universidade") != null) {
            this.universidade = (Universidade) getIntent().getSerializableExtra("universidade");
            this.edicao = true;
        }

        vincularComponentes();

        if (edicao && universidade != null) {
            carregarTela();
        }

        controlaAcesso();
    }

    private void controlaAcesso() {
        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

        spinnerUnidades.setEnabled(false);
        if(acessoLogado.getNivelAcesso() >= 6L){
            spinnerUnidades.setEnabled(true);
        }
    }

    private void vincularComponentes() {
        estadoLista = new ArrayList<>();
        cidadeLista = new ArrayList<Cidade>();

        universidadeNome = (EditText) findViewById(R.id.et_universidade_nome);
        spinnerUnidades = (Spinner) findViewById(R.id.sp_universidade_unidade);
        spinnerEstado = (Spinner) findViewById(R.id.sp_universidade_estado_nome);
        spinnerCidade = (Spinner) findViewById(R.id.sp_universidade_cidade_nome);
        btn_inserirUniversidade = (Button) findViewById(R.id.btn_inserirUniversidade);

        this.universidadeNome.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard();
                    textView.clearFocus();
                    spinnerEstado.requestFocus();
                    spinnerEstado.performClick();
                }
                return true;
            }
        });

        this.unidadeLista = Persistencia.getInstance().getUnidades();

        undAdp = new ArrayAdapter<Unidade>(this, android.R.layout.simple_spinner_dropdown_item, unidadeLista);
        undAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidades.setAdapter(undAdp);

        this.spinnerUnidades.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerUnidades.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });

        this.spinnerUnidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!copiandoTela){
                    Unidade und = (Unidade) parent.getSelectedItem();
                    und.getRegiaoId();

                    Regiao reg = new Regiao();
                    reg.setId(und.getRegiaoId());

                    reg = reg.buscaObjetoNaLista(Persistencia.getInstance().getRegioes());

                    estadoLista = Persistencia.getInstance().getEstadosByRegiao(reg);
                    setAdapterEstado();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Regiao reg = new Regiao();

        estadoLista = new ArrayList<>();

        if(unidadeLista.size() >0){
            reg.setId(unidadeLista.get(0).getRegiaoId());
            reg = reg.buscaObjetoNaLista(Persistencia.getInstance().getRegioes());
            estadoLista = Persistencia.getInstance().getEstadosByRegiao(reg);
            setAdapterEstado();
        }

        spinnerEstado.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerEstado.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });

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

        this.spinnerCidade.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerCidade.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });

        this.btn_inserirUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserir();
            }
        });

        this.lvRepresentantes = findViewById(R.id.lv_representanteUniversidade);
        this.lvRepresentantes.setVisibility(View.INVISIBLE);

        this.tvRepresentante = findViewById(R.id.tv_representante);
        this.tvRepresentante.setVisibility(View.INVISIBLE);
    }

    private void inserir() {
        copiarTela();
        if(universidade.salvar()){
            Persistencia.getInstance().setUniversidadeAtual(universidade);
            finish();
        }

        Toast.makeText(this, universidade.getMensagem(), Toast.LENGTH_SHORT).show();
    }

    private void setAdapterEstado() {
        estAdp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estadoLista);
        estAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(estAdp);
    }

    private void carregarTela() {
        this.universidadeNome.setText(universidade.getNome());

        this.spinnerUnidades.setSelection(undAdp.getPosition(Persistencia.getInstance().getUnidadeById(universidade.getUnidadeId())));

        Cidade cid = new Cidade();
        cid.setId(universidade.getCidadeId());
        cid = cid.buscaObjetoNaLista(Persistencia.getInstance().getCidades());

        Estado est = new Estado();
        est.setId(cid.getEstadoId());
        est = est.buscaObjetoNaLista(Persistencia.getInstance().getEstados());
        this.spinnerEstado.setSelection(estAdp.getPosition(est));

        this.cidadeLista = Persistencia.getInstance().getCidades(est);
        this.setAdapterCidade();
        this.spinnerCidade.setSelection(cidAdp.getPosition(Persistencia.getInstance().getCidade(universidade.getCidadeId())));
        this.copiandoTela = true;

        usuarioAdapter = new UsuarioAdapter(this, Persistencia.getInstance().getUniversidadeAtual().getRepresentantes());
        lvRepresentantes = findViewById(R.id.lv_representanteUniversidade);
        lvRepresentantes.setAdapter(usuarioAdapter);

        lvRepresentantes.setVisibility(View.VISIBLE);
        tvRepresentante.setVisibility(View.VISIBLE);
    }

    private void copiarTela(){
        if (!edicao && universidade == null) {
            universidade = new Universidade();
        }

        if (universidadeNome.getText() != null && universidadeNome.getText().length() > 0) {
            universidade.setNome(universidadeNome.getText().toString());
        }

        if (spinnerCidade.getSelectedItem() != null && spinnerCidade.getSelectedItem().toString().length() > 0) {
            Cidade cidade = (Cidade) spinnerCidade.getSelectedItem();
            universidade.setCidadeId(cidade.getId());
        }

        if (spinnerUnidades.getSelectedItem() != null && spinnerUnidades.getSelectedItem().toString().length() > 0) {
            Unidade und = (Unidade) spinnerUnidades.getSelectedItem();
            universidade.setUnidadeId(und.getId());
        }
    }

    private void setAdapterCidade() {
        cidAdp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeLista);
        cidAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCidade.setAdapter(cidAdp);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
