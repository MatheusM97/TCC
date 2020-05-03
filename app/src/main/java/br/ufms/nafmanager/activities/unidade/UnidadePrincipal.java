package br.ufms.nafmanager.activities.unidade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UnidadePrincipal extends CustomActivity {

    private TextView btn_inserirUnidade;
    private TextView btn_gerenciarUnidade;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_principal);

        btn_inserirUnidade = (TextView) findViewById(R.id.btn_inserirUnidades);
        btn_inserirUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

                showDialog();

                if(acessoLogado.getNivelAcesso() == 6L){
                    Persistencia.getInstance().carregaRegiaoById(acessoLogado.getRegiaoId());
                }
                else if (acessoLogado.getNivelAcesso() > 6L){
                    Persistencia.getInstance().carregaRegioes();
                }

                aguardandoRegioes();
            }
        });

        btn_gerenciarUnidade = (TextView) findViewById(R.id.btn_gerenciarUnidades);
        btn_gerenciarUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();

                Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();
                if(acessoLogado.getNivelAcesso() == 7L){
                    Persistencia.getInstance().carregaUnidades();
                }else if (acessoLogado.getNivelAcesso() == 6L){
                    Persistencia.getInstance().carregaUnidadesUniversidadesByRegiaoId(acessoLogado.getRegiaoId());
                }else if (acessoLogado.getNivelAcesso() == 5L){
                    Persistencia.getInstance().carregaUnidades(acessoLogado);
                }

                aguardandoUnidades();
            }
        });

        controlaAcesso();
    }

    private void aguardandoRegioes() {
        if(Persistencia.getInstance().carregouRegioes){
            hideDialog();
            iniciarTelas(new UnidadeInserir());
        }
        else{
            aguardandoCarregarRegioes();
        }
    }

    private void aguardandoUnidades() {
        if(Persistencia.getInstance().carregouUnidades){
            hideDialog();
            iniciarTelas(new UnidadeGerenciar());
        }
        else{
            aguardandoCarregarUnidades();
        }
    }

    private void aguardandoCarregarUnidades() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardandoUnidades();
            }
        }, 6000);
    }

    private void aguardandoCarregarRegioes() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardandoRegioes();
            }
        }, 6000);
    }


    private void controlaAcesso() {
        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

        btn_inserirUnidade.setVisibility(View.INVISIBLE);
        if(acessoLogado.getNivelAcesso() >= 6L){
            btn_inserirUnidade.setVisibility(View.VISIBLE);
        }
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
