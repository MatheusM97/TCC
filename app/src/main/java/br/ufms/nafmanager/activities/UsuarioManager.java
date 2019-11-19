package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;

public class UsuarioManager extends AppCompatActivity {

    private TextView btn_inserirUsuario;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_manager);
        btn_inserirUsuario = (TextView) findViewById(R.id.btn_inserirUsuario);
        btn_inserirUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UsuarioInserir());
            }
        });
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
