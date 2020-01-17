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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
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
    private List<Cidade> cidadeLista;
    private ArrayList<Regiao> regiaoLista;
    private AutoCompleteTextView acCidade;
    private boolean edicao = false;
    private Unidade unidade;
    private List<UnidadeTipo> tipoLista = new ArrayList<>();
    private ArrayAdapter<Cidade> cidadeAdptr;
    private ArrayAdapter<Estado> estAdp;
    private ArrayAdapter<UnidadeTipo> adp;
    private ArrayAdapter<Regiao> regAdp;

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
    }

    private void vincularComponentes() {
        tipoLista = new ArrayList<>();
        List<Estado> estadoLista = new ArrayList<>();
        cidadeLista = new ArrayList<Cidade>();

        this.unidadeNome = (EditText) findViewById(R.id.et_unidadeNome);
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

        this.btn_inserirUnidade = (Button) findViewById(R.id.btn_inserirUnidade);

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
        estadoLista = Persistencia.getInstance().getEstados();

        estAdp = new ArrayAdapter<Estado>(this, android.R.layout.simple_spinner_dropdown_item, estadoLista);
        estAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEstado.setAdapter(estAdp);
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cidadeLista = Persistencia.getInstance().getCidades((Estado) parent.getSelectedItem());
                setAdapterCidade();
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

        this.regiaoLista = Persistencia.getInstance().getRegioes();
        this.spinnerRegiao = (Spinner) findViewById(R.id.sp_regiaoFiscal);

        regAdp = new ArrayAdapter<Regiao>(this, android.R.layout.simple_spinner_dropdown_item, this.regiaoLista);
        regAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(regAdp);

        this.spinnerRegiao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerRegiao.requestFocusFromTouch();
                hideKeyboard();
                return false;
            }
        });

        this.btn_inserirUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserir();
            }
        });
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

        this.spinnerTipo.setSelection(adp.getPosition(uTipo));

        int pos = 0;
//        for(int i =0; i <= estAdp.getCount(); i++){
//            Estado estado = estAdp.getItem(i);
//            if(estado.getId().equals(unidade.getEstado().getId())){
//                pos = i;
//                break;
//            }
//        }
        this.spinnerEstado.setSelection(pos);

        pos = 0;
        if(unidade.getRegiaoFiscalId() != null && unidade.getRegiaoFiscalId().length() > 0){
            for(int i = 0; i <= regAdp.getCount(); i++){
                Regiao reg = regAdp.getItem(i);
                if(reg.getId().equals(unidade.getRegiaoFiscalId())){
                    pos = i;
                    break;
                }
            }
        }
        this.spinnerRegiao.setSelection(pos);
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
            unidade.setRegiaoFiscalId(reg.getId());
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

    private void setAdapterCidade() {
        cidadeAdptr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeLista);
        cidadeAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCidade.setAdapter(cidadeAdptr);

        if (edicao && unidade != null && unidade.getId() != null) {
            int pos = 0;

            for(int i =0; i <= cidadeAdptr.getCount(); i++){
                Cidade cid = cidadeAdptr.getItem(i);
                if(unidade.getCidadeId().equals(cid.getId())){
                    pos = i;
                    break;
                }
            }
            this.spinnerCidade.setSelection(pos);
        }
    }
}
