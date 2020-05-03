package br.ufms.nafmanager.activities.unidade;

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
import br.ufms.nafmanager.adapters.UnidadeAdapter;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UnidadeGerenciar extends CustomActivity {
    private Unidade unidade;
    private UnidadeAdapter adp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);
        carregaLista();
    }

    public void carregaLista(){
        adp = new UnidadeAdapter(this, Persistencia.getInstance().getUnidades());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                unidade = (Unidade) parent.getItemAtPosition(position);
                Persistencia.getInstance().carregaUnidadeById(unidade.getId());
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
                Intent novaIntent = new Intent(getBaseContext(), UnidadeInserir.class);
                novaIntent.putExtra("unidade", Persistencia.getInstance().getUnidadeAtual());
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
            adp.atualizarObjeto(Persistencia.getInstance().getUnidadeAtual());
            adp.notifyDataSetChanged();
            editando = !editando;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

        if(acessoLogado.getNivelAcesso() >= 6L){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            this.marcador = info.position;
            getMenuInflater().inflate(R.menu.lista, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        final Unidade und = (Unidade) adp.getObjeto(marcador);
        new AlertDialog.Builder(UnidadeGerenciar.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Alerta!")
                .setMessage("Tem certeza que deseja remover o item: " + und.getNome())
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(und.remover()) {
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
