package br.ufms.nafmanager.activities.regiao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.adapters.RegiaoAdapter;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.persistencies.Persistencia;

public class RegiaoGerenciar extends CustomActivity {
    private Regiao regiao;
    private RegiaoAdapter adp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);
        carregaLista();
    }

    public void carregaLista(){
        adp = new RegiaoAdapter(this, Persistencia.getInstance().getRegioes());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Persistencia.getInstance().setRegiaoAtual((Regiao) parent.getItemAtPosition(position));
                carregaCad();
            }
        });

        registerForContextMenu(list);
    }

    public void carregaCad(){
        Intent novaIntent = new Intent(getBaseContext(), RegiaoInserir.class);
        novaIntent.putExtra("regiao", Persistencia.getInstance().getRegiaoAtual());
        startActivity(novaIntent);
        editando = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(editando){
            adp.atualizarObjeto(Persistencia.getInstance().getRegiaoAtual());
            adp.notifyDataSetChanged();
            editando = !editando;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        this.marcador = info.position;
        getMenuInflater().inflate(R.menu.lista, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        final Regiao reg = (Regiao) adp.getObjeto(marcador);
        new AlertDialog.Builder(RegiaoGerenciar.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Alerta!")
                        .setMessage("Tem certeza que deseja remover o item: " + reg.getNome())
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(reg.remover()) {
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
