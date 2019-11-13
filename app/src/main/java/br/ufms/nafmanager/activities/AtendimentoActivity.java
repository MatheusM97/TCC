package br.ufms.nafmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AtendimentoActivity extends AppCompatActivity{

    //variáveis inicio

    private String usuarioLogadoId = "";
    private String usuarioLogadoNome = "";
    private String universidadeId = "";
    private String universidadeNome = "";
    private String unidadeId = "";
    private String unidadeNome = "";

    int dia = 0;
    int mes = 0;
    int ano = 0;
    int hora = 0;
    int minuto = 0;
    int segundo = 0;

    private TextView tvDataAtendimento;
    private TextView tvHoraAtendimento;
    private TextView lbDataAtendimento;
    private Date tempoInicioAtendimento;
    private CheckBox atendimentoConclusivo;
    private ListView lvAtendimentoTipo;
    private Button btnFinalizarAtendimento;
    private Button btnCancelarAtendimento;

    private SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

    private FirebaseFirestore firebaseFirestore;
    private Date dataInicioAtendimento;

    private List<AtendimentoTipo> atendimentoTipoLista = new ArrayList<AtendimentoTipo>();
    private ArrayAdapter<AtendimentoTipo> atendimentoTipoAdapter;
    private ArrayList<String> atendimentosIds;
    //variáveis fim

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("usuarioLogadoId") != null)
                this.usuarioLogadoId = extras.getString("usuarioLogadoId");

            if (extras.getString("usuarioLogadoNome") != null)
                this.usuarioLogadoNome = extras.getString("usuarioLogadoNome");

            if (extras.getString("universidadeId") != null)
                this.universidadeId = extras.getString("universidadeId");

            if (extras.getString("universidadeNome") != null)
                this.universidadeNome = extras.getString("universidadeNome");

            if (extras.getString("unidadeId") != null)
                this.unidadeId = extras.getString("unidadeId");

            if (extras.getString("unidadeNome") != null)
                this.unidadeNome = extras.getString("unidadeNome");
        }

        setContentView(R.layout.layout_atendimento);
        lvAtendimentoTipo = (ListView) findViewById(R.id.lv_atendimento_tipo);
        btnFinalizarAtendimento = (Button) findViewById(R.id.btn_finalizarAtendimento);
        atendimentoConclusivo = (CheckBox) findViewById(R.id.ctv_atendimentoConclusivo);

        atendimentosIds = new ArrayList<>();
        this.dataInicioAtendimento = new Date();
        btnFinalizarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirAtendimento(atendimentosIds);
            }
        });
        carregarAtendimentoTipoLocal();
        instanciarDados();
        instanciarDataInicial();
//        carregarAtendimentoTipo();
        iniciarCronometroAtendimento();
    }

    public void carregarAtendimentoTipoLocal() {
        atendimentoTipoLista = Persistencia.getInstance().getAtendimentos();
        atendimentoTipoAdapter =
                new ArrayAdapter<AtendimentoTipo>(getBaseContext(),
                        R.layout.layout_atendimento_tipo,
                        R.id.ctvAtendimentoTipo,
                        atendimentoTipoLista);
        lvAtendimentoTipo.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvAtendimentoTipo.setAdapter(atendimentoTipoAdapter);
        lvAtendimentoTipo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AtendimentoTipo at = (AtendimentoTipo) parent.getItemAtPosition(position);
                if (atendimentosIds.contains(at.getId())) {
                    atendimentosIds.remove(at.getId());
                } else {
                    atendimentosIds.add(at.getId());
                }
            }
        });
    }

    private void iniciarCronometroAtendimento() {
        this.tempoInicioAtendimento = new Date();
    }

    private void carregarAtendimentoTipo() {
        firebaseFirestore.collection("atendimento_tipo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AtendimentoTipo atendimentoTipo = document.toObject(AtendimentoTipo.class);
                                atendimentoTipo.setId(document.getId());
                                atendimentoTipoLista.add(atendimentoTipo);
                            }
                        }
                    }
                });
    }

    private void instanciarDados() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void inserirAtendimento(ArrayList<String> atendimentosIds) {
        if (this.atendimentosIds != null && this.atendimentosIds.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
            Date dataAtendimento = new Date();
            try {
                dataAtendimento = sdf.parse(tvDataAtendimento.getText().toString());
            } catch (Exception e) {
            }

            Atendimento atendimento = new Atendimento();
            atendimento.setDataAtendimento(dataAtendimento);
            atendimento.setTempoAtendimento(this.getTempoDeAtendimento());
            atendimento.setAtendimentoConclusivo(this.atendimentoConclusivo.isChecked());
            if (this.usuarioLogadoId != null && this.usuarioLogadoId.length() > 0)
                atendimento.setUsuarioId(this.usuarioLogadoId);
            if (this.unidadeId != null && this.unidadeId.length() > 0)
                atendimento.setUnidadeId(this.unidadeId);
            if (this.universidadeId != null && this.universidadeId.length() > 0)
                atendimento.setUniversidadeId(this.universidadeId);
            atendimento.setAtendimentoTipo(atendimentosIds);

            Persistencia.getInstance().persistirObjeto(atendimento);
            if (atendimento.getId() != null && atendimento.getId().length() > 0) {
                Toast.makeText(AtendimentoActivity.this, "Atendimento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                limparTudo();
                retornaTelaPrincipal();
            } else {
                Toast.makeText(AtendimentoActivity.this, "Falha ao salvar atendimento!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Selecione ao menos um atendimento!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void limparTudo() {
        this.atendimentosIds = new ArrayList<>();
        this.dataInicioAtendimento = new Date();
        for (int i = 0; i < this.lvAtendimentoTipo.getAdapter().getCount(); i++) {
            this.lvAtendimentoTipo.setItemChecked(i, false);
        }
    }

    public Long getTempoDeAtendimento() {
        long diferenca = Math.abs(new Date().getTime() - this.dataInicioAtendimento.getTime());
        return TimeUnit.SECONDS.convert(diferenca, TimeUnit.MILLISECONDS);
    }

    public void retornaTelaPrincipal() {
        this.finish();
    }


    private void instanciarDataInicial() {
        this.tvDataAtendimento = (TextView) findViewById(R.id.tv_dataAtendimentoView);
        this.lbDataAtendimento = (TextView) findViewById(R.id.tv_dtAtendimento);
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
        if(q == 0){
            DialogFragment newFragment = new CustomDatePickerFragment(this.tvDataAtendimento, this.tvHoraAtendimento,0);
            newFragment.show(getSupportFragmentManager(), "date picker");
        }

        else{
            DialogFragment newFragment = new CustomDatePickerFragment(this.tvDataAtendimento, this.tvHoraAtendimento,1);
            newFragment.show(getSupportFragmentManager(), "time picker");
        }
    }
}
