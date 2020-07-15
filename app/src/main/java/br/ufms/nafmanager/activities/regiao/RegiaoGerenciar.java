package br.ufms.nafmanager.activities.regiao;

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
import br.ufms.nafmanager.adapters.RegiaoAdapter;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.persistencies.Persistencia;

public class RegiaoGerenciar extends CustomActivity {
    private RegiaoAdapter adp;
    Regiao reg = new Regiao();
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
                showDialog();
                reg = (Regiao) parent.getItemAtPosition(position);
                Persistencia.getInstance().carregaRegiaoById(reg.getId());
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
        if(Persistencia.getInstance().carregouRepresentantes){
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
        if(Persistencia.getInstance().carregouRepresentantesRegiao()){
            carregaCad();
        }
        else{
            aguardaResolverNome();
        }
    }

    public void carregaCad(){
        Intent novaIntent = new Intent(getBaseContext(), RegiaoInserir.class);
        novaIntent.putExtra("regiao", Persistencia.getInstance().getRegiaoAtual());
        startActivity(novaIntent);
        editando = true;
        hideDialog();
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
        Acesso acessoLogado = Persistencia.getInstance().getAcessoAtual();

        if(acessoLogado.getNivelAcesso() >= 7L){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            this.marcador = info.position;
            getMenuInflater().inflate(R.menu.lista, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        reg = (Regiao) adp.getObjeto(marcador);
        new AlertDialog.Builder(RegiaoGerenciar.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Alerta!")
                        .setMessage("Tem certeza que deseja remover o item: " + reg.getNome())
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Persistencia.getInstance().validarRemocaoRegiao(reg.getId());
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
                reg.remover();
                adp.remover(marcador);
                Persistencia.getInstance().removerSolicitacoesRegiao(reg.getId());
                adp.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Existem Unidades vinculadas a esta Região Fiscal", Toast.LENGTH_SHORT).show();
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
