package br.ufms.nafmanager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoAdapter extends ArrayAdapter<Acesso> {
        public ArrayList<Acesso> lista;
        private final Activity context;

    public AcessoAdapter(Activity context, ArrayList<Acesso> lista){
        super(context, R.layout.listagem_item, lista);

        this.context = context;
        this.lista = lista;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listagem_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.et_listagemTitulo);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.et_listagemSubstitulo);

        Acesso ac = lista.get(position);
        if(ac.getUnidadeId() != null && ac.getUnidadeId().length() > 0){
            Unidade und = new Unidade();
            und.setId(lista.get(position).getUnidadeId());
            und = und.buscaObjetoNaLista(Persistencia.getInstance().getUnidades());
            titleText.setText("Unidade: " + und.getNome());

//            Cidade cid = new Cidade();
//            cid.setId(und.getCidadeId());
//            cid = cid.buscaObjetoNaLista(Persistencia.getInstance().getCidades());
//            Estado est = Persistencia.getInstance().getEstado(cid.getEstadoId());

//            subtitleText.setText(cid.getNome() +" / " + est.getSigla());
        }

        if(ac.getUniversidadeId() != null && ac.getUniversidadeId().length() > 0){
            Universidade unv = new Universidade();
            unv.setId(ac.getUniversidadeId());
            unv = unv.buscaObjetoNaLista(Persistencia.getInstance().getUniversidades());
            titleText.setText("Universidade: " + unv.getNome());

//            Cidade cid = new Cidade();
//            cid.setId(unv.getCidadeId());
//            cid = cid.buscaObjetoNaLista(Persistencia.getInstance().getCidades());
//            Estado est = Persistencia.getInstance().getEstado(cid.getEstadoId());
//
//            subtitleText.setText(cid.getNome() + " / " + est.getSigla());
        }

        subtitleText.setText(ac.listaPapeisAtivos());

        return rowView;
    }

    public Acesso getObjeto(int position){
        Acesso und = lista.get(position);
        return und;
    }
}
