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

import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.Atendido;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.CustomObject;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.UnidadeTipo;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;

public class Persistencia {

    private List<StatusEnum> listagemAtivos = new ArrayList<StatusEnum>();
    //region Atributos
    private static Persistencia mInstance = null;
    private static boolean carregaCidades = true;
    private Long versao = 1L;
    private FirebaseFirestore firebaseFirestore;
    private List<UnidadeTipo> unidadeTipos;
    private List<Estado> estados;
    private ArrayList<Cidade> cidadeLista;
    private Estado estadoAtual;
    private Cidade cidadeAtual;
    private List<AtendimentoTipo> atendimentos;
    private List<Atendido> atendidos;
    private List<Cidade> cidades;
    private ArrayList<Unidade> unidades;
    private ArrayList<Usuario> usuarios;
    private ArrayList<Acesso> acessos;
    private ArrayList<Universidade> universidades;
    private ArrayList<Regiao> regioes;
    private List<Acesso> usuarioAcesso;
    private Acesso acessoAtual;
    private Unidade unidadeAtual;
    private Universidade universidadeAtual;
    private Usuario usuarioAtual;
    private Regiao regiaoAtual;

    //endregion

    //region Geral
    protected Persistencia() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void Iniciar() {
//        if (versao == -1) {
//            this.inserirDadosDefault();
//        }

        this.instanciarLista();
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

    public void instanciarLista(){
        listagemAtivos = new ArrayList<StatusEnum>();
        listagemAtivos.add(StatusEnum.ATIVO);
        listagemAtivos.add(StatusEnum.BLOQUEADO);
        listagemAtivos.add(StatusEnum.RASCUNHO);
    }
    //endregion

    //region Inserir Default

    public void inserirDadosDefault() {
        insereUnidadesTipo();
        insereAtendidos();
        insereEstados();
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

    //region Persistencia no banco
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

    public void removerObjeto(CustomObject obj){
        String colecao = this.getNomeColecaoByObjeto(obj);

        if(colecao != null && colecao.length() >0){
            obj.setStatus(StatusEnum.INATIVO);
            firebaseFirestore.collection(colecao).document(obj.getId()).set(obj);
        }
    }
    //endregion

    //region Região
    public void carregaRegioes() {
        regioes = new ArrayList<Regiao>();
        firebaseFirestore.collection("regiao")
                .orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Regiao rg = (Regiao) document.toObject(Regiao.class);
                        regioes.add(rg);
                    }
                }
            }
        });
    }

    public void carregaRegiaoById(String id) {
        this.regiaoAtual = new Regiao();
        DocumentReference dr =
                firebaseFirestore.collection("regiao").document(id);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    regiaoAtual = (Regiao) document.toObject(Regiao.class);
                }
            }
        });
    }
    //endregion

    //region Unidade
    private void getUnidadesTipoBanco() {
        unidadeTipos = new ArrayList<>();
        firebaseFirestore.collection("unidade_tipo").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
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

    public void carregaUnidades() {
        this.unidades = new ArrayList<>();
        firebaseFirestore.collection("unidade").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Unidade und = (Unidade) document.toObject(Unidade.class);
                        unidades.add(und);
                    }
                }
            }
        });
    }

    public void carregaUnidadeById(String id) {
        this.unidadeAtual = new Unidade();
        DocumentReference dr =
                firebaseFirestore.collection("unidade").document(id);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    unidadeAtual = (Unidade) document.toObject(Unidade.class);
                }
            }
        });
    }
    //endregion

    //region Universidade
    public void carregaUniversidades() {
        universidades = new ArrayList<Universidade>();
        firebaseFirestore.collection("universidade").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
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

    public void carregaUniversidadeById(String id) {
        this.universidadeAtual = new Universidade();
        DocumentReference dr =
                firebaseFirestore.collection("universidade").document(id);
                 dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    universidadeAtual = (Universidade) document.toObject(Universidade.class);
                }
            }
        });
    }
    //endregion

    //region Usuario
    public void carregaUsuarios() {
        usuarios = new ArrayList<Usuario>();
        firebaseFirestore.collection("usuario").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
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

    public void carregaUsuarioById(String id) {
        usuarioAtual = new Usuario();
        if (id != null && id.length() > 0) {
            DocumentReference documento = firebaseFirestore.collection("usuario").document(id);
            documento.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        usuarioAtual = task.getResult().toObject(Usuario.class);
                    }
                }
            });
        }
    }
    //endregion

    //region Estado Cidade
    private void carregaEstadosCidades() {
        this.estados = new ArrayList<>();
        this.cidades = new ArrayList<>();
        firebaseFirestore.collection("estado").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
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

        carregaCidades();
    }

    private void carregaCidades() {
        firebaseFirestore.collection("cidade").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Cidade cid = (Cidade) document.toObject(Cidade.class);
                        cidades.add(cid);
                    }
                }
            }
        });
    }

    public void carregaEstadoById(String id) {
        this.estadoAtual = new Estado();
        DocumentReference dr =
                firebaseFirestore.collection("estado").document(id);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    estadoAtual = (Estado) document.toObject(Estado.class);
                }
            }
        });
    }

    public void carregaCidadeEstadoByCidadeId(String id) {
        this.cidadeAtual = new Cidade();
        DocumentReference dr =
                firebaseFirestore.collection("cidade").document(id);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    cidadeAtual = (Cidade) document.toObject(Cidade.class);
                    carregaEstadoById(cidadeAtual.getEstadoId());
                }
            }
        });
    }

    public void carregaCidadeById(String id) {
        this.cidadeAtual = new Cidade();
        DocumentReference dr =
                firebaseFirestore.collection("cidade").document(id);
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    cidadeAtual = (Cidade) document.toObject(Cidade.class);
                }
            }
        });
    }

    private void getCidadesBanco(Estado estado) {
        final Estado est = estado;
        firebaseFirestore.collection("cidade")
                .whereEqualTo("estadoId", estado.getId())
                .whereEqualTo("status", StatusEnum.ATIVO)
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

    private void getCidadesBanco() {
        this.cidades = new ArrayList<>();
        firebaseFirestore.collection("cidade")
                .whereEqualTo("status", StatusEnum.ATIVO)
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

    public Cidade buscaCidadeById(String cidadeId){
        for (Cidade cd: this.cidades) {
            if(cd.getId().equals(cidadeId)){
                return cd;
            }
        }
        return  new Cidade();
    }

    public Cidade getCidade(String cidadeId){
        Cidade cd = new Cidade();

        cd = this.buscaCidadeById(cidadeId);
        if(cd.getId() == null || cd.getId().length() == 0) {
            carregaCidades();
            cd = this.buscaCidadeById(cidadeId);
        }

        return cd;
    }

    public Estado buscaEstadoById(String estadoId){
        for (Estado est: this.estados) {
            if(est.getId().equals(estadoId)){
                return est;
            }
        }
        return  new Estado();
    }

    public Estado getEstado(String estadoId){
        Estado est = new Estado();

        est = this.buscaEstadoById(estadoId);
        if(est.getId() == null || est.getId().length() == 0) {
            carregaEstados();
            est = this.buscaEstadoById(estadoId);
        }

        return est;
    }

    public void carregaCidadeLista(String estadoId){
        this.cidadeLista = new ArrayList<Cidade>();
        for (Cidade cid: this.cidades) {
            if(cid.getEstadoId().equals(estadoId)){
                this.cidadeLista.add(cid);
            }
        }
    }

    //endregion

    //region ETC
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

    private void getAtendidosBanco() {
        atendidos = new ArrayList<>();
        firebaseFirestore.collection("atendido").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
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

    public void getAutenticar(Usuario usuario) {
        usuarioAtual = new Usuario();
        firebaseFirestore.collection("usuario")
                .whereEqualTo("cpf", usuario.getCpf())
                .whereEqualTo("senha", usuario.getSenha())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().getDocuments().size() > 0){
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                        usuarioAtual = (Usuario) doc.toObject(Usuario.class);
                    }

                    if(!usuarioAtual.temId())
                        usuarioAtual.setMensagem("Usuário não foi encontrado!");

                }
            }
        });
    }

    public void carregaAcessos(String id) {
        this.acessos = new ArrayList<>();
        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac  = (Acesso) document.toObject(Acesso.class);
                        acessos.add(ac);
                    }
                    usuarioAtual.setAcessos(acessos);
                }
            }
        });
    }
    //endregion

    //region Getters

    public Long getVersao() {
        return versao;
    }

    public Acesso getAcessoAtual() {
        return acessoAtual;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public ArrayList<Unidade> getUnidades() {
        return unidades;
    }

    public List<Acesso> getAcessos() {
        return usuarioAcesso;
    }

    public void carregaEstados() {
        if (versao == 0) {
            getEstadosLocal();
        } else {
            carregaEstadosCidades();
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

    public List<AtendimentoTipo> getAtendimentos() {
        return this.atendimentos;
    }

    public ArrayList<Universidade> getUniversidades() {
        return universidades;
    }

    public Unidade getUnidadeAtual() {
        return unidadeAtual;
    }

    public void setUnidadeAtual(Unidade unidadeAtual) {
        this.unidadeAtual = unidadeAtual;
    }

    public Universidade getUniversidadeAtual() {
        return universidadeAtual;
    }

    public void setUniversidadeAtual(Universidade universidadeAtual) {
        this.universidadeAtual = universidadeAtual;
    }

    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public void setUsuarioAtual(Usuario usuarioAtual) {
        this.usuarioAtual = usuarioAtual;
    }

    public void setAcessoAtual(Acesso acessoAtual) {
        this.acessoAtual = acessoAtual;
    }

    public Regiao getRegiaoAtual() {
        return regiaoAtual;
    }

    public void setRegiaoAtual(Regiao regiaoAtual) {
        this.regiaoAtual = regiaoAtual;
    }

    public ArrayList<Regiao> getRegioes() {
        return regioes;
    }

    public void setRegioes(ArrayList<Regiao> regioes) {
        this.regioes = regioes;
    }

    public void setEstados(List<Estado> estados) {
        this.estados = estados;
    }

    public Estado getEstadoAtual() {
        return estadoAtual;
    }

    public void setEstadoAtual(Estado estadoAtual) {
        this.estadoAtual = estadoAtual;
    }

    public Cidade getCidadeAtual() {
        return cidadeAtual;
    }

    public void setCidadeAtual(Cidade cidadeAtual) {
        this.cidadeAtual = cidadeAtual;
    }

    public void setAtendimentos(List<AtendimentoTipo> atendimentos) {
        this.atendimentos = atendimentos;
    }

    public List<Atendido> getAtendidos() {
        return atendidos;
    }

    public void setAtendidos(List<Atendido> atendidos) {
        this.atendidos = atendidos;
    }

    public List<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }

    public void setUnidades(ArrayList<Unidade> unidades) {
        this.unidades = unidades;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public void setUniversidades(ArrayList<Universidade> universidades) {
        this.universidades = universidades;
    }

    public List<Acesso> getUsuarioAcesso() {
        return usuarioAcesso;
    }

    public void setUsuarioAcesso(List<Acesso> usuarioAcesso) {
        this.usuarioAcesso = usuarioAcesso;
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
        else if(new UnidadeTipo().equals(obj))
            colecao = "unidade_tipo";
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
        else if (new Regiao().equals(obj))
            colecao = "regiao";

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
        this.estados = new ArrayList<>();
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
        this.cidades = new ArrayList<>();
        for (Estado est: this.estados) {
            this.cidades.addAll(getCidadesLocal(est));
        }
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
