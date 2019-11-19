package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.persistencies.Persistencia;

public class MainActivity extends AppCompatActivity {

    //variáveis inicio

    private String usuarioId;
    private String usuarioNome;
    private String unidadeId;
    private String unidadeNome;
    private String universidadeId;
    private String universidadeNome;

    private TextView btn_unidadeManager;
    private TextView btn_usuarioManager;
    private TextView btn_universidadeManager;
    private TextView btn_iniciarAtendimento;
    private TextView btn_acessoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Persistencia.getInstance().carregaUniversidades();
        Persistencia.getInstance().carregaUnidades();

        btn_unidadeManager = (TextView) findViewById(R.id.btn_unidadeManager);
        btn_unidadeManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UnidadeManager());
            }
        });

        btn_universidadeManager = (TextView) findViewById(R.id.btn_universidadeManager);
        btn_universidadeManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UniversidadeManager());
            }
        });

        btn_usuarioManager = (TextView) findViewById(R.id.btn_usuarioManager);
        btn_usuarioManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UsuarioManager());
            }
        });

        btn_iniciarAtendimento = (TextView) findViewById(R.id.btn_realizarAtendimento);
        btn_iniciarAtendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AtendimentoActivity());
            }
        });

        btn_acessoManager = (TextView) findViewById(R.id.btn_acessoManager);
        btn_acessoManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AcessoManager());
            }
        });
    }

    public void iniciarTelas(Object obj){
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        novaIntent.putExtra("usuarioNome", this.usuarioNome);
        novaIntent.putExtra("usuarioId", this.usuarioId);
        novaIntent.putExtra("unidadeId", this.unidadeId);
        novaIntent.putExtra("unidadeNome",  this.unidadeNome);
        novaIntent.putExtra("universidadeId", this.universidadeId);
        novaIntent.putExtra("universidadeNome", this.universidadeNome);
        startActivity(novaIntent);
    }
}
