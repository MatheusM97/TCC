package br.ufms.nafmanager.activities.unidade;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
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
                Persistencia.getInstance().carregaRegioes();
                showDialog();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        iniciarTelas(new UnidadeInserir());
                    }
                }, 3000);
            }
        });

        btn_gerenciarUnidade = (TextView) findViewById(R.id.btn_gerenciarUnidades);
        btn_gerenciarUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Persistencia.getInstance().carregaUnidades();

                showDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        iniciarTelas(new UnidadeGerenciar());
                    }
                }, 6000);
            }
        });
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
