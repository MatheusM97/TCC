package br.ufms.nafmanager.persistencies;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Atendido;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.CustomObject;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.UnidadeTipo;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;

public class Persistencia {

    //region Atributos
    private static Persistencia mInstance = null;
    private Long versao = 1L;
    private FirebaseFirestore firebaseFirestore;
    private List<UnidadeTipo> unidadeTipos;
    private List<Estado> estados;
    private List<AtendimentoTipo> atendimentos;
    private List<Atendido> atendidos;
    private List<Cidade> cidades;
    private List<Unidade> unidades;
    private List<Usuario> usuarios;
    private List<Universidade> universidades;
    private List<Acesso> usuarioAcesso;
    private Usuario usuarioLogado;
    private Acesso acessoAtual;
    //endregion

    protected Persistencia() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void Iniciar() {
        if (versao == -1) {
            this.inserirDadosDefault();
        }

        this.carregaUnidadesTipo();
        this.carregaEstados();
        this.getAtendimentoTipoLocal();
        this.carregaAtendidos();
        this.carregaUniversidades();
        this.carregaUsuarios();
    }

    public static synchronized Persistencia getInstance() {
        if (null == mInstance) {
            mInstance = new Persistencia();
        }
        return mInstance;
    }

    //region Inserir Default

    public void inserirDadosDefault() {
        insereEstados();
        insereUnidadesTipo();
        insereAtendidos();
    }

    private void insereEstados() {
        getEstadosLocal();
        for (Estado estado : estados) {
            this.persistirObjeto(estado);
            for (Cidade cidade : this.getCidadesLocal(estado)) {
                this.persistirObjeto(cidade);
            }
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

    //endregion

    //region Busca no banco

    public void buscaVersao() {
        DocumentReference dr = firebaseFirestore
                .collection("banco")
                .document("I2Mh0OOkU2WoMaejdhDU");
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    versao = task.getResult().getLong("versao");
                }
            }
        });
    }

    public void persistirObjeto(CustomObject obj) {
        String colecao = this.getNomeColecaoByObjeto(obj);

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


    private Usuario usr;

    public Usuario getUsuarioById(String id) {
        if (id != null && id.length() > 0) {
            DocumentReference documento = firebaseFirestore.collection("usuario").document(id);
            documento.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        usr = task.getResult().toObject(Usuario.class);
                    }
                }
            });
        }
        return usr;
    }

    public void carregaUniversidades() {
        universidades = new ArrayList<Universidade>();
        firebaseFirestore.collection("universidade")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Universidade unv = (Universidade) document.toObject(Universidade.class);
                        universidades.add(unv);
                    }
                }
            }
        });
    }


    private void getEstadosBanco() {
        estados = new ArrayList<>();
        firebaseFirestore.collection("estado")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Estado est = (Estado) document.toObject(Estado.class);
                        estados.add(est);
                    }
                }
            }
        });
    }

    private void getUnidadesTipoBanco() {
        unidadeTipos = new ArrayList<>();
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
        atendidos = new ArrayList<>();
        firebaseFirestore.collection("atendido")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Atendido at = (Atendido) document.toObject(Atendido.class);
                        atendidos.add(at);
                    }
                }
            }
        });
    }

    private void getCidadesBanco(Estado estado) {
        firebaseFirestore.collection("cidade").whereEqualTo("sigla", estado.getSigla())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Cidade cd = (Cidade) document.toObject(Cidade.class);
                        cidades.add(cd);
                    }
                }
            }
        });
    }

    public void getAutenticar(String usr, String psd) {
        usuarioLogado = new Usuario();
        firebaseFirestore.collection("usuario").whereEqualTo("cpf", usr).whereEqualTo("senha", psd)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        usuarioLogado = (Usuario) document.toObject(Usuario.class);
                        carregaAcessos(usuarioLogado.getId());
                        break;
                    }
                }
            }
        });
    }

    public void carregaAcessoAtual(String usuarioId, String universidadeId) {
        acessoAtual = new Acesso();
        firebaseFirestore.collection("acesso").whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("universidadeId", universidadeId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = (Acesso) document.toObject(Acesso.class);
                        acessoAtual = ac;
                    }
                }
            }
        });
    }

    public void carregaUsuarios() {
        usuarios = new ArrayList<Usuario>();
        firebaseFirestore.collection("usuario")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Usuario usr = (Usuario) document.toObject(Usuario.class);
                        usuarios.add(usr);
                    }
                }
            }
        });
    }

    public void carregaUnidades() {
        this.unidades = new ArrayList<>();
        firebaseFirestore.collection("unidade")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Unidade und = (Unidade) document.toObject(Unidade.class);
//                        if (document.getString("id") != null)
//                            und.setId(document.getString("id"));
//                        if (document.getString("nome") != null)
//                            und.setNome(document.getString("nome"));
//                        if (document.getString("estadoId") != null)
//                            und.setEstadoId(document.getString("estadoId"));
//                        if (document.getString("estadoNome") != null)
//                            und.setEstadoNome(document.getString("estadoNome"));
//                        if (document.getString("estadoSigla") != null)
//                            und.setEstadoSigla(document.getString("estadoSigla"));
//                        if (document.getString("cidadeId") != null)
//                            und.setCidadeId(document.getString("cidadeId"));
//                        if (document.getString("cidadeNome") != null)
//                            und.setCidadeNome((document.getString("cidadeNome")));
//                        if (document.getLong("regiaoFiscal") != null)
//                            und.setRegiaoFiscal(Integer.parseInt(document.getLong("regiaoFiscal").toString()));
//                        if (document.getString("responsavelId") != null)
//                            und.setResponsavelId(document.getString("responsavelId"));
//                        if (document.getString("responsavelNome") != null)
//                            und.setResponsavelNome(document.getString("responsavelNome"));
                        unidades.add(und);
                    }
                }
            }
        });
    }

    public void carregaAcessos(String id) {
        acessoAtual = new Acesso();
        if (id != null && id.length() > 0) {
            DocumentReference dr = firebaseFirestore.collection("acesso").document(id);
            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Acesso acesso = (Acesso) task.getResult().toObject(Acesso.class);
                        acessoAtual = acesso;
                    }
                }
            });
        }
    }
    //endregion

    //region Getters

    public Long getVersao() {
        return versao;
    }

    public Acesso getAcessoAtual() {
        return acessoAtual;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Unidade> getUnidades() {
        return unidades;
    }

    public List<Acesso> getAcessos() {
        return usuarioAcesso;
    }

    public void carregaEstados() {
        if (versao == 0) {
            getEstadosLocal();
        } else {
            getEstadosBanco();
        }
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public void carregaUnidadesTipo() {
        if (versao == 0) {
            getUnidadesTipoLocal();
        } else {
            getUnidadesTipoBanco();
        }
    }

    public List<UnidadeTipo> getUnidadesTipo() {
        return unidadeTipos;
    }

    public void carregaAtendidos() {
        if (versao == 0) {
            getAtendidosLocal();
        } else {
            getAtendidosBanco();
        }
    }

    public List<Atendido> getAtendido() {
        return atendidos;
    }

    public List<Cidade> getCidades(Estado est) {
        if (versao == 0) {
            cidades = getCidadesLocal(est);
        } else {
            getCidadesBanco(est);
        }

        return cidades;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public List<AtendimentoTipo> getAtendimentos() {
        return this.atendimentos;
    }

    public List<Universidade> getUniversidades() {
        return universidades;
    }


    //endregion

    public String getNomeColecaoByObjeto(Object obj) {
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
        else if (new Universidade().equals(obj))
            colecao = "universidade";
        else if (new Acesso().equals(obj))
            colecao = "acesso";
        else if (new Atendido().equals(obj))
            colecao = "atendido";

        return colecao;
    }

    //region Local
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
        lista.add(new Atendido("eJBYdgN73UduYpr3464P", "Pessoa Física"));
        lista.add(new Atendido("kbU2kPG0ki2EZh68VNmy", "Microempreendedor Individual(MEI)"));
        lista.add(new Atendido("mtjSYNphM6pfp3Zn7uOB", "Microempresa optante pelo Simples Nacional"));
        lista.add(new Atendido("3lOsIPApci6rHGXxEOMx", "Empresa de Pequeno porte optante pelo Simples Nacional"));
        lista.add(new Atendido("5LFQd4NcmU7l80Iscaor", "Entidade sem fins lucrativos"));
        lista.add(new Atendido("VitxMl0bVODPwFtXz6w3", "Outro"));
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

    public void getAtendimentoTipoLocal() {
        List<AtendimentoTipo> lista = new ArrayList<AtendimentoTipo>();
        lista.add(new AtendimentoTipo("BFUw5tuJHZPmcKwb2u4f", "Auxílio à elaboração e orientações sobre a Declaração de Ajuste Anual do IRPF"));
        lista.add(new AtendimentoTipo("m7pHX9YXcasc4NCnLF83", "Auxílio à inscrição e Informações cadastrais de CPF"));
        lista.add(new AtendimentoTipo("efE4yAzydBFpPYEOhP46", "Auxílio à inscrição e Informações cadastrais do CNPJ"));
        lista.add(new AtendimentoTipo("eX4ddhUEeVkcgS4gIuo8", "Auxílio à emissão e informações sobre Certidões Negativas de Débitos PF e PJ"));
        lista.add(new AtendimentoTipo("wbLxjD9kU0nQGTZEDfp4", "Auxílio à consulta à situação fiscal"));
        lista.add(new AtendimentoTipo("4oTugvnwxE17vcHsMzO8", "Agendamento on-line de s na RFB"));
        lista.add(new AtendimentoTipo("4oTugvnwxE17vcHsMzO8", "Informações e auxílio à regularização de CPF Suspenso"));
        lista.add(new AtendimentoTipo("67zNThARxUzayOvgiIjP", "Informações e auxílio à elaboração de pedido de isenção de IRPF para portadores de moléstias graves"));
        lista.add(new AtendimentoTipo("EkOhufIPvpQDGn1U1y6S", "Orientações e auxílio à elaboração de pedidos de isenção de IPI/IOF na compra de veículos por portadores de deficiência física, mental ou visual"));
        lista.add(new AtendimentoTipo("NBHGbyhivHfcLWwsktO4", "Auxílio à apresentação de pedidos de restituição de pagamentos indevidos e/ou a maior (Perdcomps)"));
        lista.add(new AtendimentoTipo("UGkGSdR3iEyVjSROTESN", "Informações gerais sobre ITR"));
        lista.add(new AtendimentoTipo("4oxpvKyIdjBR3nDhqlZV", "Auxílio à inscrição e Informações gerais sobre o Microempreendedor Individual"));
        lista.add(new AtendimentoTipo("TfLRF8xnMWjzmw0FhJmH", "Auxílio à inscrição e Informações gerais sobre o Simples Nacional"));
        lista.add(new AtendimentoTipo("ltqhMsqYa3rJc7JAUs9N", "Auxílio à inscrição e informações cadastrais da matrícula CEI"));
        lista.add(new AtendimentoTipo("fBW0Bx1hZj9tl1gZDAgM", "Informações e auxílio no eSocial do empregador doméstico"));
        lista.add(new AtendimentoTipo("ajnGAJAHNFmWbhdgKFBQ", "Auxílio à emissão e informações sobre guias para o recolhimento da contribuição previdenciária de Produtores Rurais Pessoa Física, Segurado Especial, Contribuinte Individual e obras de pessoas físicas"));
        lista.add(new AtendimentoTipo("XMKtd0V5fIn31P5MD63x", "Orientações e auxílio ao cumprimento de obrigações tributárias acessórias para associações e demais entidades sem fins lucrativos"));
        lista.add(new AtendimentoTipo("aQQlvMIceikWdV3gjx2X", "Informações e auxilio para a obtenção de Certificado Digital"));
        lista.add(new AtendimentoTipo("uw72qLtG2q8iYSRNVrCS", "Informações e auxilio para realizar a opção pelo Domicílio Tributário Eletrônico - DTE"));
        lista.add(new AtendimentoTipo("IWoDGO6nZEBju6LHVj8K", "Auxílio à habilitação nos sistemas RADAR e Siscomex"));
        lista.add(new AtendimentoTipo("AiEwDKQQvGhp2dXPwNkm", "Informações sobre regras de importação e exportação através dos Correios"));
        lista.add(new AtendimentoTipo("xGYylzx84CdVzI6duIHy", "Informações sobre Regras de Bagagem"));
        this.atendimentos = lista;
    }

    public List<Cidade> getCidadesLocal(Estado estado) {
        if ("AC".equals(estado.getSigla())) {
            return AC.getCidades();
        } else if ("AL".equals(estado.getSigla())) {
            return AL.getCidades();
        } else if ("AM".equals(estado.getSigla())) {
            return AM.getCidades();
        } else if ("AP".equals(estado.getSigla())) {
            return AP.getCidades();
        } else if ("BA".equals(estado.getSigla())) {
            return BA.getCidades();
        } else if ("CE".equals(estado.getSigla())) {
            return CE.getCidades();
        } else if ("DF".equals(estado.getSigla())) {
            return DF.getCidades();
        } else if ("ES".equals(estado.getSigla())) {
            return ES.getCidades();
        } else if ("GO".equals(estado.getSigla())) {
            return GO.getCidades();
        } else if ("MA".equals(estado.getSigla())) {
            return MA.getCidades();
        } else if ("MG".equals(estado.getSigla())) {
            return MG.getCidades();
        } else if ("MS".equals(estado.getSigla())) {
            return MS.getCidades();
        } else if ("MT".equals(estado.getSigla())) {
            return MT.getCidades();
        } else if ("PA".equals(estado.getSigla())) {
            return PA.getCidades();
        } else if ("PB".equals(estado.getSigla())) {
            return PB.getCidades();
        } else if ("PE".equals(estado.getSigla())) {
            return PE.getCidades();
        } else if ("PI".equals(estado.getSigla())) {
            return PI.getCidades();
        } else if ("PR".equals(estado.getSigla())) {
            return PR.getCidades();
        } else if ("RJ".equals(estado.getSigla())) {
            return RJ.getCidades();
        } else if ("RN".equals(estado.getSigla())) {
            return RN.getCidades();
        } else if ("RO".equals(estado.getSigla())) {
            return RO.getCidades();
        } else if ("RR".equals(estado.getSigla())) {
            return RR.getCidades();
        } else if ("RS".equals(estado.getSigla())) {
            return RS.getCidades();
        } else if ("SC".equals(estado.getSigla())) {
            return SC.getCidades();
        } else if ("SE".equals(estado.getSigla())) {
            return SE.getCidades();
        } else if ("SP".equals(estado.getSigla())) {
            return SP.getCidades();
        } else if ("TO".equals(estado.getSigla())) {
            return TO.getCidades();
        }
        return null;
    }
    //endregion
}
