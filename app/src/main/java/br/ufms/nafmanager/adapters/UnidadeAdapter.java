package br.ufms.nafmanager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UnidadeAdapter extends ArrayAdapter<Unidade> {
    public ArrayList<Unidade> lista;
    private final Activity context;

    public UnidadeAdapter(Activity context, ArrayList<Unidade> lista){
        super(context, R.layout.listagem_item, lista);

        this.context = context;
        this.lista = lista;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listagem_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.et_listagemTitulo);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.et_listagemSubstitulo);

        Unidade und = lista.get(position);
        Cidade cidade = Persistencia.getInstance().getCidade(und.getCidadeId());
        String estadoSigla = Persistencia.getInstance().getEstado(cidade.getEstadoId()).getSigla();
        titleText.setText(lista.get(position).getNome());

        subtitleText.setText(cidade.getNome()+ "/" + estadoSigla);

        return rowView;
    }

    public Unidade getObjeto(int position){
        Unidade und = lista.get(position);
        return und;
    }

    public void atualizarObjeto(Unidade unidade) {
        for (Unidade und : this.lista) {
            if (und.getId().equals(unidade.getId())) {
                this.lista.remove(und);
                this.lista.add(unidade);
                break;
            }
        }
    }

    public void remover(int position){
        if(position >= 0){
            this.lista.remove(position);
        }
    }
}
