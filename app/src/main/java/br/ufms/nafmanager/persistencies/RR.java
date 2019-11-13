package br.ufms.nafmanager.persistencies;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.model.Cidade;

public class RR {
    public static List<Cidade> getCidades() {
        List<Cidade> lista = new ArrayList<Cidade>();
        lista.add(new Cidade("", "Alto Alegre", "RR"));
        lista.add(new Cidade("", "Amajari", "RR"));
        lista.add(new Cidade("", "Boa Vista", "RR"));
        lista.add(new Cidade("", "Bonfim", "RR"));
        lista.add(new Cidade("", "Cantá", "RR"));
        lista.add(new Cidade("", "Caracaraí", "RR"));
        lista.add(new Cidade("", "Caroebe", "RR"));
        lista.add(new Cidade("", "Iracema", "RR"));
        lista.add(new Cidade("", "Mucajaí", "RR"));
        lista.add(new Cidade("", "Normandia", "RR"));
        lista.add(new Cidade("", "Pacaraima", "RR"));
        lista.add(new Cidade("", "Rorainópolis", "RR"));
        lista.add(new Cidade("", "São João da Baliza", "RR"));
        lista.add(new Cidade("", "São Luiz", "RR"));
        lista.add(new Cidade("", "Uiramutã", "RR"));
        return lista;
    }
}
