package br.ufms.nafmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.UnidadeTipo;

public class UnidadeTipoAdapter extends ArrayAdapter<UnidadeTipo> {

    public UnidadeTipoAdapter(@NonNull Context context, ArrayList<UnidadeTipo> unidadeTipoLista) {
        super(context, 0, unidadeTipoLista);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.unidade_tipo,
                    parent, false);
        }

        TextView tv = convertView.findViewById(R.id.tv_unidadeTipoNome);

        UnidadeTipo tipoSelecionado = getItem(position);

        if(tipoSelecionado != null){
            tv.setText(tipoSelecionado.getNome());
        }

        return convertView;
    }
}
