package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;

public class AcessoManager extends AppCompatActivity {

    private Button btn_inserirAcesso;
    private Button btn_vincularAcesso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acesso_manager);
        btn_inserirAcesso = (Button) findViewById(R.id.btn_inserirAcesso);
        btn_vincularAcesso = (Button) findViewById(R.id.btn_editar_acesso);

        btn_inserirAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AcessoInserir());
            }
        });

        btn_vincularAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new AcessoEditar());
            }
        });
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}