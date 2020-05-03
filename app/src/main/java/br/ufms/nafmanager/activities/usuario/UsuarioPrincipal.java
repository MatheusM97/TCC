package br.ufms.nafmanager.activities.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UsuarioPrincipal extends CustomActivity {

    private TextView btn_inserirUsuario;
    private TextView btn_gerenciarUsuario;
    private Acesso acesso;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_principal);

        acesso = Persistencia.getInstance().getAcessoAtual();

        btn_inserirUsuario = (TextView) findViewById(R.id.btn_inserirUsuario);
        btn_inserirUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog();
                iniciarTelas(new UsuarioInserir());
            }
        });

        btn_gerenciarUsuario = (TextView) findViewById(R.id.btn_gerenciarUsuario);
        btn_gerenciarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(acesso != null){
                    Persistencia.getInstance().carregaUsuarioById(acesso.getUsuarioId());
                }
                else if (Persistencia.getInstance().getUsuarioAtual()!= null){
                    Persistencia.getInstance().carregaUsuarioById(Persistencia.getInstance().getUsuarioAtual().getId());
                }

                showDialog();
                aguardandoUsuariosAcesso();
            }
        });

        controlaAcesso();
    }

    private void aguardandoUsuariosAcesso() {
        if(Persistencia.getInstance().carregouUsuariosAcesso){
            hideDialog();
            iniciarTelas(new UsuarioGerenciar());
        }
        else{
            aguardandoCarregamentoAcessos();
        }
    }

    private void aguardandoUniversidades() {
        if(Persistencia.getInstance().carregouUniversidades){
            hideDialog();
            iniciarTelas(new UsuarioInserir());
        }
        else{
            aguardandoCarregamentoUniversidades();
        }
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

    private void controlaAcesso() {
        btn_inserirUsuario.setVisibility(View.INVISIBLE);

        if(acesso != null){
            if(acesso.getNivelAcesso() >= 2L){
                btn_inserirUsuario.setVisibility(View.VISIBLE);
            }
        }
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
