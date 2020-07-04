package br.ufms.nafmanager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Regiao;

public class RegiaoAdapter extends ArrayAdapter<Regiao> {
    public ArrayList<Regiao> lista;
    private final Activity context;

    public RegiaoAdapter(Activity context, ArrayList<Regiao> lista){
        super(context, R.layout.listagem_item, lista);

        this.context = context;
        this.lista = lista;
    }

    public View getView(int position, View view, final ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listagem_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.et_listagemTitulo);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.et_listagemSubstitulo);

        titleText.setText(lista.get(position).getNome());
        subtitleText.setText("Estados: " +lista.get(position).getEstadosSigla());

        return rowView;
    }

    public Regiao getObjeto(int position){
        Regiao und = lista.get(position);
        return und;
    }

    public void atualizarObjeto(Regiao regiao) {
        for (Regiao reg : this.lista) {
            if (reg.getId().equals(regiao.getId())) {
                this.lista.remove(reg);
                this.lista.add(regiao);
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
