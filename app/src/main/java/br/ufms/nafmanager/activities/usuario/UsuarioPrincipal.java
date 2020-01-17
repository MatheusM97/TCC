package br.ufms.nafmanager.activities.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;

public class UsuarioPrincipal extends AppCompatActivity {

    private TextView btn_inserirUsuario;
    private TextView btn_gerenciarUsuario;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario_principal);
        btn_inserirUsuario = (TextView) findViewById(R.id.btn_inserirUsuario);
        btn_inserirUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UsuarioInserir());
            }
        });

        btn_gerenciarUsuario = (TextView) findViewById(R.id.btn_gerenciarUsuario);
        btn_gerenciarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarTelas(new UsuarioGerenciar());
            }
        });
    }

    public void iniciarTelas(Object obj) {
        Intent novaIntent = new Intent(getBaseContext(), obj.getClass());
        startActivity(novaIntent);
    }
}
