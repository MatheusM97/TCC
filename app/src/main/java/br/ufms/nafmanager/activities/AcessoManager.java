package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoManager extends AppCompatActivity {

    private Button btn_inserirAcesso;
    private Button btn_vincularAcesso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Persistencia.getInstance();
        setContentView(R.layout.acesso_manager);

        btn_inserirAcesso = (Button) findViewById(R.id.btn_inserirAcesso);
        btn_vincularAcesso = (Button) findViewById(R.id.btn_vincularAcesso);

        btn_inserirAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AcessoInserir());
            }
        });

        btn_vincularAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AcessoVincular());
            }
        });
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}