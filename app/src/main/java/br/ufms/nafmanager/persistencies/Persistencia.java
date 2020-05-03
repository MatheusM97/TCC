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
import java.util.Arrays;
import java.util.List;

import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.AcessoTipoEnum;
import br.ufms.nafmanager.model.AtendidoTipo;
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
    private Long versao = 1L;

    public boolean carregouUniversidades = false;
    public boolean carregouEstados = false;
    public boolean carregouUsuariosAcesso = false;
    public boolean carregouUsuarios = false;
    public boolean buscouUsuarioPeloId = false;
    public boolean carregouAcessosPossiveis = false;
    public boolean carregouRegioes = false;
    public boolean carregouUnidades = false;

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<UnidadeTipo> unidadeTipos = new ArrayList<>();
    private ArrayList<Estado> estados = new ArrayList<>();
    private ArrayList<Cidade> cidadeLista = new ArrayList<>();
    private ArrayList<Cidade> cidadePorEstado = new ArrayList<>();
    private Estado estadoAtual;
    private Cidade cidadeAtual;
    private List<AtendimentoTipo> atendimentos = new ArrayList<>();
    private List<AtendidoTipo> atendidoTipos = new ArrayList<>();
    private ArrayList<Cidade> cidades = new ArrayList<>();
    private ArrayList<Unidade> unidades = new ArrayList<>();
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private ArrayList<Acesso> acessos = new ArrayList<>();
    private ArrayList<Acesso> acessosRegistrados = new ArrayList<>();
    private ArrayList<Usuario> usuariosComAcesso = new ArrayList<>();
    private ArrayList<Universidade> universidades = new ArrayList<>();
    private ArrayList<Regiao> regioes = new ArrayList<>();
    private ArrayList<Acesso> usuarioAcesso = new ArrayList<>();
    private Acesso acessoAtual;
    private Acesso acessoCarregado;
    private Unidade unidadeAtual;
    private Universidade universidadeAtual;
    private Usuario usuarioAtual;
    private Usuario usuarioCarregado;
    private Regiao regiaoAtual;
    private boolean pesquisouUsuarioJahCadastrado;
    private boolean pesquisouAcessoJahCadastrado= false;
    private boolean pesquisouAcessoJahSolicitado=false;
    private boolean podeGravarAcesso;
    private boolean podeFinalizarTela;
    private Usuario usuarioVerificado;
    private Acesso acessoBanco;
    private List<String> usuariosIds = new ArrayList<>();

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
        this.getAtendimentoTipoLocal();
        this.carregaAtendidos();
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
        }

        for (Cidade cidade : this.cidades) {
            this.persistirObjeto(cidade);
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
        for (AtendidoTipo atendidoTipo : atendidoTipos) {
            this.persistirObjeto(atendidoTipo);
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
        carregouRegioes = false;

        firebaseFirestore.collection("regiao")
                .orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Regiao rg = (Regiao) document.toObject(Regiao.class);
                        if(!regioes.contains(rg)){
                            regioes.add(rg);
                        }
                    }
                }
                carregouRegioes = true;
            }
        });
    }

    public void carregaRegiaoById(String id) {
        this.regioes = new ArrayList<>();
        this.carregouRegioes = false;

        DocumentReference dr =
                firebaseFirestore.collection("regiao").document(id);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Regiao reg  = (Regiao) document.toObject(Regiao.class);
                    if(!regioes.contains(reg)){
                        regioes.add(reg);
                    }
                }

                carregouRegioes = true;
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
        carregouUnidades = false;

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

                carregouUnidades = true;
            }
        });
    }

    public void carregaUnidades(Acesso acesso){
        this.unidades = new ArrayList<>();
        this.carregouUnidades = false;

        if(acesso.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor())){
            if(acesso.isRepresentante()){
                firebaseFirestore.collection("unidade").orderBy("nome")
                        .whereEqualTo("status", StatusEnum.ATIVO)
                        .whereEqualTo("id", acesso.getUnidadeId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Unidade und = (Unidade) document.toObject(Unidade.class);
                                if(!unidades.contains(und))
                                    unidades.add(und);
                            }
                        }

                        carregouUnidades = true;
                    }
                });
            }
        }
        else if (acesso.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())){
            if(acesso.isRepresentante()){
                firebaseFirestore.collection("unidade").orderBy("nome")
                        .whereEqualTo("status", StatusEnum.ATIVO)
                        .whereEqualTo("regiaoId", acesso.getRegiaoId())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Unidade und = (Unidade) document.toObject(Unidade.class);
                                if(!unidades.contains(und))
                                    unidades.add(und);
                            }
                        }
                    }
                });
            }
            else if(acesso.isModerador()){
                carregaUnidades();
            }
        }
    }

    public void carregaUnidadesUniversidades(List<String> unidadesId) {
        this.unidades = new ArrayList<>();
        final List<String> unidsLista = unidadesId;
        this.carregouUniversidades = false;

        firebaseFirestore.collection("unidade").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereIn("id", unidsLista)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Unidade und = (Unidade) document.toObject(Unidade.class);
                        unidades.add(und);
                    }

                    firebaseFirestore.collection("universidade").orderBy("nome")
                            .whereEqualTo("status", StatusEnum.ATIVO)
                            .whereIn("unidadeId", unidsLista)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                universidades = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Universidade unv = (Universidade) document.toObject(Universidade.class);
                                    universidades.add(unv);
                                }
                            }

                            carregouUniversidades = true;
                        }
                    });
                }
            }
        });
    }

    public void carregaUnidadesUniversidadesByRegiaoId(String regiaoId){
        this.unidades = new ArrayList<>();
        this.universidades = new ArrayList<>();
        this.carregouUnidades = false;

        final List<String> unidadesIds = new ArrayList<>();

        firebaseFirestore.collection("unidade").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("regiaoId", regiaoId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Unidade und = (Unidade) document.toObject(Unidade.class);
                        unidades.add(und);

                        if(!unidadesIds.contains(und.getId()))
                            unidadesIds.add(und.getId());

                    }

                    carregouUnidades = true;

                    firebaseFirestore.collection("universidade").orderBy("nome")
                            .whereEqualTo("status", StatusEnum.ATIVO)
                            .whereIn("unidadeId", unidadesIds)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                universidades = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Universidade unv = (Universidade) document.toObject(Universidade.class);
                                    universidades.add(unv);
                                }
                            }

                            carregouUniversidades = true;
                        }
                    });
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
        this.carregouUniversidades = false;
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
                    carregouUniversidades = true;
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

    public void carregaUniversidadeByAcesso(Acesso acesso){
        this.carregouUniversidades = false;
        universidades = new ArrayList<Universidade>();
        firebaseFirestore.collection("universidade").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("id", acesso.getUniversidadeId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Universidade uni = (Universidade) document.toObject(Universidade.class);
                        universidades.add(uni);
                    }
                }
                carregouUniversidades = true;
            }
        });
    }

    //endregion

    //region Usuario
    public void carregaUsuarios() {
        this.carregouUsuarios = false;
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
                carregouUsuarios = true;
            }
        });
    }


    public void carregaUsuarioById(String id) {
        usuarios = new ArrayList<>();
        carregouUsuariosAcesso = false;

        if (id != null && id.length() > 0) {
            DocumentReference documento = firebaseFirestore.collection("usuario").document(id);
            documento.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Usuario us  = task.getResult().toObject(Usuario.class);
                        usuarios.add(us);
                    }

                    carregouUsuariosAcesso = true;
                }
            });
        }
    }

    public void carregaUsuariosByAcessos(List<Acesso> acessos){
        if(usuariosComAcesso == null)
            usuariosComAcesso = new ArrayList<>();

        if(usuariosIds == null)
            usuariosIds = new ArrayList<>();

        for (Acesso ac: acessos) {
            if(!usuariosIds.contains(ac.getUsuarioId())){
                usuariosIds.add(ac.getUsuarioId());
            }
        }

        carregaUsuarios(usuariosIds);
    }

    private void carregaUsuarios(List<String> usuariosIds) {
        if(usuariosIds.size() == 0){
            carregouUsuariosAcesso = true;
        }else{
            firebaseFirestore.collection("usuario").orderBy("nome")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereIn("id", usuariosIds)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Usuario usr = (Usuario) document.toObject(Usuario.class);
                            if(!usuariosComAcesso.contains(usr))
                                usuariosComAcesso.add(usr);
                        }

                        carregouUsuariosAcesso = true;
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
                    carregouEstados = true;
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
        atendidoTipos = new ArrayList<>();
        firebaseFirestore.collection("atendidoTipo").orderBy("nome")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AtendidoTipo at = (AtendidoTipo) document.toObject(AtendidoTipo.class);
                        atendidoTipos.add(at);
                    }
                }
            }
        });
    }

    public void verificaUsuarioCadastrado(final Usuario usuario){
        usuarioVerificado = new Usuario();

        firebaseFirestore.collection("usuario")
                .whereEqualTo("cpf", usuario.getCpf())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().getDocuments().size() > 0){
                        usuarioVerificado = (Usuario) task.getResult().getDocuments().get(0).toObject(Usuario.class);
                        usuarioVerificado.setMensagem("Já existe um usuário com o CPF informado!");
                    }

                    pesquisouUsuarioJahCadastrado = true;
                }
            }
        });
    }

    //endregion

    //region Acesso

    public void getAutenticar(final Usuario usuario) {
        usuarioAtual = new Usuario();
        acessos = new ArrayList<>();

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
                        carregaAcessos(usuarioAtual.getId());
                    }

                    if(!usuarioAtual.temId()) {
                        usuarioAtual.setMensagem("Usuário não foi encontrado!");
                    }
                }
            }
        });
    }

    public void aprovarAcesso(Acesso acesso){
        podeFinalizarTela = false;
        final Acesso acessoSolicitado = acesso;

        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", acesso.getUsuarioId())
                .whereEqualTo("unidadeId", acesso.getUnidadeId())
                .whereEqualTo("universidadeId", acesso.getUniversidadeId())
                .whereEqualTo("regiaoId", acesso.getRegiaoId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        Acesso ac = (Acesso) doc.toObject(Acesso.class);

                        if(acessoSolicitado.getNivelAcesso() > ac.getNivelAcesso()) {
                            if (acessoSolicitado.isAluno())
                                ac.setAluno(true);

                            if (acessoSolicitado.isProfessor())
                                ac.setProfessor(true);

                            if (acessoSolicitado.isRepresentante())
                                ac.setRepresentante(true);

                            if (acessoSolicitado.isModerador())
                                ac.setModerador(true);
                        }

                        persistirObjeto(ac);
                        removerAcesso(acessoSolicitado);
                    }else{
                        acessoSolicitado.setStatus(StatusEnum.ATIVO);
                        persistirObjeto(acessoSolicitado);
                    }
                    podeFinalizarTela = true;
                }
            }
        });
    }

    private void removerAcesso(Acesso ac) {
        firebaseFirestore.collection("acesso").document(ac.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    int i = 1;
                }
            }
        });
    }

    public void validarAcessoMenor(final Acesso acesso){
        final Acesso acessoSolicitado = acesso;
        podeGravarAcesso = true;
        pesquisouAcessoJahCadastrado = false;

        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", acesso.getUsuarioId())
                .whereEqualTo("unidadeId", acesso.getUnidadeId())
                .whereEqualTo("universidadeId", acesso.getUniversidadeId())
                .whereEqualTo("regiaoId", acesso.getRegiaoId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        Acesso ac = (Acesso) doc.toObject(Acesso.class);
                        if(ac.getNivelAcesso() >= acessoSolicitado.getNivelAcesso()){
                            podeGravarAcesso = false;
                            acesso.setMensagem("Acesso maior ou igual já cadastrado!");
                        }
                    }
                }

                pesquisouAcessoJahCadastrado = true;
            }
        });

    }

    public void verificarAcessoDuplicado(final Acesso acesso, final StatusEnum status){
        pesquisouAcessoJahCadastrado = false;
        pesquisouAcessoJahSolicitado = false;
        podeGravarAcesso = true;
        acessoBanco = acesso;

        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", acesso.getUsuarioId())
                .whereEqualTo("unidadeId", acesso.getUnidadeId())
                .whereEqualTo("universidadeId", acesso.getUniversidadeId())
                .whereEqualTo("regiaoId", acesso.getRegiaoId())
                .whereEqualTo("status", status)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() > 0) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                Acesso ac = (Acesso) doc.toObject(Acesso.class);
                                if (!ac.getId().equals(acessoBanco.getId())) {
                                    podeGravarAcesso = false;

                                    if(status.equals(StatusEnum.RASCUNHO)){
                                        acesso.setMensagem("Acesso já SOLICITADO!");
                                    }
                                }
                            }
                        }
                        pesquisouAcessoJahCadastrado = true;
                        pesquisouAcessoJahSolicitado = true;
                    }
                }
            });
//        }
    }

    private void carregaAcessos(StatusEnum status){
        this.acessosRegistrados = new ArrayList<>();

        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", status)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac  = (Acesso) document.toObject(Acesso.class);
                        acessosRegistrados.add(ac);
                    }

                    carregaUsuariosByAcessos(acessosRegistrados);
                }
            }
        });
    }

    public void carregaAcessos(String usuarioId) {
        this.acessos = new ArrayList<>();
        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac  = (Acesso) document.toObject(Acesso.class);
                        if(!acessos.contains(ac)){
                            acessos.add(ac);
                        }
                    }
                    usuarioAtual.setAcessos(acessos);
                }
            }
        });
    }

    public void carregaUnidadeUniversidadeRegiaoByAcesso(Acesso acesso){
        this.carregouAcessosPossiveis = false;
        this.carregouUniversidades = false;

        this.regioes = new ArrayList<>();
        this.unidades = new ArrayList<>();
        this.universidades = new ArrayList<>();

        if(acesso.getNivelAcesso() >= 1L && acesso.getNivelAcesso() <= 4L){
            this.carregaUniversidadeByAcesso(acesso);
        }
        else if (acesso.getNivelAcesso() == 5L){
            this.carregaUnidadesUniversidades(Arrays.asList(acesso.getUnidadeId()));
        }
        else if (acesso.getNivelAcesso() == 6L) {
            this.carregaUnidadesUniversidadesByRegiaoId(acesso.getRegiaoId());
        }else if(acesso.getNivelAcesso() == 7L){
                carregaRegioes();
                carregaUnidades();
                carregaUniversidades();
        }

        this.carregouAcessosPossiveis = true;
    }

    public void carregaAcessosLimitado(Acesso acessoLogado){
        this.carregouUsuariosAcesso = false;
        this.acessosRegistrados = new ArrayList<>();
        this.usuariosComAcesso = new ArrayList<>();

        if(acessoLogado.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor())){
            if(acessoLogado.isRepresentante() || acessoLogado.isProfessor()){
                carregaAcessosByUniversidadeId(Arrays.asList(acessoLogado.getUniversidadeId()), StatusEnum.ATIVO);
            }
            else if (acessoLogado.isAluno()){
                carregaAcessosProprio(acessoLogado.getUsuarioId());
            }
        }
        else if (acessoLogado.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor())){
                if(acessoLogado.isRepresentante()){
                    carregaAcessosByUnidadeId(Arrays.asList(acessoLogado.getUnidadeId()), StatusEnum.ATIVO);
                }
                else
                    this.carregouUsuariosAcesso = true;
        }
        else if (acessoLogado.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())){
            if(acessoLogado.isRepresentante()){
                carregaAcessosByRegiaoId(acessoLogado.getRegiaoId(), StatusEnum.ATIVO);
            }
            else if (acessoLogado.isModerador()){
                carregaAcessos(StatusEnum.ATIVO);
            }
            else
                this.carregouUsuariosAcesso = true;
        }
    }

    public void carregaSolicitacoes(Acesso acessoLogado){
        this.carregouUsuariosAcesso = false;
        this.acessosRegistrados = new ArrayList<>();
        this.usuariosComAcesso = new ArrayList<>();

        if(acessoLogado.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor())){
            if(acessoLogado.isProfessor()){
                carregaAcessosByUniversidadeId(Arrays.asList(acessoLogado.getUniversidadeId()), StatusEnum.RASCUNHO);
            }
        }
        else if (acessoLogado.getTipoValor().equals(AcessoTipoEnum.UNIDADE.getValor())){
            if(acessoLogado.isRepresentante()){
                carregaAcessosByUnidadeId(Arrays.asList(acessoLogado.getUnidadeId()), StatusEnum.RASCUNHO);
            }
        }
        else if (acessoLogado.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())){
            if(acessoLogado.isRepresentante()){
                carregaAcessosByRegiaoId(acessoLogado.getRegiaoId(), StatusEnum.RASCUNHO);
            }
            else if (acessoLogado.isModerador()){
                carregaAcessos(StatusEnum.RASCUNHO);
            }
            else
                this.carregouUsuariosAcesso = true;
        }
    }

    public void carregaAcessosByRegiaoId(String regiaoId, StatusEnum status){
        final StatusEnum statusFinal = status;

        if(acessosRegistrados == null){
            acessosRegistrados = new ArrayList<>();
        }

        firebaseFirestore.collection("acesso")
                .whereEqualTo("regiaoId", regiaoId)
                .whereEqualTo("status", status)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = (Acesso) document.toObject(Acesso.class);
                        if(ac.getNivelAcesso() <= getAcessoAtual().getNivelAcesso())
                            acessosRegistrados.add(ac);
                    }
                }
            }
        });

        final List<String> listaUniIds = new ArrayList<>();

        firebaseFirestore.collection("unidade")
                .whereEqualTo("regiaoId", regiaoId)
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Unidade uni = (Unidade) document.toObject(Unidade.class);
                        listaUniIds.add(uni.getId());
                    }
                }
                carregaAcessosByUnidadeId(listaUniIds, statusFinal);
            }
        });
    }

    public void carregaAcessosByUnidadeId(List<String> unidadeLista, StatusEnum status) {
        final StatusEnum statusFinal = status;

        if (acessosRegistrados == null){
            acessosRegistrados = new ArrayList<>();
        }

        final List<String> unidadesId = unidadeLista;

        if(unidadesId.size() == 0 ){
            carregaAcessosByUniversidadeId(unidadesId, statusFinal);
        }else{
            firebaseFirestore.collection("acesso")
                    .whereIn("unidadeId", unidadeLista )
                    .whereEqualTo("status", status)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Acesso ac = document.toObject(Acesso.class);
                            if(ac.getNivelAcesso() <= getAcessoAtual().getNivelAcesso())
                                acessosRegistrados.add(ac);
                        }
                        carregaUniversidadeByUnidade(unidadesId, statusFinal);
                    }
                }
            });
        }
    }

    public void carregaUniversidadeByUnidade(List<String> unidadesId, StatusEnum status){
        final StatusEnum statusFinal = status;

        firebaseFirestore.collection("universidade")
                .whereIn("unidadeId", unidadesId )
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> universidadesId = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Universidade un = document.toObject(Universidade.class);
                        universidadesId.add(un.getId());
                    }

                    carregaAcessosByUniversidadeId(universidadesId, statusFinal);
                }
            }
        });
    }

    public void carregaAcessosByUniversidadeId(List<String> universidadeLista, StatusEnum status) {
        if (acessosRegistrados == null){
            acessosRegistrados = new ArrayList<>();
        }

        if(universidadeLista.size() == 0){
            carregaUsuariosByAcessos(acessosRegistrados);
        }else{
            firebaseFirestore.collection("acesso")
                    .whereIn("universidadeId", universidadeLista )
                    .whereEqualTo("status", status)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Acesso ac = document.toObject(Acesso.class);
                            if(ac.getNivelAcesso() <= getAcessoAtual().getNivelAcesso())
                                acessosRegistrados.add(ac);
                        }

                        // passo base recursão de hierarquia de acesso
                        carregaUsuariosByAcessos(acessosRegistrados);
                    }
                }
            });
        }
    }

    public void carregaAcessosProprio(String usuarioId) {
        if (acessosRegistrados == null){
            acessosRegistrados = new ArrayList<>();
        }

        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", usuarioId )
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        if(ac.getNivelAcesso() <= getAcessoAtual().getNivelAcesso())
                            acessosRegistrados.add(ac);
                    }

                    carregaUsuariosByAcessos(acessosRegistrados);
                }
            }
        });
    }

    public void carregaAcessosByUniversidadeIdLimitado(List<String> universidadeLista) {
        if (acessosRegistrados == null){
            acessosRegistrados = new ArrayList<>();
        }

        firebaseFirestore.collection("acesso")
                .whereIn("universidadeId", universidadeLista )
                .whereEqualTo("coordenador", false)
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        acessosRegistrados.add(ac);
                    }
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

    public void carregaEstados() {
        carregouEstados = false;
//        if (versao == 0) {
            getEstadosLocal();
            carregouEstados = true;
//        } else {
//            carregaEstadosCidades();
//        }
    }

    public ArrayList<Estado> getEstados() {
        return estados;
    }

    public void carregaUnidadesTipo() {
        if (versao == 0) {
            getUnidadesTipoLocal();
        } else {
            getUnidadesTipoBanco();
        }
    }

    public ArrayList<UnidadeTipo> getUnidadesTipo() {
        return unidadeTipos;
    }

    public void carregaAtendidos() {
        if (versao == 0) {
            getAtendidosLocal();
        } else {
            getAtendidosBanco();
        }
    }

    public List<AtendidoTipo> getAtendido() {
        return atendidoTipos;
    }

    public ArrayList<Cidade> getCidades(Estado est) {
        ArrayList<Cidade> cidadesLista = new ArrayList<>();
        for(Cidade cid: cidades){
            if(cid.getEstadoId().equals(est.getId()))
                cidadesLista.add(cid);
        }

        return cidadesLista;
    }

    public Unidade getUnidadeById(String unidadeId) {
        for(Unidade und: unidades){
            if(und.getId().equals(unidadeId))
                return und;
        }

        return null;
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

    public void setEstados(ArrayList<Estado> estados) {
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

    public List<AtendidoTipo> getAtendidoTipos() {
        return atendidoTipos;
    }

    public void setAtendidoTipos(List<AtendidoTipo> atendidoTipos) {
        this.atendidoTipos = atendidoTipos;
    }

    public ArrayList<Cidade> getCidades() {
        return cidades;
    }

    public void setCidades(ArrayList<Cidade> cidades) {
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

    public ArrayList<Acesso> getUsuarioAcesso() {
        return usuarioAcesso;
    }

    public void setUsuarioAcesso(ArrayList<Acesso> usuarioAcesso) {
        this.usuarioAcesso = usuarioAcesso;
    }

    public ArrayList<Cidade> getCidadePorEstado() {
        return cidadePorEstado;
    }

    public void setCidadePorEstado(ArrayList<Cidade> cidadePorEstado) {
        this.cidadePorEstado = cidadePorEstado;
    }

    public boolean isPesquisouUsuarioJahCadastrado() {
        return pesquisouUsuarioJahCadastrado;
    }

    public void setPesquisouUsuarioJahCadastrado(boolean pesquisouUsuarioJahCadastrado) {
        this.pesquisouUsuarioJahCadastrado = pesquisouUsuarioJahCadastrado;
    }

    public Usuario getUsuarioVerificado() {
        return usuarioVerificado;
    }

    public void setUsuarioVerificado(Usuario usuarioVerificado) {
        this.usuarioVerificado = usuarioVerificado;
    }

    public boolean isCarregouUsuariosAcesso() {
        return carregouUsuariosAcesso;
    }

    public void setCarregouUsuariosAcesso(boolean carregouUsuariosAcesso) {
        this.carregouUsuariosAcesso = carregouUsuariosAcesso;
    }

    public ArrayList<Acesso> getAcessosRegistrados() {
        return acessosRegistrados;
    }

    public void setAcessosRegistrados(ArrayList<Acesso> acessosRegistrados) {
        this.acessosRegistrados = acessosRegistrados;
    }

    public ArrayList<Usuario> getUsuariosComAcesso() {
        return usuariosComAcesso;
    }

    public void setUsuariosComAcesso(ArrayList<Usuario> usuariosComAcesso) {
        this.usuariosComAcesso = usuariosComAcesso;
    }

    public Usuario getUsuarioCarregado() {
        return usuarioCarregado;
    }

    public void setUsuarioCarregado(Usuario usuarioCarregado) {
        this.usuarioCarregado = usuarioCarregado;
    }

    public Acesso getAcessoCarregado() {
        return acessoCarregado;
    }

    public void setAcessoCarregado(Acesso acessoCarregado) {
        this.acessoCarregado = acessoCarregado;
    }

    public boolean isPesquisouAcessoJahCadastrado() {
        return pesquisouAcessoJahCadastrado;
    }

    public void setPesquisouAcessoJahCadastrado(boolean pesquisouAcessoJahCadastrado) {
        this.pesquisouAcessoJahCadastrado = pesquisouAcessoJahCadastrado;
    }

    public boolean isPodeGravarAcesso() {
        return podeGravarAcesso;
    }

    public void setPodeGravarAcesso(boolean podeGravarAcesso) {
        this.podeGravarAcesso = podeGravarAcesso;
    }

    public boolean isPodeFinalizarTela() {
        return podeFinalizarTela;
    }

    public void setPodeFinalizarTela(boolean podeFinalizarTela) {
        this.podeFinalizarTela = podeFinalizarTela;
    }

    public boolean isPesquisouAcessoJahSolicitado() {
        return pesquisouAcessoJahSolicitado;
    }

    public void setPesquisouAcessoJahSolicitado(boolean pesquisouAcessoJahSolicitado) {
        this.pesquisouAcessoJahSolicitado = pesquisouAcessoJahSolicitado;
    }

    //endregion

    public String getNomeColecaoByObjeto(Object obj) {
        String colecao = "";
        if (new Atendimento().equalsClass(obj))
            colecao = "atendimento";
        else if (new Usuario().equalsClass(obj))
            colecao = "usuario";
        else if (new Unidade().equalsClass(obj))
            colecao = "unidade";
        else if(new UnidadeTipo().equalsClass(obj))
            colecao = "unidade_tipo";
        else if (new Estado().equalsClass(obj))
            colecao = "estado";
        else if (new Cidade().equalsClass(obj))
            colecao = "cidade";
        else if (new Universidade().equalsClass(obj))
            colecao = "universidade";
        else if (new Acesso().equalsClass(obj))
            colecao = "acesso";
        else if (new AtendidoTipo().equalsClass(obj))
            colecao = "atendidoTipo";
        else if (new Regiao().equalsClass(obj))
            colecao = "regiao";

        return colecao;
    }

    //region Local
    private void getUnidadesTipoLocal() {
        ArrayList<UnidadeTipo> lista = new ArrayList<UnidadeTipo>();
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
        List<AtendidoTipo> lista = new ArrayList<AtendidoTipo>();
        lista.add(new AtendidoTipo("eJBYdgN73UduYpr3464P", "Pessoa Física"));
        lista.add(new AtendidoTipo("kbU2kPG0ki2EZh68VNmy", "Microempreendedor Individual(MEI)"));
        lista.add(new AtendidoTipo("mtjSYNphM6pfp3Zn7uOB", "Microempresa optante pelo Simples Nacional"));
        lista.add(new AtendidoTipo("3lOsIPApci6rHGXxEOMx", "Empresa de Pequeno porte optante pelo Simples Nacional"));
        lista.add(new AtendidoTipo("5LFQd4NcmU7l80Iscaor", "Entidade sem fins lucrativos"));
        lista.add(new AtendidoTipo("VitxMl0bVODPwFtXz6w3", "Outro"));
        this.atendidoTipos = lista;
    }

    public void getEstadosLocal() {
        this.estados = new ArrayList<>();
        ArrayList<Estado> lista = new ArrayList<Estado>();

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

    public ArrayList<Cidade> getCidadesLocal(Estado estado) {
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
