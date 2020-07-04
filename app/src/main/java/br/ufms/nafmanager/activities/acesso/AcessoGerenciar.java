package br.ufms.nafmanager.activities.acesso;

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
import br.ufms.nafmanager.adapters.AcessoUsuarioAdapter;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoGerenciar extends CustomActivity {
    private AcessoUsuarioAdapter adp;
    private Acesso ac;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);
        carregaLista();
    }

    public void carregaLista(){
        adp = new AcessoUsuarioAdapter(this, Persistencia.getInstance().getAcessosRegistrados());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Persistencia.getInstance().setAcessoCarregado((Acesso) parent.getItemAtPosition(position));
                carregaCad();
            }
        });

        registerForContextMenu(list);
    }

    public void carregaCad(){
        Intent novaIntent = new Intent(getBaseContext(), AcessoInserir.class);
        novaIntent.putExtra("acesso", Persistencia.getInstance().getAcessoCarregado());
        startActivity(novaIntent);
        editando = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(editando){
            adp.atualizarObjeto(Persistencia.getInstance().getAcessoCarregado());
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
        ac = (Acesso) adp.getObjeto(marcador);
        Usuario usr = new Usuario();
        usr.setId(ac.getUsuarioId());
        usr = usr.buscaObjetoNaLista(Persistencia.getInstance().getUsuariosComAcesso());

        new AlertDialog.Builder(AcessoGerenciar.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Alerta!")
                .setMessage("Tem certeza que deseja remover o acesso para o usário: " + usr.getNome())
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Persistencia.getInstance().validarRemocaoParticipante(ac.getId());
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
                ac.remover();
                adp.remover(marcador);
                adp.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Existem Atendimentos vinculadas a este Acesso", Toast.LENGTH_SHORT).show();
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
