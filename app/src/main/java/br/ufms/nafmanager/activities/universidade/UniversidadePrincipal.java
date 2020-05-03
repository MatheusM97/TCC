package br.ufms.nafmanager.activities.universidade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.AcessoTipoEnum;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UniversidadePrincipal extends CustomActivity {
    private TextView btn_inserirUniversidade;
    private TextView btn_gerenciarUniversidade;
    private Acesso acessoLogado = new Acesso();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universidade_principal);

        acessoLogado = Persistencia.getInstance().getAcessoAtual();
        btn_inserirUniversidade = (TextView) findViewById(R.id.btn_inserirUniversidades);
        btn_inserirUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistencia.getInstance().carregaUnidadeUniversidadeRegiaoByAcesso(acessoLogado);
                showDialog();

                carregandoInserir();
            }
        });

        btn_gerenciarUniversidade = (TextView) findViewById(R.id.btn_gerenciarUniversidades);
        btn_gerenciarUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistencia.getInstance().carregaUnidadeUniversidadeRegiaoByAcesso(acessoLogado);

                showDialog();
                carregandoGerenciar();
            }
        });

        btn_inserirUniversidade.setVisibility(View.INVISIBLE);
        if(acessoLogado != null){
            if(acessoLogado.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor()) || acessoLogado.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor()) ){
                if(acessoLogado.isRepresentante() || acessoLogado.isModerador() ){
                  btn_inserirUniversidade.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void carregandoInserir(){
        if(Persistencia.getInstance().carregouUniversidades){
            hideDialog();
            iniciarTelas(new UniversidadeInserir());
        }
        else{
            aguardandoInserir();
        }
    }

    private void carregandoGerenciar(){
        if(Persistencia.getInstance().carregouUniversidades){
            hideDialog();
            iniciarTelas(new UniversidadeGerenciar());
        }
        else{
            aguardandoGerenciar();
        }
    }

    private void aguardandoInserir() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregandoInserir();
            }
        }, 500);
    }

    private void aguardandoGerenciar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregandoGerenciar();
            }
        }, 500);
    }


    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
