package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;

public class UniversidadeManager extends AppCompatActivity {
    private TextView btn_inserirUniversidade;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universidade_manager);
        btn_inserirUniversidade = (TextView) findViewById(R.id.btn_universidadeInserir);
        btn_inserirUniversidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UniversidadeInserir());
            }
        });
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
