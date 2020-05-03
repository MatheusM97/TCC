package br.ufms.nafmanager.activities.universidade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.adapters.UniversidadeAdapter;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UniversidadeGerenciar extends CustomActivity {
    private Universidade universidade;
    private UniversidadeAdapter adp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);
        carregaLista();
    }

    private void carregaLista() {
        adp = new UniversidadeAdapter(this, Persistencia.getInstance().getUniversidades());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                universidade = (Universidade) parent.getItemAtPosition(position);
                Persistencia.getInstance().carregaUniversidadeById(universidade.getId());
                carregaCad();
            }
        });

        registerForContextMenu(list);
    }

    private void carregaCad() {
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent novaIntent = new Intent(getBaseContext(), UniversidadeInserir.class);
                novaIntent.putExtra("universidade", Persistencia.getInstance().getUniversidadeAtual());
                startActivity(novaIntent);
                editando = true;
                hideDialog();
            }
        }, 6000);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(editando){
            adp.atualizarObjeto(Persistencia.getInstance().getUniversidadeAtual());
            adp.notifyDataSetChanged();
            editando = !editando;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

        if(acessoLogado.getNivelAcesso() >= 5L){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            this.marcador = info.position;
            getMenuInflater().inflate(R.menu.lista, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        final Universidade unv = (Universidade) adp.getObjeto(marcador);
        new AlertDialog.Builder(UniversidadeGerenciar.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Alerta!")
                .setMessage("Tem certeza que deseja remover o item: " + unv.getNome())
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(unv.remover()) {
                            adp.remover(marcador);
                            adp.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Não", null)
                .show();
        return true;
    }
}
