package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.acesso.AcessoPrincipal;
import br.ufms.nafmanager.activities.atendimento.AtendimentoActivity;
import br.ufms.nafmanager.activities.regiao.RegiaoPrincipal;
import br.ufms.nafmanager.activities.unidade.UnidadePrincipal;
import br.ufms.nafmanager.activities.universidade.UniversidadePrincipal;
import br.ufms.nafmanager.activities.usuario.UsuarioPrincipal;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.AcessoTipoEnum;
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
    private TextView btn_acessoManager;
    private TextView btn_atendimento;
    private TextView btn_sair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        Persistencia.getInstance().Iniciar();

        carregaComponentes();

        controlaAcesso();
    }

    private void carregaComponentes() {
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

        btn_acessoManager = (TextView) findViewById(R.id.btn_acessoManager);
        btn_acessoManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AcessoPrincipal());
            }
        });

        btn_atendimento = findViewById(R.id.btn_atendimento);
        btn_atendimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AtendimentoActivity());
            }
        });

        btn_sair = (TextView) findViewById(R.id.btn_sair);
        btn_sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void controlaAcesso() {
        btn_regiaoManager.setVisibility(View.INVISIBLE);
        btn_unidadeManager.setVisibility(View.INVISIBLE);
        btn_universidadeManager.setVisibility(View.INVISIBLE);
        btn_atendimento.setVisibility(View.INVISIBLE);
        btn_acessoManager.setVisibility(View.VISIBLE);
        btn_usuarioManager.setVisibility(View.VISIBLE);

        Acesso acesso = Persistencia.getInstance().getAcessoAtual();

        if(acesso != null){
            if((acesso.isModerador() || acesso.isRepresentante()) && acesso.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())){
                btn_regiaoManager.setVisibility(View.VISIBLE);
                btn_unidadeManager.setVisibility(View.VISIBLE);
                btn_universidadeManager.setVisibility(View.VISIBLE);
            }
            else if(acesso.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor()) && acesso.isRepresentante()){
                btn_unidadeManager.setVisibility(View.VISIBLE);
                btn_universidadeManager.setVisibility(View.VISIBLE);
            }else if (acesso.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor()) && (acesso.isRepresentante())) {
                btn_universidadeManager.setVisibility(View.VISIBLE);
            }else if (acesso.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor()) && (acesso.isProfessor() || acesso.isAluno())){
                btn_usuarioManager.setVisibility(View.VISIBLE);
            }

            if(acesso.isProfessor() || acesso.isAluno()){
                btn_atendimento.setVisibility(View.VISIBLE);
            }
        }
        else{
            btn_acessoManager.setVisibility(View.VISIBLE);
            btn_usuarioManager.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
