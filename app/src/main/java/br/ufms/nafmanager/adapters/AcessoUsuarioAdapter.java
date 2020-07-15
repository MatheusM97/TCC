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
import br.ufms.nafmanager.model.AcessoTipoEnum;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;

public class AcessoUsuarioAdapter extends ArrayAdapter<Acesso> {
    public ArrayList<Acesso> lista;
    private final Activity context;

    public AcessoUsuarioAdapter(Activity context, ArrayList<Acesso> lista) {
        super(context, R.layout.listagem_item, lista);

        this.context = context;
        this.lista = lista;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.listagem_item, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.et_listagemTitulo);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.et_listagemSubstitulo);

        Acesso ac = lista.get(position);

        Usuario usr = new Usuario();
        usr.setId(ac.getUsuarioId());
        usr = usr.buscaObjetoNaLista(Persistencia.getInstance().getUsuariosComAcesso());

        titleText.setText(usr.getNome() + " (" + ac.listaPapeisAtivos() + ")");

        if (ac.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor()) && ac.getUnidadeId().length() > 0) {
            Unidade und = new Unidade();
            und.setId(lista.get(position).getUnidadeId());
            und = und.buscaObjetoNaLista(Persistencia.getInstance().getUnidades());
            subtitleText.setText("Unidade: " + und.getNome());
        }
        else if (ac.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor()) && ac.getUniversidadeId().length() > 0) {
            Universidade unv = new Universidade();
            unv.setId(ac.getUniversidadeId());
            unv = unv.buscaObjetoNaLista(Persistencia.getInstance().getUniversidades());
            subtitleText.setText("Universidade: " + unv.getNome());
        }
        else if (ac.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor()) && ac.getRegiaoId().length() > 0) {
            Regiao reg = new Regiao();
            reg.setId(ac.getRegiaoId());
            reg = reg.buscaObjetoNaLista(Persistencia.getInstance().getRegioes());
            subtitleText.setText("Região: " + reg.getNome());
        }else if (ac.getTipoValor().equals(AcessoTipoEnum.MODERADOR.getValor()) && ac.isModerador()) {
            subtitleText.setText("Moderador");
        }

        return rowView;
    }

    public Acesso getObjeto(int position) {
        Acesso und = lista.get(position);
        return und;
    }

    public void atualizarObjeto(Acesso acesso) {
        for (Acesso ac : this.lista) {
            if (ac.getId().equals(acesso.getId())) {
                this.lista.remove(ac);
                this.lista.add(acesso);
                break;
            }
        }
    }

    public void remover(int position) {
        if (position >= 0) {
            this.lista.remove(position);
        }
    }
}
