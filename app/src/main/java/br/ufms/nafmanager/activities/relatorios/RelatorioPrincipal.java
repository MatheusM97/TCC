package br.ufms.nafmanager.activities.relatorios;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.persistencies.Persistencia;

public class RelatorioPrincipal extends CustomActivity {

    private Button relatorioParticipante;
    private Button relatorioTempoMedio;
    private Button relatorioEstatisticasCadastrais;
    private Button relatorioRepresentanteUniversidade;
    private Button relatorioRepresentanteUnidade;
    private Button relatorioRepresentanteRegiao;
    private Button relatorioRanking;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_principal);

        vinculaComponentes();
        controlaAcesso();
    }

    private void controlaAcesso() {
        relatorioRepresentanteUniversidade.setVisibility(View.INVISIBLE);
        relatorioRepresentanteUnidade.setVisibility(View.INVISIBLE);
        relatorioRepresentanteRegiao.setVisibility(View.INVISIBLE);
        relatorioParticipante.setVisibility(View.INVISIBLE);

        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();
        if(acessoLogado!= null && acessoLogado.getNivelAcesso() >= 4L){
            relatorioRepresentanteUniversidade.setVisibility(View.VISIBLE);

            if(acessoLogado.getNivelAcesso() >= 5L){
                relatorioRepresentanteUnidade.setVisibility(View.VISIBLE);
            }

            if(acessoLogado.getNivelAcesso() >= 6L){
                relatorioRepresentanteRegiao.setVisibility(View.VISIBLE);
            }
        }
        else if(acessoLogado!= null && acessoLogado.getNivelAcesso() <= 2L){
            relatorioParticipante.setVisibility(View.VISIBLE);
        }
    }

    private void vinculaComponentes() {
        relatorioParticipante = findViewById(R.id.btn_relatorioParticipante);
        relatorioParticipante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new RelatorioParticipante());
            }
        });

        relatorioTempoMedio = findViewById(R.id.btn_relatorioTempoMedio);
        relatorioTempoMedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistencia.getInstance().carregaUniversidades();
                showDialog();
                carregandoUniversidades();
            }
        });

        relatorioEstatisticasCadastrais = findViewById(R.id.btn_relatorioCadastro);
        relatorioEstatisticasCadastrais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistencia.getInstance().carregaRegioes();
                showDialog();
                carregandoFiltros();
            }
        });

        relatorioRepresentanteUniversidade = findViewById(R.id.btn_relatorioRepresentanteUniversidade);
        relatorioRepresentanteUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

                Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();
                Persistencia.getInstance().carregaUnidadeUniversidadeRegiaoByAcesso(acessoLogado);

                aguardandoCarregarUniversidades();
            }
        });

        relatorioRepresentanteUnidade = findViewById(R.id.btn_relatorioRepresentanteUnidade);
        relatorioRepresentanteUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();
                if(acessoLogado.getNivelAcesso() >= 5L){
                    showDialog();
                    Persistencia.getInstance().carregaUnidades(acessoLogado);
                    carregouUnidades();
                }
            }
        });


        relatorioRepresentanteRegiao = findViewById(R.id.btn_relatorioRepresentanteRegiao);
        relatorioRepresentanteRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

                Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();
                if(acessoLogado.getNivelAcesso() == 6L){
                    Persistencia.getInstance().carregaRegiaoById(acessoLogado.getRegiaoId());
                }
                else if(acessoLogado.getNivelAcesso() == 7L){
                    Persistencia.getInstance().carregaRegioes();
                }

                carregouRegioes();
            }
        });

        relatorioRanking = findViewById(R.id.btn_relatorioRanking);
        relatorioRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                Persistencia.getInstance().getAtendimentoTipoLocal();
                Persistencia.getInstance().carregaRegioes();
                Persistencia.getInstance().carregaUnidades();
                Persistencia.getInstance().carregaUniversidades();
                Persistencia.getInstance().carregaParticipantes();
                carregouRanking();
            }
        });
    }

    private void carregouRanking() {
        if(Persistencia.getInstance().carregouRegioes &&
           Persistencia.getInstance().carregouUnidades &&
           Persistencia.getInstance().carregouUniversidades &&
           Persistencia.getInstance().isParticipantesRelatorioCarregado() &&
           Persistencia.getInstance().traduziuNomesParticipantes()){
            hideDialog();
            iniciarTelas(new RelatorioRanking());
        }
        else{
            aguardandoCarregarRanking();
        }
    }

    private void aguardandoCarregarRanking() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregouRanking();
            }
        }, 300);
    }

    private void carregouUniversidades() {
        if(Persistencia.getInstance().carregouAcessosPossiveis && Persistencia.getInstance().carregouUniversidades){
            hideDialog();
            iniciarTelas(new RelatorioRepresentanteUniversidade());
        }
        else{
            aguardandoCarregarUniversidades();
        }
    }

    private void aguardandoCarregarUniversidades() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregouUniversidades();
            }
        }, 200);
    }


    private void carregandoFiltros() {
        if(Persistencia.getInstance().carregouRegioes){
            hideDialog();
            iniciarTelas(new RelatorioCadastro());
        }
        else{
            aguardandoFiltros();
        }
    }

    private void carregandoUniversidades() {
        if(Persistencia.getInstance().carregouUniversidades){
            hideDialog();
            iniciarTelas(new RelatorioTempoMedio());
        }
        else{
            aguardando();
        }
    }

    private void aguardando() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregandoUniversidades();
            }
        }, 500);
    }

    private void aguardandoFiltros() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregandoFiltros();
            }
        }, 500);
    }

    private void carregouUnidades() {
        if(Persistencia.getInstance().carregouUnidades){
            hideDialog();
            iniciarTelas(new RelatorioRepresentanteUnidade());
        }
        else{
            aguardandoCarregarUnidades();
        }
    }

    private void aguardandoCarregarUnidades() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregouUnidades();
            }
        }, 200);
    }

    private void carregouRegioes(){
        if(Persistencia.getInstance().carregouRegioes){
            hideDialog();
            iniciarTelas(new RelatorioRepresentanteRegiao());
        }
        else{
            aguardandoCarregarRegioes();
        }
    }
    private void aguardandoCarregarRegioes() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregouRegioes();
            }
        }, 200);
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
