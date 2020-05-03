package br.ufms.nafmanager.activities.regiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.persistencies.Persistencia;

public class RegiaoPrincipal extends CustomActivity {

    private TextView btn_inserirRegiao;
    private TextView btn_gerenciarRegiao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regiao_principal);

        btn_inserirRegiao = (TextView) findViewById(R.id.btn_inserirRegioes);
        btn_inserirRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new RegiaoInserir());
            }
        });

        btn_gerenciarRegiao = (TextView) findViewById(R.id.btn_gerenciarRegioes);
        btn_gerenciarRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

                if(acessoLogado.getNivelAcesso() == 7L){
                    Persistencia.getInstance().carregaRegioes();
                }
                else if(acessoLogado.getNivelAcesso() == 6L){
                        Persistencia.getInstance().carregaRegiaoById(acessoLogado.getRegiaoId());
                }

                aguardandoRegioes();
            }
        });

        controlaAcesso();
    }

    private void controlaAcesso() {
        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

        btn_inserirRegiao.setVisibility(View.INVISIBLE);
        if(acessoLogado.getNivelAcesso() >= 7L){
            btn_inserirRegiao.setVisibility(View.VISIBLE);
        }
    }

    private void aguardandoRegioes() {
        if(Persistencia.getInstance().carregouRegioes){
            hideDialog();
            iniciarTelas(new RegiaoGerenciar());
        }
        else{
            aguardandoCarregarRegioes();
        }
    }

    private void aguardandoCarregarRegioes() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardandoRegioes();
            }
        }, 6000);
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}

