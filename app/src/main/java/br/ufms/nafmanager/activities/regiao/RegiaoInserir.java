package br.ufms.nafmanager.activities.regiao;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.adapters.UsuarioAdapter;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.persistencies.Persistencia;

public class RegiaoInserir extends CustomActivity {

    private Regiao regiao;
    private ListView lvEstados;
    private ArrayList<Estado> estados;
    private ArrayList<String> estadosSelecionados;
    private ArrayAdapter<Estado> estadoAdapter;
    private Button btnCadastrar;
    private EditText nome;
    private boolean edicao = false;
    private ListView lvRepresentantes;
    private UsuarioAdapter usuarioAdapter;
    private TextView tvRepresentante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regiao_inserir);

        this.edicao = false;
        if(getIntent().getSerializableExtra("regiao") != null){
            this.regiao = (Regiao) getIntent().getSerializableExtra("regiao");
            this.edicao = true;
        }

        vincularComponentes();
        carregarEstados();

        if(edicao && regiao != null){
            regiao.setEdicao(true);
            carregarTela();
        }
    }

    private void vincularComponentes() {
        nome = (EditText) findViewById(R.id.et_regiaoNome);
        lvEstados = (ListView) findViewById(R.id.lv_estados);
        btnCadastrar = (Button)findViewById(R.id.btn_criarRegiao);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserir();
            }
        });

        this.lvRepresentantes = findViewById(R.id.lv_representanteRegiao);
        this.lvRepresentantes.setVisibility(View.INVISIBLE);

        this.tvRepresentante = findViewById(R.id.tv_representante);
        this.tvRepresentante.setVisibility(View.INVISIBLE);
    }

    public void carregarEstados() {
        estadosSelecionados = new ArrayList<>();

        estados = Persistencia.getInstance().getEstados();

        estadoAdapter = new ArrayAdapter<Estado>(getBaseContext(),
                R.layout.layout_atendimento_tipo,
                R.id.ctvAtendimentoTipo,
                estados);
        lvEstados.setDividerHeight(1);
        lvEstados.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvEstados.setAdapter(estadoAdapter);
        lvEstados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Estado estado = (Estado) parent.getItemAtPosition(position);
                if (estadosSelecionados.contains(estado.getId())) {
                    estadosSelecionados.remove(estado.getId());
                } else {
                    estadosSelecionados.add(estado.getId());
                }
                hideKeyboard();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void inserir(){
        copiarTela();
        Persistencia.getInstance().validarInserirRegiao(regiao);
        aguardandoValidacao();
    }

    private void aguardando(){
        if(Persistencia.getInstance().isValidouInserirRegiao()){
            if(Persistencia.getInstance().isPodeInserirRegiao()){
                if(regiao.salvar()){
                    Persistencia.getInstance().setRegiaoAtual(regiao);
                    finish();
                }
                Toast.makeText(this, regiao.getMensagem(), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Estado já cadastrado em uma Região Fiscal", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            aguardandoValidacao();
        }
    }

    private void aguardandoValidacao() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardando();
            }
        }, 500);
    }

    private void carregarTela() {
        if(regiao.getNome() != null && regiao.getNome().length() >0)
            nome.setText(regiao.getNome());

        if(regiao.getEstados() != null && regiao.getEstados().size() > 0){
            estadosSelecionados.addAll(regiao.getEstados());
        }

        int count = estadoAdapter.getCount();
        for(String estadoId: estadosSelecionados){
            for(int i = 0; i < count; i++) {
                Estado estado = (Estado) lvEstados.getItemAtPosition(i);
                if(estado.getId().equals(estadoId)){
                    lvEstados.setItemChecked(i, true);
                }
            }
        }

        usuarioAdapter = new UsuarioAdapter(this, Persistencia.getInstance().getRegiaoAtual().getRepresentantes());
        lvRepresentantes.setAdapter(usuarioAdapter);

        lvRepresentantes.setVisibility(View.VISIBLE);
        tvRepresentante.setVisibility(View.VISIBLE);
    }

    private void copiarTela(){
        if (!edicao && (regiao == null || regiao.getId() == null || regiao.getId().length() == 0)) {
            regiao = new Regiao();
        }
        if (this.nome.getText() != null && this.nome.getText().length() > 0) {
            regiao.setNome(this.nome.getText().toString());
        }

        if(estadosSelecionados != null && estadosSelecionados.size() > 0){
            regiao.setEstados(estadosSelecionados);
        }
    }
}

