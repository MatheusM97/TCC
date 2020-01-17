package br.ufms.nafmanager.activities.usuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.adapters.UsuarioAdapter;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UsuarioGerenciar extends AppCompatActivity {
    private Usuario usuario;
    private ProgressDialog progressDialog;
    private UsuarioAdapter adp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);

        adp = new UsuarioAdapter(this, Persistencia.getInstance().getUsuarios());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usuario = (Usuario) parent.getItemAtPosition(position);
                Persistencia.getInstance().carregaUsuarioById(usuario.getId());

                showDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent novaIntent = new Intent(getBaseContext(), UsuarioInserir.class);
                        novaIntent.putExtra("usuario", Persistencia.getInstance().getUsuarioAtual());
                        startActivity(novaIntent);
                        progressDialog.dismiss();
                    }
                }, 6000);
            }
        });
    }

    public void showDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setCancelable(false);
        this.progressDialog.setContentView(R.layout.layout_carregando);
        this.progressDialog.show();
    }
}
