package br.ufms.nafmanager.persistencies;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.model.Atendido;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.CustomObject;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.UnidadeTipo;
import br.ufms.nafmanager.model.Usuario;

public class Persistencia {

    private static Persistencia mInstance = null;
    private int versao = 0;
    private FirebaseFirestore firebaseFirestore;
    private List<UnidadeTipo> unidadeTipos;
    private List<Estado> estados;
    private List<AtendimentoTipo> atendimentos;
    private List<Atendido> atendidos;

    protected Persistencia() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.buscaVersao();
        this.getUnidadesTipo();
        this.getEstados();
        this.getAtendimentoTipoLocal();
    }

    public static synchronized Persistencia getInstance() {
        if (null == mInstance) {
            mInstance = new Persistencia();
        }
        return mInstance;
    }

    private void buscaVersao() {
        firebaseFirestore.collection("banco")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    try {
                        versao = Integer.parseInt(task.getResult().getDocuments().get(0).getString("versao").toString());
                    } catch (Exception e) {
                        versao = 1;
                    }
                }
            }
        });
    }

    public void persistirSeNaoExistir(CustomObject obj){

    }

    public void persistirObjeto(CustomObject obj) {
        String colecao = "";

        if (new Atendimento().equals(obj))
            colecao = "atendimento";
        else if (new Usuario().equals(obj))
            colecao = "usuario";
        else if (new Unidade().equals(obj))
            colecao = "unidade";
        else if (new Estado().equals(obj))
            colecao = "estado";
        else if (new Cidade().equals(obj))
            colecao = "cidade";


        if (colecao != null && colecao.length() > 0) {
            if (obj.getId() != null && obj.getId().length() > 0) {
                firebaseFirestore.collection(colecao).document(obj.getId()).set(obj);
            } else {
                DocumentReference referencia = firebaseFirestore.collection(colecao).document();
                obj.setId(referencia.getId());
                referencia.set(obj);
            }
        }
    }

    public List<Estado> getEstados() {
        if (versao == -1) {//todo:versionalize
            insereEstados();
            getEstadosLocal();
        } else if (versao == 0) {
            getEstadosLocal();
        } else {
            getEstadosBanco();
        }

        return estados;
    }

    public List<UnidadeTipo> getUnidadesTipo() {
        if (versao == -1) {//todo:versionalize
            insereUnidadesTipo();
            getUnidadesTipoLocal();
        } else if (versao == 0) {
            getUnidadesTipoLocal();
        } else {
            getUnidadesTipoBanco();
        }

        return unidadeTipos;
    }

    public List<Atendido> getAtendido() {
        if (versao == -1) {//todo:versionalize
            insereAtendidos();
            getAtendidosLocal();
        } else if (versao == 0) {
            getAtendidosLocal();
        } else {
            getAtendidosBanco();
        }

        return atendidos;
    }

    private void insereEstados() {
        getEstadosLocal();
        for (Estado estado : estados) {
            this.persistirObjeto(estado);
        }
    }

    private void insereUnidadesTipo() {
        getUnidadesTipoLocal();
        for (UnidadeTipo tipo : unidadeTipos) {
            this.persistirObjeto(tipo);
        }
    }

    private void insereAtendidos() {
        getAtendidosLocal();
        for (Atendido atendido : atendidos) {
            this.persistirObjeto(atendido);
        }
    }

    private void getEstadosBanco() {
        firebaseFirestore.collection("estado")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        estados.add(new Estado(document.getString("id"), document.getString("nome"), document.getString("sigla")));
                    }
                }
            }
        });
    }

    private void getUnidadesTipoBanco() {
        firebaseFirestore.collection("unidade_tipo")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        unidadeTipos.add(new UnidadeTipo(document.getString("id"), document.getString("nome")));
                    }
                }
            }
        });
    }

    private void getAtendidosBanco() {
        firebaseFirestore.collection("atendido")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        atendidos.add(new Atendido(document.getString("id"), document.getString("nome")));
                    }
                }
            }
        });
    }

    private void getUnidadesTipoLocal() {
        List<UnidadeTipo> lista = new ArrayList<UnidadeTipo>();
        lista.add(new UnidadeTipo("2fc6E3rkAkZcFKHpF9Eh", "Superintendência"));
        lista.add(new UnidadeTipo("G6WLURVH2Cd7TG4ACCD4", "Inspetoria"));
        lista.add(new UnidadeTipo("O6GnqBBkJZLJhIlzHycq", "Agência"));
        lista.add(new UnidadeTipo("TFN0ALlaiOwCc2paZkY0", "Delegacia"));
        lista.add(new UnidadeTipo("mLoI8CgDXH8WtVEcsC1j", "Alfandega"));
        lista.add(new UnidadeTipo("plFhYkxaF1eJq5VHtE5v", "Delegacia de Julgamento"));
        lista.add(new UnidadeTipo("usk8AUmOGY2nTu9mcV2c", "Posto"));
        lista.add(new UnidadeTipo("2fc6E3rkAkZcFKHpF9Eh", "Superintendência"));
        lista.add(new UnidadeTipo("G6WLURVH2Cd7TG4ACCD4", "Inspetoria"));
        lista.add(new UnidadeTipo("O6GnqBBkJZLJhIlzHycq", "Agência"));
        lista.add(new UnidadeTipo("TFN0ALlaiOwCc2paZkY0", "Delegacia"));
        lista.add(new UnidadeTipo("mLoI8CgDXH8WtVEcsC1j", "Alfandega"));
        lista.add(new UnidadeTipo("plFhYkxaF1eJq5VHtE5v", "Delegacia de Julgamento"));
        lista.add(new UnidadeTipo("usk8AUmOGY2nTu9mcV2c", "Posto"));
        this.unidadeTipos = lista;
    }

    private void getAtendidosLocal() {
        List<Atendido> lista = new ArrayList<Atendido>();
        lista.add(new Atendido("","Pessoa Física"));
        lista.add(new Atendido("","Microempreendedor Individual(MEI)"));
        lista.add(new Atendido("","Microempresa optante pelo Simples Nacional"));
        lista.add(new Atendido("","Empresa de Pequeno porte optante pelo Simples Nacional"));
        lista.add(new Atendido("","Entidade sem fins lucrativos"));
        lista.add(new Atendido("","Outro"));
        this.atendidos = lista;
    }

    public void getEstadosLocal() {
        List<Estado> lista = new ArrayList<Estado>();
        lista.add(new Estado("YOHaadyseN9LJy6v2wzQ", "Acre", "AC"));
        lista.add(new Estado("M5R8LCulJcMtwd9jH2T2", "Alagoas", "AL"));
        lista.add(new Estado("U4socdTZxzTDU8WB6fIY", "Amazonas", "AM"));
        lista.add(new Estado("8rK4E9CTuN3YhwbCr9GL", "Amapá", "AP"));
        lista.add(new Estado("6R8soQpFwwNYRMxhvqVJ", "Bahia", "BA"));
        lista.add(new Estado("19LMXJKf2gQhrwvFKpCG", "Ceará", "CE"));
        lista.add(new Estado("URuENL5wManmASqNRZlE", "Distrito Federal", "DF"));
        lista.add(new Estado("yTgVVuT0uf8wT03omvWX", "Espírito Santo", "ES"));
        lista.add(new Estado("vp6JrzOjGLJ1BUkxrUjo", "Goiás", "GO"));
        lista.add(new Estado("pHJjtnzuXeDQ7bqmJItp", "Maranhão", "MA"));
        lista.add(new Estado("18adfRUvWf5yNxfV3zG3", "Minas Gerais", "MG"));
        lista.add(new Estado("82bo5VcIUDbVhAiiWKE0", "Mato Grosso do Sul", "MS"));
        lista.add(new Estado("bSVpIP0ywsHAwNpWWJWT", "Mato Grosso", "MT"));
        lista.add(new Estado("ItdD34xP0a63qxRmw2P0", "Pará", "PA"));
        lista.add(new Estado("h5RRp13ofgAMn9v37Ln8", "Paraíba", "PB"));
        lista.add(new Estado("YS1okckJJhDXNtmcKS9b", "Pernambuco", "PE"));
        lista.add(new Estado("n30NTxhAuxqCa6OMcR42", "Piauí", "PI"));
        lista.add(new Estado("ExaDaauaC2Jwt6W9rT8Y", "Paraná", "PR"));
        lista.add(new Estado("piIRsvy9nHTMzBq3lRU0", "Rio de Janeiro", "RJ"));
        lista.add(new Estado("hVaznHuTvMcaCVJKgZjk", "Rio Grande do Norte", "RN"));
        lista.add(new Estado("s1YxDxTODBAl79sFMfIs", "Rondônia", "RO"));
        lista.add(new Estado("xS8wVdtWmIj134cTBLTC", "Roraima", "RR"));
        lista.add(new Estado("XE0DpyxK9DOXBlSqfma6", "Rio Grande do Sul", "RS"));
        lista.add(new Estado("4niJNacnYflWAk7B2yA4", "Santa Catarina", "SC"));
        lista.add(new Estado("S2KhrsFJMDoPxfZc91tr", "Sergipe", "SE"));
        lista.add(new Estado("RhxB7uMtgwpjGI8ZCc69", "São Paulo", "SP"));
        lista.add(new Estado("3Y8KZ4fEQRlMakrppPfa", "Tocantins", "TO"));
        this.estados = lista;
    }


    public List<AtendimentoTipo> getAtendimentos(){
        return this.atendimentos;
    }

    public void getAtendimentoTipoLocal(){
        List<AtendimentoTipo> lista = new ArrayList<AtendimentoTipo>();
        lista.add(new AtendimentoTipo("BFUw5tuJHZPmcKwb2u4f","Auxílio à elaboração e orientações sobre a Declaração de Ajuste Anual do IRPF"));
        lista.add(new AtendimentoTipo("m7pHX9YXcasc4NCnLF83","Auxílio à inscrição e Informações cadastrais de CPF"));
        lista.add(new AtendimentoTipo("efE4yAzydBFpPYEOhP46","Auxílio à inscrição e Informações cadastrais do CNPJ"));
        lista.add(new AtendimentoTipo("eX4ddhUEeVkcgS4gIuo8","Auxílio à emissão e informações sobre Certidões Negativas de Débitos PF e PJ"));
        lista.add(new AtendimentoTipo("wbLxjD9kU0nQGTZEDfp4","Auxílio à consulta à situação fiscal"));
        lista.add(new AtendimentoTipo("4oTugvnwxE17vcHsMzO8","Agendamento on-line de s na RFB"));
        lista.add(new AtendimentoTipo("4oTugvnwxE17vcHsMzO8","Informações e auxílio à regularização de CPF Suspenso"));
        lista.add(new AtendimentoTipo("67zNThARxUzayOvgiIjP","Informações e auxílio à elaboração de pedido de isenção de IRPF para portadores de moléstias graves"));
        lista.add(new AtendimentoTipo("EkOhufIPvpQDGn1U1y6S","Orientações e auxílio à elaboração de pedidos de isenção de IPI/IOF na compra de veículos por portadores de deficiência física, mental ou visual"));
        lista.add(new AtendimentoTipo("NBHGbyhivHfcLWwsktO4","Auxílio à apresentação de pedidos de restituição de pagamentos indevidos e/ou a maior (Perdcomps)"));
        lista.add(new AtendimentoTipo("UGkGSdR3iEyVjSROTESN","Informações gerais sobre ITR"));
        lista.add(new AtendimentoTipo("4oxpvKyIdjBR3nDhqlZV","Auxílio à inscrição e Informações gerais sobre o Microempreendedor Individual"));
        lista.add(new AtendimentoTipo("TfLRF8xnMWjzmw0FhJmH","Auxílio à inscrição e Informações gerais sobre o Simples Nacional"));
        lista.add(new AtendimentoTipo("ltqhMsqYa3rJc7JAUs9N","Auxílio à inscrição e informações cadastrais da matrícula CEI"));
        lista.add(new AtendimentoTipo("fBW0Bx1hZj9tl1gZDAgM","Informações e auxílio no eSocial do empregador doméstico"));
        lista.add(new AtendimentoTipo("ajnGAJAHNFmWbhdgKFBQ","Auxílio à emissão e informações sobre guias para o recolhimento da contribuição previdenciária de Produtores Rurais Pessoa Física, Segurado Especial, Contribuinte Individual e obras de pessoas físicas"));
        lista.add(new AtendimentoTipo("XMKtd0V5fIn31P5MD63x","Orientações e auxílio ao cumprimento de obrigações tributárias acessórias para associações e demais entidades sem fins lucrativos"));
        lista.add(new AtendimentoTipo("aQQlvMIceikWdV3gjx2X","Informações e auxilio para a obtenção de Certificado Digital"));
        lista.add(new AtendimentoTipo("uw72qLtG2q8iYSRNVrCS","Informações e auxilio para realizar a opção pelo Domicílio Tributário Eletrônico - DTE"));
        lista.add(new AtendimentoTipo("IWoDGO6nZEBju6LHVj8K","Auxílio à habilitação nos sistemas RADAR e Siscomex"));
        lista.add(new AtendimentoTipo("AiEwDKQQvGhp2dXPwNkm","Informações sobre regras de importação e exportação através dos Correios"));
        lista.add(new AtendimentoTipo("xGYylzx84CdVzI6duIHy","Informações sobre Regras de Bagagem"));
        this.atendimentos = lista;
    }
}
