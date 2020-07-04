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
import android.widget.Toast;

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
    private Universidade unv;
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
                showDialog();
                universidade = (Universidade) parent.getItemAtPosition(position);
                Persistencia.getInstance().carregaUniversidadeById(universidade.getId());
                Persistencia.getInstance().carregaUnidadeById(universidade.getUnidadeId());
                aguardaRepresentante();
            }
        });

        registerForContextMenu(list);
    }

    private void aguardaRepresentante() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregouRepresentante();
            }
        }, 500);
    }

    private void carregouRepresentante(){
        if(Persistencia.getInstance().carregouRepresentantes && Persistencia.getInstance().carregouUnidades){
            aguardaResolverNome();
        }
        else{
            aguardaRepresentante();
        }
    }

    private void aguardaResolverNome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                carregouNome();
            }
        }, 100);
    }

    private void carregouNome(){
        if(Persistencia.getInstance().carregouRepresentantesUniversidade()){
            carregaCad();
        }
        else{
            aguardaResolverNome();
        }
    }

    private void carregaCad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent novaIntent = new Intent(getBaseContext(), UniversidadeInserir.class);
                novaIntent.putExtra("universidade", Persistencia.getInstance().getUniversidadeAtual());
                startActivity(novaIntent);
                editando = true;
                hideDialog();
            }
        }, 500);
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
        unv = (Universidade) adp.getObjeto(marcador);
        new AlertDialog.Builder(UniversidadeGerenciar.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Alerta!")
                .setMessage("Tem certeza que deseja remover o item: " + unv.getNome())
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Persistencia.getInstance().validarRemocaoUniversidade(unv.getId());
                        aguardandoValidacao();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
        return true;
    }

    public void validou(){
        if(Persistencia.getInstance().isVerificouExclusao()){
            if(Persistencia.getInstance().isPodeExcluir()){
                unv.remover();
                adp.remover(marcador);
                adp.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Existem Participantes vinculadas a esta Universidade", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            aguardandoValidacao();
        }
    }

    private void aguardandoValidacao() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                validou();
            }
        }, 500);
    }
}
