package br.ufms.nafmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.persistencies.Persistencia;

public class MainActivity extends AppCompatActivity {

    //variáveis inicio
    private TextView btn_inserirUnidade;
    private TextView btn_inserirUsuario;
    private TextView btn_iniciarAtendimento;
//    public Persistencia persistencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Persistencia.getInstance();

        btn_inserirUnidade = (TextView) findViewById(R.id.btn_inserirUnidade);
        btn_inserirUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UnidadeActivity());
            }
        });

        btn_inserirUsuario = (TextView) findViewById(R.id.btn_inserirUsuario);
        btn_inserirUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UsuarioActivity());
            }
        });
        btn_iniciarAtendimento = (TextView) findViewById(R.id.btn_realizarAtendimento);
        btn_iniciarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AtendimentoActivity());
            }
        });
    }

    public void iniciarTelas(Object obj){
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        novaIntent.putExtra("usuarioLogadoNome", "André Furlan");
        novaIntent.putExtra("usuarioLogadoId", "eYNKzbkH3Mv80w8aIk0L");
        novaIntent.putExtra("unidadeId", "p6Jpyb2t9lHb0TR9cRL7");
        novaIntent.putExtra("unidadeNome", "Delegacia Regional");
        novaIntent.putExtra("universidadeId", "72QutgZ0uK5P3zJPdtRi");
        novaIntent.putExtra("universidadeNome", "UFMS - Universidade Federal do Mato Grosso do Sul");
        startActivity(novaIntent);
    }
}
