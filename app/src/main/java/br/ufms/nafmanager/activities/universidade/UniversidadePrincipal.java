package br.ufms.nafmanager.activities.universidade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;

public class UniversidadePrincipal extends AppCompatActivity {
    private TextView btn_inserirUniversidade;
    private TextView btn_gerenciarUniversidade;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universidade_principal);
        btn_inserirUniversidade = (TextView) findViewById(R.id.btn_inserirUniversidades);
        btn_inserirUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UniversidadeInserir());
            }
        });

        btn_gerenciarUniversidade = (TextView) findViewById(R.id.btn_gerenciarUniversidades);
        btn_gerenciarUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UniversidadeGerenciar());
            }
        });
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
