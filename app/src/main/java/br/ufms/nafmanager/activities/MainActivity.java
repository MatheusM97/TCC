package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.regiao.RegiaoPrincipal;
import br.ufms.nafmanager.activities.unidade.UnidadePrincipal;
import br.ufms.nafmanager.activities.universidade.UniversidadePrincipal;
import br.ufms.nafmanager.activities.usuario.UsuarioPrincipal;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.persistencies.Persistencia;

public class MainActivity extends AppCompatActivity {

    private String usuarioId;
    private String usuarioNome;
    private String unidadeId;
    private String unidadeNome;
    private String universidadeId;
    private String universidadeNome;
    private String acessoId;

    private TextView btn_regiaoManager;
    private TextView btn_unidadeManager;
    private TextView btn_usuarioManager;
    private TextView btn_universidadeManager;
//    private TextView btn_iniciarAtendimento;
//    private TextView btn_acessoManager;
    private Acesso acessoAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString("acessoId") != null)
                this.acessoId = extras.getString("acessoId");

            if (extras.getString("usuarioId") != null)
                this.usuarioId = extras.getString("usuarioId");

            if (extras.getString("usuarioNome") != null)
                this.usuarioNome = extras.getString("usuarioNome");

            if (extras.getString("universidadeId") != null)
                this.universidadeId = extras.getString("universidadeId");

            if (extras.getString("universidadeNome") != null)
                this.universidadeNome = extras.getString("universidadeNome");

            if (extras.getString("unidadeId") != null)
                this.unidadeId = extras.getString("unidadeId");

            if (extras.getString("unidadeNome") != null)
                this.unidadeNome = extras.getString("unidadeNome");
        }

//        acessoAtual = new Acesso();
//        acessoAtual = Persistencia.getInstance().getAcessoAtual();

        Persistencia.getInstance().carregaAtendidos();


        btn_unidadeManager = (TextView) findViewById(R.id.btn_unidadeManager);
        btn_unidadeManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UnidadePrincipal());
            }
        });

        btn_universidadeManager = (TextView) findViewById(R.id.btn_universidadeManager);
        btn_universidadeManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UniversidadePrincipal());
            }
        });

        btn_usuarioManager = (TextView) findViewById(R.id.btn_usuarioManager);
        btn_usuarioManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UsuarioPrincipal());
            }
        });

        btn_regiaoManager = (TextView) findViewById(R.id.btn_regiaoManager);
        btn_regiaoManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new RegiaoPrincipal());
            }
        });

//        btn_acessoManager = (TextView) findViewById(R.id.btn_acessoManager);
//        btn_acessoManager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                iniciarTelas(new AcessoManager());
//            }
//        });

//        btn_iniciarAtendimento = (TextView) findViewById(R.id.btn_realizarAtendimento);
//        btn_iniciarAtendimento.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                iniciarTelas(new AtendimentoActivity());
//            }
//        });

//        if(Persistencia.getInstance().getVersao() > 0){
//            btn_acessoManager.setVisibility(View.INVISIBLE);
//            btn_iniciarAtendimento.setVisibility(View.INVISIBLE);
//            btn_usuarioManager.setVisibility(View.INVISIBLE);
//            btn_universidadeManager.setVisibility(View.INVISIBLE);
//            btn_unidadeManager.setVisibility(View.INVISIBLE);
//
//            if (acessoAtual.isParticipante() || acessoAtual.isSupervisor() || acessoAtual.isCoordenador() || acessoAtual.isRepresentante()) {
//                btn_iniciarAtendimento.setVisibility(View.VISIBLE);
//                btn_usuarioManager.setVisibility(View.VISIBLE);
//            }
//
//            if (acessoAtual.isCoordenador() || acessoAtual.isRepresentante()) {
//                btn_universidadeManager.setVisibility(View.VISIBLE);
//            }
//
//            if (acessoAtual.isRepresentante()) {
//                btn_unidadeManager.setVisibility(View.VISIBLE);
//            }
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Persistencia.getInstance().carregaUniversidades();
//        Persistencia.getInstance().carregaUnidades();
//        Persistencia.getInstance().carregaUsuarios();
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        novaIntent.putExtra("usuarioNome", this.usuarioNome);
        novaIntent.putExtra("usuarioId", this.usuarioId);
        novaIntent.putExtra("unidadeId", this.unidadeId);
        novaIntent.putExtra("unidadeNome", this.unidadeNome);
        novaIntent.putExtra("universidadeId", this.universidadeId);
        novaIntent.putExtra("universidadeNome", this.universidadeNome);
        startActivity(novaIntent);
    }
}
