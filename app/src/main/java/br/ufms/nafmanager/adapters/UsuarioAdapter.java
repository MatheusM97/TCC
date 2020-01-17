package br.ufms.nafmanager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Usuario;

public class UsuarioAdapter extends ArrayAdapter<Usuario> {
    public ArrayList<Usuario> lista;
    private final Activity context;

    public UsuarioAdapter(Activity context, ArrayList<Usuario> lista){
        super(context, R.layout.listagem_item, lista);

        this.context = context;
        this.lista = lista;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listagem_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.et_listagemTitulo);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.et_listagemSubstitulo);

        titleText.setText(lista.get(position).getNome());
        subtitleText.setText(MaskEditUtil.mask(lista.get(position).getCpf()) + " - "+ lista.get(position).getEmail());

        return rowView;
    }

    public Usuario getObjeto(int position){
        Usuario und = lista.get(position);
        return und;
    }
}
