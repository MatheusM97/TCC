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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;


public class UniversidadeInserir extends AppCompatActivity {

    private EditText universidadeNome;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private Spinner spinnerUnidades;
    //    private Spinner spinnerCoordenador;
    private List<Cidade> cidadeLista;
    private List<Unidade> unidadeLista;
    //    private List<String> coordSel;
//    private List<String> coorSelBk;
//    private TextView coordenadorSelect;
//    private List<Usuario> coordLista;
//    private List<Usuario> coordListaSel;
    private ArrayAdapter<Usuario> usuarioAdapter;
    private ListView lv;
    private TextView btn_inserir;
    private boolean edicao = false;
    private Universidade universidade;
    private ArrayAdapter<Estado> estAdp;
    private ArrayAdapter<Unidade> undAdp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universidade_inserir);
        btn_inserir = (TextView) findViewById(R.id.btn_inserirUniversidade);

        if (getIntent().getSerializableExtra("universidade") != null) {
            this.universidade = (Universidade) getIntent().getSerializableExtra("universidade");
            this.edicao = true;
        }

        universidadeNome = (EditText) findViewById(R.id.et_universidade_nome);
//        coordSel = new ArrayList<>();
//        coorSelBk = new ArrayList<>();
        List<Estado> estadoLista = new ArrayList<>();
        cidadeLista = new ArrayList<Cidade>();

        spinnerEstado = (Spinner) findViewById(R.id.sp_universidade_estado_nome);
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

        this.spinnerCidade = (Spinner) findViewById(R.id.sp_universidade_cidade_nome);
        this.spinnerCidade.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerCidade.requestFocusFromTouch();
                return false;
            }
        });

        this.spinnerUnidades = (Spinner) findViewById(R.id.sp_universidade_unidade);
        unidadeLista = Persistencia.getInstance().getUnidades();
        undAdp = new ArrayAdapter<Unidade>(this, android.R.layout.simple_spinner_dropdown_item, unidadeLista);
        undAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidades.setAdapter(undAdp);

        this.spinnerUnidades.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                spinnerUnidades.requestFocusFromTouch();
                return false;
            }
        });

        this.btn_inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!edicao && universidade.getId() == null && universidade.getId().length() == 0) {
                        universidade = new Universidade();
                    }

                    if (universidadeNome.getText() != null && universidadeNome.getText().length() > 0) {
                        universidade.setNome(universidadeNome.getText().toString());
                    }

                    if (spinnerEstado.getSelectedItem() != null && spinnerEstado.getSelectedItem().toString().length() > 0) {
                        Estado estado = (Estado) spinnerEstado.getSelectedItem();
                        universidade.setEstadoId(estado.getId());
                        universidade.setEstadoNome(estado.getNome());
                        universidade.setEstadoSigla(estado.getSigla());
                    }

                    if (spinnerCidade.getSelectedItem() != null && spinnerCidade.getSelectedItem().toString().length() > 0) {
                        Cidade cidade = (Cidade) spinnerCidade.getSelectedItem();
                        universidade.setCidadeId(cidade.getId());
                        universidade.setCidadeNome(cidade.getNome());
                    }

                    if (spinnerUnidades.getSelectedItem() != null && spinnerUnidades.getSelectedItem().toString().length() > 0) {
                        Unidade und = (Unidade) spinnerUnidades.getSelectedItem();
                        universidade.setUnidadeId(und.getId());
                        universidade.setUnidadeNome(und.getNome());
                    }

                    if (validar(universidade)) {
                        Persistencia.getInstance().persistirObjeto(universidade);
                        if (universidade.getId() != null && universidade.getId().length() > 0) {
                            Toast.makeText(UniversidadeInserir.this, "Universidade salva com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UniversidadeInserir.this, "Falha ao salvar a universidade!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(UniversidadeInserir.this, "Falha ao salvar a universidade!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (edicao && universidade != null) {
            carregarTela();
        }
    }

    private void carregarTela() {
        this.universidadeNome.setText(universidade.getNome());

        Estado est = new Estado();
        est.setNome(universidade.getEstadoNome());
        est.setId(universidade.getEstadoId());
        est.setSigla(universidade.getEstadoSigla());

        int pos = 0;
        for (int i = 0; i <= estAdp.getCount(); i++) {
            Estado estado = estAdp.getItem(i);
            if (estado.getId().equals(est.getId())) {
                pos = i;
                break;
            }
        }
        this.spinnerEstado.setSelection(pos);

        if(universidade.getUnidadeId()!= null && universidade.getId().length() > 0){
            pos = 0;
            for (int i = 0; i <= undAdp.getCount(); i++) {
                Unidade und = undAdp.getItem(i);
                if (und.getId().equals(universidade.getUnidadeId())) {
                    pos = i;
                    break;
                }
            }
            this.spinnerUnidades.setSelection(pos);
        }
    }

    private void setAdapterCidade() {
        ArrayAdapter<Cidade> cidadeAdptr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeLista);
        cidadeAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCidade.setAdapter(cidadeAdptr);

        if (edicao && universidade != null && universidade.getId() != null) {
            Cidade cidade = new Cidade();
            cidade.setId(universidade.getCidadeId());
            cidade.setNome(universidade.getCidadeNome());

            int pos = 0;

            for (int i = 0; i <= cidadeAdptr.getCount(); i++) {
                Cidade cid = cidadeAdptr.getItem(i);
                if (cidade.getId().equals(cid.getId())) {
                    pos = i;
                    break;
                }
            }
            this.spinnerCidade.setSelection(pos);
        }
    }

    public boolean validar(Universidade und) {
        boolean retorno = true;

        if (und.getNome() == null || und.getNome().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar um nome", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if (und.getEstadoId() == null || und.getEstadoId().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar um estado", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if (und.getCidadeId() == null || und.getCidadeId().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar uma cidade", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        return retorno;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
