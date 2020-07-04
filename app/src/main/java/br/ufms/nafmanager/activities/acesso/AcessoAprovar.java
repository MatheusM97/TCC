package br.ufms.nafmanager.activities.acesso;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.adapters.AcessoUsuarioAdapter;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoAprovar extends CustomActivity {
    private AcessoUsuarioAdapter adp;
    private Acesso acessoSelecionado = new Acesso();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listagem_gerencial);
        carregaLista();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        if(acessoSelecionado.isSolicitando()){
            finish();
        }
    }

    public void carregaLista(){
        adp = new AcessoUsuarioAdapter(this, Persistencia.getInstance().getAcessosRegistrados());
        ListView list = (ListView) findViewById(R.id.lv_listagemGerencial);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                acessoSelecionado = (Acesso) parent.getItemAtPosition(position);
                acessoSelecionado.setSolicitando(true);
                Persistencia.getInstance().setAcessoCarregado(acessoSelecionado);
                carregaCad();
            }
        });
    }

    private void carregaCad() {
        Intent novaIntent = new Intent(getBaseContext(), AcessoInserir.class);
        novaIntent.putExtra("acesso", Persistencia.getInstance().getAcessoCarregado());
        startActivity(novaIntent);
    }
}
