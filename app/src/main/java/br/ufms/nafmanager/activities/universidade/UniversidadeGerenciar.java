package br.ufms.nafmanager.activities.universidade;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.adapters.UniversidadeAdapter;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UniversidadeGerenciar extends AppCompatActivity {
    private Universidade universidade;
    private ProgressDialog progressDialog;
    private UniversidadeAdapter adp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);

        adp = new UniversidadeAdapter(this, Persistencia.getInstance().getUniversidades());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                universidade = (Universidade) parent.getItemAtPosition(position);
                Persistencia.getInstance().carregaUniversidadeById(universidade.getId());

                showDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent novaIntent = new Intent(getBaseContext(), UniversidadeInserir.class);
                        novaIntent.putExtra("universidade", Persistencia.getInstance().getUniversidadeAtual());
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
