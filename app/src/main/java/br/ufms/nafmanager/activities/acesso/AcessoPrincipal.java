package br.ufms.nafmanager.activities.acesso;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoPrincipal extends CustomActivity {

    private Button btn_inserirAcesso;
    private Button btn_gerenciarAcesso;
    private Button btn_solicitarAcesso;
    private Button btn_aprovarAcesso;
    
    private boolean edicao = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_principal);

        btn_inserirAcesso = (Button) findViewById(R.id.btn_inserirAcesso);
        btn_inserirAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistencia.getInstance().carregaUsuarios();

//                Acesso ac = Persistencia.getInstance().getAcessoAtual();
//
//                Persistencia.getInstance().carregaUnidadeUniversidadeRegiaoByAcesso(ac);
                Persistencia.getInstance().carregaRegioes();
                Persistencia.getInstance().carregaUnidades();
                Persistencia.getInstance().carregaUniversidades();

                showDialog();
                carregouUsuario();
            }
        });

        btn_gerenciarAcesso = (Button) findViewById(R.id.btn_gerenciarAcesso);
        btn_gerenciarAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Persistencia.getInstance().carregaAcessosLimitado(Persistencia.getInstance().getAcessoAtual());

                showDialog();
                aguardandoUsuariosAcesso();
            }
        });
        
        btn_solicitarAcesso = findViewById(R.id.btn_solicitarAcesso);
        btn_solicitarAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();

                Persistencia.getInstance().carregaRegioes();
                Persistencia.getInstance().carregaUnidades();
                Persistencia.getInstance().carregaUniversidades();

                aguardando();
            }
        });
        
        btn_aprovarAcesso = findViewById(R.id.btn_aprovarAcesso);
        btn_aprovarAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistencia.getInstance().carregaSolicitacoes(Persistencia.getInstance().getAcessoAtual());

                showDialog();
                aguardandoSolicitacoes();
            }
        });
        
        controlaAcesso();
    }
    private void aguardando(){
        if(Persistencia.getInstance().carregouUniversidades && Persistencia.getInstance().carregouUnidades && Persistencia.getInstance().carregouRegioes){
            hideDialog();
            iniciarTelas(new AcessoSolicitar());
        }
        else{
            aguardandoCarregamento();
        }
    }

    private void aguardandoCarregamento() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardando();
            }
        }, 500);
    }

    private void controlaAcesso() {
        btn_inserirAcesso.setVisibility(View.INVISIBLE);
        btn_gerenciarAcesso.setVisibility(View.INVISIBLE);
        btn_aprovarAcesso.setVisibility(View.INVISIBLE);

        btn_solicitarAcesso.setVisibility(View.VISIBLE);

        Acesso acesso = Persistencia.getInstance().getAcessoAtual();
        if(acesso != null){
            if(acesso.getId() != null ){
                if(acesso.getNivelAcesso() >= 2L){
                    btn_inserirAcesso.setVisibility(View.VISIBLE);
                    btn_aprovarAcesso.setVisibility(View.VISIBLE);
                }


                btn_gerenciarAcesso.setVisibility(View.VISIBLE);
            }

            if(acesso.getNivelAcesso() == 7L){
                btn_solicitarAcesso.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void carregouUsuario() {
        if(Persistencia.getInstance().carregouUsuarios &&
                Persistencia.getInstance().carregouUniversidades &&
                Persistencia.getInstance().carregouUnidades &&
                Persistencia.getInstance().carregouRegioes){
            hideDialog();
            iniciarTelas(new AcessoInserir());
        }
        else{
            aguardandoCarregamentoUsuario();
        }
    }

    private void aguardandoUsuariosAcesso() {
        if(Persistencia.getInstance().carregouUsuariosAcesso){
            hideDialog();
            iniciarTelas(new AcessoGerenciar());
        }
        else{
            aguardandoCarregamentoAcessos();
        }
    }

    private void aguardandoUniversidades(){
        if(Persistencia.getInstance().carregouUniversidades){
            hideDialog();
            iniciarTelas(new AcessoSolicitar());
        }
        else{
            aguardandoCarregamentoUniversidades();
        }
    }

    private void aguardandoSolicitacoes(){
        if(Persistencia.getInstance().carregouUsuariosAcesso){
            hideDialog();
            iniciarTelas(new AcessoAprovar());
        }
        else{
            aguardandoCarregamentoSolicitacoes();
        }
    }

    private void aguardandoCarregamentoUsuario() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregouUsuario();
            }
        }, 500);
    }

    private void aguardandoCarregamentoAcessos() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardandoUsuariosAcesso();
            }
        }, 500);
    }

    private void aguardandoCarregamentoUniversidades() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardandoUniversidades();
            }
        }, 500);
    }

    private void aguardandoCarregamentoSolicitacoes() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                aguardandoSolicitacoes();
            }
        }, 500);
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}