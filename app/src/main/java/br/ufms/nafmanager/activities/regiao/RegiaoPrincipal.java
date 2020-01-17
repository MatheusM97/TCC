package br.ufms.nafmanager.activities.regiao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
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
                Persistencia.getInstance().carregaRegioes();

                showDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideDialog();
                        iniciarTelas(new RegiaoGerenciar());
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

