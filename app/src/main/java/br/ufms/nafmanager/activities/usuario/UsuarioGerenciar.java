package br.ufms.nafmanager.activities.usuario;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.adapters.UsuarioAdapter;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UsuarioGerenciar extends CustomActivity {
    private Usuario usuario;
    private ProgressDialog progressDialog;
    private UsuarioAdapter adp;
    private Usuario usr;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);

        carregaLista();
    }

    private void carregaLista(){
        adp = new UsuarioAdapter(this, getUsuarioAtual());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Persistencia.getInstance().setUsuarioCarregado((Usuario) parent.getItemAtPosition(position));
                carregaCad();
           }
        });

        registerForContextMenu(list);
    }

    private void carregaCad() {
        Intent novaIntent = new Intent(getBaseContext(), UsuarioInserir.class);
        novaIntent.putExtra("usuario", Persistencia.getInstance().getUsuarioCarregado());
        startActivity(novaIntent);
        editando = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(editando){
            adp.atualizarObjeto(Persistencia.getInstance().getUsuarioCarregado());
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
        usr = (Usuario) adp.getObjeto(marcador);
        new AlertDialog.Builder(UsuarioGerenciar.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Alerta!")
                .setMessage("Tem certeza que deseja remover o item: " + usr.getNome())
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Persistencia.getInstance().validarRemocaoUsuario(usr.getId());
                        aguardandoValidacao();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
        return true;
    }

    private ArrayList<Usuario> getUsuarioAtual(){
        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (Usuario usr: Persistencia.getInstance().getUsuarios()) {
            if(!usuarios.contains(usr)){
                usuarios.add(usr);
            }
        }

        return usuarios;
    }

    public void validou(){
        if(Persistencia.getInstance().isVerificouExclusao()){
            if(Persistencia.getInstance().isPodeExcluir()){
                usr.remover();
                adp.remover(marcador);
                adp.notifyDataSetChanged();
            }else{
                Toast.makeText(this, "Existem Acessos vinculadas a este Usuário", Toast.LENGTH_SHORT).show();
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
