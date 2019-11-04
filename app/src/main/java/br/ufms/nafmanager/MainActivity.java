package br.ufms.nafmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckedTextView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;

public class MainActivity extends AppCompatActivity {

    private ListView lvAtendimentoTipo;
    private Button btnFinalizarAtendimento;

    FirebaseFirestore firebaseFirestore;
    Date dataInicioAtendimento;
    Date dataFimAtendimento;

    private List<AtendimentoTipo> atendimentoTipoLista = new ArrayList<AtendimentoTipo>();
    private ArrayAdapter<AtendimentoTipo> atendimentoTipoAdapter;
    private ArrayList<String> atendimentosIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_interview);
        lvAtendimentoTipo = (ListView)findViewById(R.id.lv_atendimento_tipo);
        btnFinalizarAtendimento = (Button)findViewById(R.id.btn_finalizarAtendimento);
        btnFinalizarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserirAtendimento(atendimentosIds);
                limparTudo();
            }
        });
        atendimentosIds = new ArrayList<>();
        this.dataInicioAtendimento = new Date();

        instanciarDados();
        carregarAtendimentoTipo();
    }

    private void carregarAtendimentoTipo() {
        firebaseFirestore.collection("atendimento_tipo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AtendimentoTipo atendimentoTipo = document.toObject(AtendimentoTipo.class);
                                atendimentoTipo.setId(document.getId());
                                atendimentoTipoLista.add(atendimentoTipo);
                                count ++;

                                if(count == 5)
                                    break;
                            }
                        } else {
                            System.out.println("Error getting documents: " + task.getException());
                        }
                        atendimentoTipoAdapter = new ArrayAdapter<AtendimentoTipo>
                                (MainActivity.this,
                                        R.layout.layout_atendimento_tipo, R.id.ctvAtendimentoTipo,
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
                });
    }

    private void instanciarDados() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void inserirAtendimento(ArrayList<String> atendimentosIds){
        this.dataFimAtendimento = new Date();
        Long tempoFim = this.dataFimAtendimento.getTime() - this.dataInicioAtendimento.getTime();
        Atendimento atendimento = new Atendimento();
        atendimento.setDataAtendimento(new Date());
        atendimento.setTempoFinalizacao(tempoFim);
        atendimento.setAtendimentoTipo(atendimentosIds);

        firebaseFirestore.collection("atendimento").add(atendimento)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("Sucesso ao gravar atendimento!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Falha ao gravar atendimento!");
                    }
                });
    }

    private void limparTudo(){
        this.atendimentoTipoLista = new ArrayList<>();
        this.dataInicioAtendimento = new Date();
        this.carregarAtendimentoTipo();
    }
}
