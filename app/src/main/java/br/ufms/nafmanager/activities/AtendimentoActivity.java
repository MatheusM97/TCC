package br.ufms.nafmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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

public class AtendimentoActivity extends AppCompatActivity {

    //variáveis inicio
    private TextView tvDataAtendimento;
    private TextView lbDataAtendimento;
    private Date tempoInicioAtendimento;
    private ListView lvAtendimentoTipo;
    private Button btnFinalizarAtendimento;
    private Button btnCancelarAtendimento;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private FirebaseFirestore firebaseFirestore;
    private Date dataInicioAtendimento;

    private List<AtendimentoTipo> atendimentoTipoLista = new ArrayList<AtendimentoTipo>();
    private ArrayAdapter<AtendimentoTipo> atendimentoTipoAdapter;
    private ArrayList<String> atendimentosIds;
    //variáveis fim

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_atendimento);
        lvAtendimentoTipo = (ListView)findViewById(R.id.lv_atendimento_tipo);
        btnFinalizarAtendimento = (Button)findViewById(R.id.btn_finalizarAtendimento);
//        btnCancelarAtendimento = (Button)findViewById(R.id.btn_cancelarAtendimento);

        btnFinalizarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirAtendimento(atendimentosIds);
            }
        });

//        btnCancelarAtendimento.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                retornaTelaPrincipal();
//            }
//        });

        atendimentosIds = new ArrayList<>();
        this.dataInicioAtendimento = new Date();

        instanciarDados();
        instanciarDataInicial();
        carregarAtendimentoTipo();
        iniciarCronometroAtendimento();
    }

    private void iniciarCronometroAtendimento() {
        this.tempoInicioAtendimento = new Date();
    }

    private void instanciarDataInicial() {
        this.tvDataAtendimento = (TextView) findViewById(R.id.tv_dataAtendimentoView);
        this.lbDataAtendimento = (TextView) findViewById(R.id.tv_dtAtendimento);
        this.tvDataAtendimento.setText(sdf.format(new Date()));
    }
    public void abrirDatePicker(View v) {
        DialogFragment newFragment = new customDatePickerFragment(this.tvDataAtendimento);
        newFragment.show(getSupportFragmentManager(), "date picker");
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
                        } else {
                            System.out.println("Erro ao baixar dados: " + task.getException());
                        }
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
                                if(atendimentosIds.contains(at.getId())){
                                    atendimentosIds.remove(at.getId());
                                }
                                else{
                                    atendimentosIds.add(at.getId());
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Não foi possível inserir o registro: " + e);
            }
        });
    }

    private void instanciarDados() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void inserirAtendimento(ArrayList<String> atendimentosIds){
        if(this.atendimentosIds != null && this.atendimentosIds.size() > 0){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
            Date dataAtendimento = new Date();
            try{
                dataAtendimento = sdf.parse(tvDataAtendimento.getText().toString());
            }catch (Exception e){}

            Atendimento atendimento = new Atendimento();
            atendimento.setDataAtendimento(dataAtendimento);
            atendimento.setTempoAtendimento(this.getTempoDeAtendimento());
            atendimento.setAtendimentoTipo(atendimentosIds);

            firebaseFirestore.collection("atendimento").add(atendimento)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AtendimentoActivity.this, "Atendimento salvo com sucesso!", Toast.LENGTH_SHORT).show();
                            limparTudo();
                            retornaTelaPrincipal();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AtendimentoActivity.this, "Falha ao salvar atendimento!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Selecione ao menos um atendimento!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void limparTudo(){
        this.atendimentosIds = new ArrayList<>();
        this.dataInicioAtendimento = new Date();
        for ( int i=0; i < this.lvAtendimentoTipo.getAdapter().getCount(); i++) {
            this.lvAtendimentoTipo.setItemChecked(i, false);
        }
    }

    public Long getTempoDeAtendimento(){
        long diferenca = Math.abs(new Date().getTime() - this.dataInicioAtendimento.getTime());
        return TimeUnit.SECONDS.convert(diferenca, TimeUnit.MILLISECONDS);
    }

    public void retornaTelaPrincipal(){
        this.finish();
    }
}
