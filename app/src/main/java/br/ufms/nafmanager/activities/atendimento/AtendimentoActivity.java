package br.ufms.nafmanager.activities.atendimento;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomDatePickerFragment;
import br.ufms.nafmanager.adapters.MaskEditUtil;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.AtendidoTipo;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.TipoDocumentoEnum;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AtendimentoActivity extends AppCompatActivity {

    //variáveis inicio
    private Acesso acesso = new Acesso();
    private Atendimento atendimento = new Atendimento();
    private TextView tvDataAtendimento;
    private TextView tvHoraAtendimento;
    private Spinner spnTipoDocumento;
    private EditText etCpf;
    private EditText etCnpj;
    private EditText etNome;
    private EditText etTelefone;
    private CheckBox atendimentoConclusivo;
    private ListView lvAtendimentoTipo;
    private Button btnFinalizarAtendimento;

    private SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

    private List<AtendimentoTipo> atendimentoTipoLista = new ArrayList<AtendimentoTipo>();
    private ArrayAdapter<AtendimentoTipo> atendimentoTipoAdapter;
    private ArrayList<String> atendimentoTipoIds;
    private List<AtendidoTipo> atendidoTipoList;
    private Spinner spnAtendido;
    //variáveis fim

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acesso = Persistencia.getInstance().getAcessoAtual();

        setContentView(R.layout.layout_atendimento);

        vincularComponentes();

        carregarAtendimentoTipoLocal();
        instanciarDataInicial();
        atendimento = new Atendimento();
    }

    private void vincularComponentes() {
        lvAtendimentoTipo = (ListView) findViewById(R.id.lv_atendimento_tipo);

        btnFinalizarAtendimento = (Button) findViewById(R.id.btn_finalizarAtendimento);
        atendimentoConclusivo = (CheckBox) findViewById(R.id.ctv_atendimentoConclusivo);
        spnAtendido = (Spinner) findViewById(R.id.sp_atendido);
        atendidoTipoList = Persistencia.getInstance().getAtendido();

        etCpf = findViewById(R.id.et_cpf);
        etCnpj = findViewById(R.id.et_cnpj);

        etCpf.addTextChangedListener(MaskEditUtil.mask(etCpf, MaskEditUtil.FORMAT_CPF));
        etCnpj.addTextChangedListener(MaskEditUtil.mask(etCnpj, MaskEditUtil.FORMAT_CNPJ));

        ArrayAdapter<AtendidoTipo> atendidoAdapter = new ArrayAdapter<AtendidoTipo>(this, android.R.layout.simple_spinner_dropdown_item, atendidoTipoList);
        atendidoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAtendido.setAdapter(atendidoAdapter);

        atendimentoTipoIds = new ArrayList<>();
        btnFinalizarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirAtendimento(atendimentoTipoIds);
            }
        });

        spnTipoDocumento = findViewById(R.id.sp_tipoDocumento);
        ArrayAdapter<TipoDocumentoEnum> tipoDocAdp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TipoDocumentoEnum.values());
        spnTipoDocumento.setAdapter(tipoDocAdp);

        spnTipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spnTipoDocumento.getSelectedItem().toString().equals(TipoDocumentoEnum.CPF.getLabel())){
                    etCnpj.setVisibility(View.INVISIBLE);
                    etCpf.setVisibility(View.VISIBLE);
                    etCnpj.setText("");
                }
                else{
                    etCnpj.setVisibility(View.VISIBLE);
                    etCpf.setVisibility(View.INVISIBLE);
                    etCpf.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etNome = findViewById(R.id.et_nome);
        etTelefone = findViewById(R.id.et_telefone);
        etTelefone.addTextChangedListener(MaskEditUtil.mask(etTelefone, MaskEditUtil.FORMAT_FONE));

        atendimentoConclusivo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    hideKeyboard();
                }
            }
        });
    }

    public void carregarAtendimentoTipoLocal() {
        atendimentoTipoLista = Persistencia.getInstance().getAtendimentos();
        atendimentoTipoAdapter = new ArrayAdapter<AtendimentoTipo>(getBaseContext(),
                                     R.layout.layout_atendimento_tipo,
                                     R.id.ctvAtendimentoTipo,
                                     atendimentoTipoLista);
        lvAtendimentoTipo.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvAtendimentoTipo.setAdapter(atendimentoTipoAdapter);
        lvAtendimentoTipo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AtendimentoTipo at = (AtendimentoTipo) parent.getItemAtPosition(position);
                if (atendimentoTipoIds.contains(at.getId())) {
                    atendimentoTipoIds.remove(at.getId());
                } else {
                    atendimentoTipoIds.add(at.getId());
                }
            }
        });
    }

    private void copiarTela(){
        atendimento.setAtendimentoTipoId(atendimentoTipoIds);

        if(spnTipoDocumento.getSelectedItem().toString() != null){
            atendimento.setAtendidoTipoDocumento((TipoDocumentoEnum) spnTipoDocumento.getSelectedItem());

            if(spnTipoDocumento.getSelectedItem().toString().equals(TipoDocumentoEnum.CPF.getLabel())){
                atendimento.setAtendidoDocumento(etCpf.getText().toString());
            }
            else if(spnTipoDocumento.getSelectedItem().toString().equals(TipoDocumentoEnum.CNPJ.getLabel())){
                atendimento.setAtendidoDocumento(etCnpj.getText().toString());
            }
        }

        if(etNome.getText() != null && etNome.getText().toString().trim().length() > 0){
            atendimento.setAtendidoNome(etNome.getText().toString());
        }

        if(etTelefone.getText() != null && etTelefone.getText().toString().trim().length() > 0){
            atendimento.setAtendidoFone(etTelefone.getText().toString());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
        Date dataAtendimento = new Date();
        try {
            dataAtendimento = sdf.parse(tvDataAtendimento.getText().toString() + ":" + tvHoraAtendimento.getText().toString());
        } catch (Exception e) {}

        atendimento.setAtendimentoTipoId(atendimentoTipoIds);
        atendimento.setDataAtendimento(dataAtendimento);
        atendimento.setConclusivo(this.atendimentoConclusivo.isChecked());
        atendimento.setAcessoId(acesso.getId());

        if(spnAtendido.getSelectedItem() != null && spnAtendido.getSelectedItem().toString().trim().length() > 0){
            AtendidoTipo at = (AtendidoTipo) spnAtendido.getSelectedItem();
            atendimento.setAtendidoTipoId(at.getId());
        }
    }

    private void inserirAtendimento(ArrayList<String> atendimentosIds) {
        copiarTela();
        if(atendimento.salvar()){
            finish();
        }

        Toast.makeText(this, atendimento.getMensagem(), Toast.LENGTH_SHORT).show();
    }

    private void instanciarDataInicial() {
        this.tvDataAtendimento = (TextView) findViewById(R.id.tv_dataAtendimentoView);
        this.tvHoraAtendimento = (TextView) findViewById(R.id.tv_horaAtendimento);

        this.tvDataAtendimento.setText(formatoData.format(new Date()));
        this.tvHoraAtendimento.setText(formatoHora.format(new Date()));

        this.tvDataAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(0);
            }
        });

        this.tvHoraAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(1);
            }
        });
    }

    public void showDatePicker(int q) {
        if (q == 0) {
            DialogFragment newFragment = new CustomDatePickerFragment(this.tvDataAtendimento, this.tvHoraAtendimento, 0);
            newFragment.show(getSupportFragmentManager(), "date picker");
        } else {
            DialogFragment newFragment = new CustomDatePickerFragment(this.tvDataAtendimento, this.tvHoraAtendimento, 1);
            newFragment.show(getSupportFragmentManager(), "time picker");
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
