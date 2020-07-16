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
import java.util.Date;
import java.util.List;

import br.ufms.nafmanager.activities.relatorios.FiltroRanking;
import br.ufms.nafmanager.activities.relatorios.RelatorioObjeto;
import br.ufms.nafmanager.adapters.StatusEnum;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.AcessoTipoEnum;
import br.ufms.nafmanager.model.AtendidoTipo;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.CustomObject;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Participante;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.model.Relatorios;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.UnidadeTipo;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;

public class Persistencia {

    private List<StatusEnum> listagemAtivos = new ArrayList<StatusEnum>();
    //region Atributos
    private Long versao = 1L;

    public boolean carregouUniversidades = false;
    public boolean carregouEstados = false;
    public boolean carregouUsuariosAcesso = false;
    public boolean carregouUsuarios = false;
    public boolean buscouUsuarioPeloId = false;
    public boolean carregouAcessosPossiveis = false;
    public boolean carregouRegioes = false;
    public boolean carregouUnidades = false;
    public boolean carregouAtendimentos = false;

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<UnidadeTipo> unidadeTipos = new ArrayList<>();
    private ArrayList<Estado> estados = new ArrayList<>();
    private ArrayList<Cidade> cidadeLista = new ArrayList<>();
    private ArrayList<Cidade> cidadePorEstado = new ArrayList<>();
    private Estado estadoAtual;
    private Cidade cidadeAtual;
    private ArrayList<AtendimentoTipo> atendimentosTipo = new ArrayList<>();
    private ArrayList<AtendidoTipo> atendidoTipos = new ArrayList<>();
    private ArrayList<Cidade> cidades = new ArrayList<>();
    private ArrayList<Unidade> unidades = new ArrayList<>();
    private ArrayList<Usuario> usuarios = new ArrayList<>();
    private ArrayList<Acesso> participantesRelatorio = new ArrayList<>();
    private ArrayList<Acesso> acessos = new ArrayList<>();
    private ArrayList<Acesso> acessosRegistrados = new ArrayList<>();
    private ArrayList<Usuario> usuariosComAcesso = new ArrayList<>();
    private ArrayList<Universidade> universidades = new ArrayList<>();
    private ArrayList<Regiao> regioes = new ArrayList<Regiao>();
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
    private ArrayList<Atendimento> atendimentosLista = new ArrayList<>();
    private ArrayList<Relatorios> relatorios = new ArrayList<>();
    private RelatorioObjeto relatorio = new RelatorioObjeto();

    public RelatorioObjeto getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(RelatorioObjeto relatorio) {
        this.relatorio = relatorio;
    }

    public ArrayList<Relatorios> getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(ArrayList<Relatorios> relatorios) {
        this.relatorios = relatorios;
    }

    //endregion

    //region Geral

    private static Persistencia mInstance = null;

    public static synchronized Persistencia getInstance() {
        if (null == mInstance) {
            mInstance = new Persistencia();
        }
        return mInstance;
    }

    protected Persistencia() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void Iniciar() {
        this.instanciarLista();
        this.carregaUnidadesTipo();
        this.getAtendimentoTipoLocal();
        this.carregaAtendidos();
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
                        if(!existeRegiao(rg.getId())){
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
        this.regiaoAtual = new Regiao();

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

                    regiaoAtual = reg;
                    carregaRepresentanteRegiao();
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
                        if(!existeUnidade(und.getId())){
                            unidades.add(und);
                        }
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
                        carregouUnidades = true;
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

                    if(unidadesIds.size() >0){
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
                    }else{
                        carregouUniversidades = true;
                    }
                }
            }
        });
    }

    public void carregaUnidadeById(String id) {
        carregouUnidades = false;
        this.unidadeAtual = new Unidade();
        DocumentReference dr =
                firebaseFirestore.collection("unidade").document(id);
                dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    unidadeAtual = (Unidade) document.toObject(Unidade.class);
                    carregaRepresentanteUnidade();
                }
                carregouUnidades = true;
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
                        if(!existeUniversidade(unv.getId())){
                            universidades.add(unv);
                        }
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
                    universidadeAtual = document.toObject(Universidade.class);
                    carregaRepresentanteUniversidade();
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

    public boolean carregouRepresentantes = false;

    public void carregaRepresentanteUniversidade(){
        carregouRepresentantes = false;

        if(universidadeAtual != null && universidadeAtual.getId() != null)
        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("universidadeId", universidadeAtual.getId())
                .whereEqualTo("representante", true)
                .whereEqualTo("tipo", AcessoTipoEnum.UNIVERSIDADE)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        universidadeAtual.addRepresentante(new Usuario(ac.getUsuarioId(), ""));
                        DocumentReference dr =
                                firebaseFirestore.collection("usuario").document(ac.getUsuarioId());
                        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Usuario usr = document.toObject(Usuario.class);
                                    for(Usuario usuario: universidadeAtual.getRepresentantes()){
                                        if(usr.getId().equals(usuario.getId())){
                                            usuario.setNome(usr.getNome());
                                            usuario.setTelefone(usr.getTelefone());
                                            usuario.setCpf(usr.getCpf());
                                        }
                                    }
                                }
                            }
                        });
                    }
                    carregouRepresentantes = true;
                }
            }
        });
    }

    public void carregaRepresentanteUnidade(){
        carregouRepresentantes = false;

        if(unidadeAtual != null && unidadeAtual.getId() != null)
            firebaseFirestore.collection("acesso")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("unidadeId", unidadeAtual.getId())
                    .whereEqualTo("representante", true)
                    .whereEqualTo("tipo", AcessoTipoEnum.UNIDADE)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Acesso ac = document.toObject(Acesso.class);
                            unidadeAtual.addRepresentante(new Usuario(ac.getUsuarioId(), ""));
                            DocumentReference dr =
                                    firebaseFirestore.collection("usuario").document(ac.getUsuarioId());
                            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        Usuario usr = document.toObject(Usuario.class);
                                        for(Usuario usuario: unidadeAtual.getRepresentantes()){
                                            if(usr.getId().equals(usuario.getId())){
                                                usuario.setNome(usr.getNome());
                                                usuario.setTelefone(usr.getTelefone());
                                                usuario.setCpf(usr.getCpf());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        carregouRepresentantes = true;
                    }
                }
            });
    }

    public void carregaRepresentanteRegiao(){
        carregouRepresentantes = false;

        if(regiaoAtual != null && regiaoAtual.getId() != null)
            firebaseFirestore.collection("acesso")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("regiaoId", regiaoAtual.getId())
                    .whereEqualTo("representante", true)
                    .whereEqualTo("tipo", AcessoTipoEnum.REGIAO)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Acesso ac = document.toObject(Acesso.class);
                            regiaoAtual.addRepresentante(new Usuario(ac.getUsuarioId(), ""));
                            DocumentReference dr =
                                    firebaseFirestore.collection("usuario").document(ac.getUsuarioId());
                            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        Usuario usr = document.toObject(Usuario.class);
                                        for(Usuario usuario: regiaoAtual.getRepresentantes()){
                                            if(usr.getId().equals(usuario.getId())){
                                                usuario.setNome(usr.getNome());
                                                usuario.setTelefone(usr.getTelefone());
                                                usuario.setCpf(usr.getCpf());
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        carregouRepresentantes = true;
                    }
                }
            });
    }

    public boolean carregouRepresentantesUniversidade(){
       for(Usuario usuario: universidadeAtual.getRepresentantes()){
           if(usuario.getNome() == null || usuario.getNome().length() == 0){
               return false;
           }
       }

       return true;
    }

    public boolean carregouRepresentantesUnidade(){
        for(Usuario usuario: unidadeAtual.getRepresentantes()){
            if(usuario.getNome() == null || usuario.getNome().length() == 0){
                return false;
            }
        }

        return true;
    }

    public boolean carregouRepresentantesRegiao(){
        for(Usuario usuario: regiaoAtual.getRepresentantes()){
            if(usuario.getNome() == null || usuario.getNome().length() == 0){
                return false;
            }
        }

        return true;
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
                .whereEqualTo("senha", Usuario.criarHashSha256(usuario.getSenha()))
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

    public void verificarAcessoSolicitado(final Acesso acesso){
        pesquisouAcessoJahCadastrado = false;
        podeGravarAcesso = true;
        acessoBanco = acesso;

        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", acesso.getUsuarioId())
                .whereEqualTo("unidadeId", acesso.getUnidadeId())
                .whereEqualTo("universidadeId", acesso.getUniversidadeId())
                .whereEqualTo("regiaoId", acesso.getRegiaoId())
                .whereEqualTo("status", StatusEnum.RASCUNHO)
                .whereEqualTo("aluno", acesso.isAluno())
                .whereEqualTo("professor", acesso.isProfessor())
                .whereEqualTo("representante", acesso.isRepresentante())
                .whereEqualTo("moderador", acesso.isModerador())
                .whereEqualTo("tipo", acesso.getTipo())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() > 0) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                Acesso ac = (Acesso) doc.toObject(Acesso.class);
                                if (!ac.getId().equals(acessoBanco.getId())) {
                                    podeGravarAcesso = false;
                                }
                            }
                        }
                        pesquisouAcessoJahSolicitado = true;
                    }
                }
            });
//        }
    }

    public void verificarAcessoExistente(final Acesso acesso){
        pesquisouAcessoJahCadastrado = false;
        podeGravarAcesso = true;
        acessoBanco = acesso;

        firebaseFirestore.collection("acesso")
                .whereEqualTo("usuarioId", acesso.getUsuarioId())
                .whereEqualTo("unidadeId", acesso.getUnidadeId())
                .whereEqualTo("universidadeId", acesso.getUniversidadeId())
                .whereEqualTo("regiaoId", acesso.getRegiaoId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("aluno", acesso.isAluno())
                .whereEqualTo("professor", acesso.isProfessor())
                .whereEqualTo("representante", acesso.isRepresentante())
                .whereEqualTo("moderador", acesso.isModerador())
                .whereEqualTo("tipo", acesso.getTipo())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getDocuments().size() > 0) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Acesso ac = (Acesso) doc.toObject(Acesso.class);
                            if (!ac.getId().equals(acessoBanco.getId())) {
                                podeGravarAcesso = false;
                            }
                        }
                    }
                    pesquisouAcessoJahCadastrado = true;
                }
            }
        });
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
//                        for(Acesso acesso: acessos){
//                            if(acesso.getId().equals(ac.getId()))
//                                break;
//                        }

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
        else if (acessoLogado.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())) {
            if (acessoLogado.isRepresentante()) {
                carregaAcessosByRegiaoId(acessoLogado.getRegiaoId(), StatusEnum.ATIVO);
            }
        }
        else if (acessoLogado.isModerador() && acessoLogado.getTipoValor().equals(AcessoTipoEnum.MODERADOR.getValor())){
            carregaAcessos(StatusEnum.ATIVO);
        }
        else{
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
        else if (acessoLogado.getTipoValor().equals(AcessoTipoEnum.REGIAO.getValor())) {
            if (acessoLogado.isRepresentante()) {
                carregaAcessosByRegiaoId(acessoLogado.getRegiaoId(), StatusEnum.RASCUNHO);
            }
        }
        else if (acessoLogado.getTipoValor().equals(AcessoTipoEnum.MODERADOR.getValor())) {
            if (acessoLogado.isModerador()){
                carregaAcessos(StatusEnum.RASCUNHO);
            }
        }
        else
            this.carregouUsuariosAcesso = true;

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

    private boolean validouInserirRegiao = false;
    public boolean isValidouInserirRegiao() {
        return validouInserirRegiao;
    }

    private boolean podeInserirRegiao = false;
    public boolean isPodeInserirRegiao() {
        return podeInserirRegiao;
    }

    public void validarInserirRegiao(final Regiao regiao){
        podeInserirRegiao = true;
        validouInserirRegiao = false;

        firebaseFirestore.collection("regiao")
                .whereArrayContainsAny("estados", regiao.getEstados())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Regiao reg = document.toObject(Regiao.class);
                        if(!reg.getId().equals(regiao.getId()))
                            podeInserirRegiao = false;
                    }
                }

                validouInserirRegiao = true;
            }
        });
    }

    //endregion

    //region Relatorios
    public void carregaAtendimentos(String acessoId, Date dataInicial, Date dataFinal){
        dataFinal.setHours(23);
        dataFinal.setMinutes(59);
        dataFinal.setSeconds(59);

        atendimentosLista = new ArrayList<>();
        carregouAtendimentos = false;

        firebaseFirestore.collection("atendimento")
                .whereEqualTo("acessoId", acessoId )
                .whereGreaterThanOrEqualTo("dataAtendimento", dataInicial)
                .whereLessThan("dataAtendimento",dataFinal)
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Atendimento atendimento = document.toObject(Atendimento.class);
                        atendimentosLista.add(atendimento);
                    }
                    carregouAtendimentos = true;
                }
            }
        });
    }

    public void carregaAtendimentosTempoMedio(final Date dataInicial, final Date dataFinal, final String atendidoTipoId, final String universidadeId) {
        dataFinal.setHours(23);
        dataFinal.setMinutes(59);
        dataFinal.setSeconds(59);
        final ArrayList<String> acessos = new ArrayList<>();

        carregouAtendimentos = false;
        relatorios = new ArrayList<>();

        if (universidadeId != null && universidadeId.length() > 0) {// com filtro de universidade
            firebaseFirestore.collection("acesso")
                    .whereEqualTo("universidadeId", universidadeId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Acesso ac = document.toObject(Acesso.class);
                            acessos.add(ac.getId());
                        }

                        if (atendidoTipoId != null && atendidoTipoId.length() > 0) {
                            firebaseFirestore.collection("atendimento")
                                    .whereIn("acessoId", acessos)
                                    .whereArrayContains("atendimentoTipoId", atendidoTipoId)
                                    .whereGreaterThanOrEqualTo("dataAtendimento", dataInicial)
                                    .whereLessThan("dataAtendimento", dataFinal)
                                    .whereEqualTo("retroativo", false)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Participante part = new Participante();
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Atendimento at = document.toObject(Atendimento.class);
                                            part.addAtendimento(at);
                                        }
                                        if (part.getAtendimentos().size() > 0) {
                                            Relatorios relatorio = new Relatorios();
                                            relatorio.addParticipante(part);
                                            relatorios.add(relatorio);
                                        }

                                        carregouAtendimentos = true;
                                    }
                                }
                            });
                        } else {
                            firebaseFirestore.collection("atendimento")
                                    .whereIn("acessoId", acessos)
                                    .whereGreaterThanOrEqualTo("dataAtendimento", dataInicial)
                                    .whereLessThan("dataAtendimento", dataFinal)
                                    .whereEqualTo("retroativo", false)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Participante part = new Participante();
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Atendimento at = document.toObject(Atendimento.class);
                                            part.addAtendimento(at);
                                        }
                                        if (part.getAtendimentos().size() > 0) {
                                            Relatorios relatorio = new Relatorios();
                                            relatorio.addParticipante(part);
                                            relatorios.add(relatorio);
                                        }

                                        carregouAtendimentos = true;
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else { // Sem filtro de universidade
            if (atendidoTipoId != null && atendidoTipoId.length() > 0) {
                firebaseFirestore.collection("atendimento")
                        .whereArrayContains("atendimentoTipoId", atendidoTipoId)
                        .whereGreaterThanOrEqualTo("dataAtendimento", dataInicial)
                        .whereLessThan("dataAtendimento", dataFinal)
                        .whereEqualTo("retroativo", false)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Participante part = new Participante();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Atendimento at = document.toObject(Atendimento.class);
                                part.addAtendimento(at);
                            }
                            if (part.getAtendimentos().size() > 0) {
                                Relatorios relatorio = new Relatorios();
                                relatorio.addParticipante(part);
                                relatorios.add(relatorio);
                            }

                            carregouAtendimentos = true;
                        }
                    }
                });
            } else {
                firebaseFirestore.collection("atendimento")
                        .whereGreaterThanOrEqualTo("dataAtendimento", dataInicial)
                        .whereLessThan("dataAtendimento", dataFinal)
                        .whereEqualTo("retroativo", false)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Participante part = new Participante();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Atendimento at = document.toObject(Atendimento.class);
                                part.addAtendimento(at);
                            }
                            if (part.getAtendimentos().size() > 0) {
                                Relatorios relatorio = new Relatorios();
                                relatorio.addParticipante(part);
                                relatorios.add(relatorio);
                            }

                            carregouAtendimentos = true;
                        }
                    }
                });
            }
        }
    }

    private ArrayList<RelatorioObjeto> regiaoObjeto = new ArrayList<>();
    private ArrayList<RelatorioObjeto> unidadeObjeto = new ArrayList<>();
    private ArrayList<RelatorioObjeto> universidadeObjeto = new ArrayList<>();
    private ArrayList<RelatorioObjeto> unidadeRepresentanteObjeto = new ArrayList<>();

    private boolean estatisticasUniversidade = false;
    private boolean estatisticasRegiao = false;
    private boolean estatisticasUsuario = false;
    private boolean estatisticarepresentanteUnidade = false;
    private boolean estatisticasAcesso = false;
    private boolean estatisticasByCidadeCarregadas = false;

    public boolean isEstatisticasByCidadeCarregadas() {
        return estatisticasByCidadeCarregadas;
    }

    public boolean isEstatisticasUniversidade() {
        return estatisticasUniversidade;
    }
    public boolean isEstatisticasRegiao() {
        return estatisticasRegiao;
    }
    public boolean isEstatisticasUsuario() {
        return estatisticasUsuario;
    }
    public boolean isEstatisticasAcesso() {
        return estatisticasAcesso;
    }

    public ArrayList<RelatorioObjeto> getUniversidadeObjeto() {
        return universidadeObjeto;
    }

    public boolean estatisticasUsuarioComAcesso(){
        for(RelatorioObjeto universidade: universidadeObjeto){
            for(RelatorioObjeto usuario: universidade.getDetalhe()){
                if(usuario.getNome() == null || usuario.getNome().length() == 0 ){
                    return false;
                }
            }
        }

        return true;
    }

    public void instanciarEstatisticas(){
        estatisticasUniversidade = false;
        estatisticasRegiao = false;
        estatisticasUsuario = false;
        estatisticarepresentanteUnidade = false;

        cadastralRegiao = false;
        cadastralUnidade = false;
        cadastralUniversidade = false;

        regiaoObjeto = new ArrayList<>();
        unidadeObjeto = new ArrayList<>();
        universidadeObjeto = new ArrayList<>();
        unidadeRepresentanteObjeto = new ArrayList<>();
    }

    private boolean cadastralRegiao = false;
    private boolean cadastralUnidade = false;
    private boolean cadastralUniversidade = false;

    public boolean isCadastralRegiao() {
        return cadastralRegiao;
    }
    public boolean isCadastralUnidade() {
        return cadastralUnidade;
    }
    public boolean isCadastralUniversidade() {
        return cadastralUniversidade;
    }

    //region Relatorio Cadastral
    public void relatorioCadastralByRegiao(String regiaoId){
        if(regiaoId != null){
            for(Unidade unidade: unidades){
                if(unidade.getRegiaoId().equals(regiaoId)){
                    RelatorioObjeto un = new RelatorioObjeto(unidade.getId(), unidade.getNome());
                    un.setValor1(unidade.getRegiaoId());
                    for(Universidade universidade: universidades){
                        if(universidade.getUnidadeId().equals(un.getId())){
                            RelatorioObjeto uni = new RelatorioObjeto(universidade.getId(), universidade.getNome());
                            uni.setValor1(un.getId());
                            cadastralBuscarParticipantes(uni);
                            un.addDetalhe(uni);
                        }
                    }

                unidadeObjeto.add(un);
                }
            }
        }
        else {
            for (Unidade unidade : unidades) {
                RelatorioObjeto un = new RelatorioObjeto(unidade.getId(), unidade.getNome());
                un.setValor1(unidade.getRegiaoId());
                for (Universidade universidade : universidades) {
                    if (universidade.getUnidadeId().equals(un.getId())) {
                        RelatorioObjeto uni = new RelatorioObjeto(universidade.getId(), universidade.getNome());
                        uni.setValor1(un.getId());
                        cadastralBuscarParticipantes(uni);
                        un.addDetalhe(uni);
                    }
                }

                unidadeObjeto.add(un);
            }
        }

        relatorio = new RelatorioObjeto();
        for(RelatorioObjeto unidade: unidadeObjeto){
            relatorio.addDetalhe(unidade);
        }
    }

    private void cadastralBuscarParticipantes(final RelatorioObjeto universidade) {
        firebaseFirestore.collection("acesso")
                .whereEqualTo("universidadeId", universidade.getId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso acesso = document.toObject(Acesso.class);

                        if(acesso.getNivelAcesso() <= 2L){
                            RelatorioObjeto ac = new RelatorioObjeto(acesso.getId(), "");
                            ac.setValor1(universidade.getId());
                            ac.marcarDetalhesFinalizados();
                            universidade.addDetalhe(ac);
                        }
                    }
                }
            }
        });
    }

    public boolean isMarcarFinalizada(){
        for(RelatorioObjeto unidade: unidadeObjeto) {
            if (!unidade.isDetalhesFinalizados()) {
                return false;
            }
        }
        return true;
    }

    public void cadastralMarcarFinalizadaReflexiva(){
        for(RelatorioObjeto unidade: unidadeObjeto){
            for(RelatorioObjeto universidade: unidade.getDetalhe()){
                for(RelatorioObjeto participante: universidade.getDetalhe()){
                    if(!participante.isDetalhesFinalizados()){
                        return;
                    }

                    participante.marcarDetalhesFinalizados();
                }

                universidade.marcarDetalhesFinalizados();
            }

            unidade.marcarDetalhesFinalizados();
        }
    }

    public void relatorioCadastralByEstado(String estadoId){
        ArrayList<Cidade> cidadesFiltradas = getCidadesLocal(estadoId);

        for(Unidade unidade: unidades){
            for(Cidade cidade: cidadesFiltradas) {
                if(cidade.getId().equals(unidade.getCidadeId())) {
                    RelatorioObjeto un = new RelatorioObjeto(unidade.getId(), unidade.getNome());
                    un.setValor1(unidade.getRegiaoId());
                    for (Universidade universidade : universidades) {
                        if (universidade.getUnidadeId().equals(un.getId())) {
                            RelatorioObjeto uni = new RelatorioObjeto(universidade.getId(), universidade.getNome());
                            uni.setValor1(un.getId());
                            cadastralBuscarParticipantes(uni);
                            un.addDetalhe(uni);
                        }
                    }

                    unidadeObjeto.add(un);
                }
            }
        }

        relatorio = new RelatorioObjeto();
        for(RelatorioObjeto unidade: unidadeObjeto){
            relatorio.addDetalhe(unidade);
        }
    }

    public void relatorioCadastralByCidade(String cidadeId){

        ArrayList<String> unidadeIds = new ArrayList<>();

        for(Unidade unidade: unidades){
           if(unidade.getCidadeId().equals(cidadeId)){
                RelatorioObjeto unid = new RelatorioObjeto(unidade.getId(), unidade.getNome());
                unidadeObjeto.add(unid);
                unidadeIds.add(unidade.getId());
           }
        }

        RelatorioObjeto naoRelacionado = new RelatorioObjeto(null, "Unidades não relacionadas no filtro");

        for(Universidade universidade: universidades){
            if(universidade.getCidadeId().equals(cidadeId)){
                RelatorioObjeto uni = new RelatorioObjeto(universidade.getId(), universidade.getNome());
                cadastralBuscarParticipantes(uni);

                if(!unidadeIds.contains(universidade.getUnidadeId())){
                    uni.setValor1(universidade.getUnidadeId());
                    naoRelacionado.addDetalhe(uni);
                }
                else{
                    for(RelatorioObjeto unidade: unidadeObjeto){
                        if(unidade.getId().equals(universidade.getUnidadeId())){
                            unidade.addDetalhe(uni);
                            break;
                        }
                    }
                }
            }
        }

        if(naoRelacionado.getDetalhe().size() > 0){
            unidadeObjeto.add(naoRelacionado);
        }

        relatorio = new RelatorioObjeto();
        for(RelatorioObjeto unidade: unidadeObjeto){
            relatorio.addDetalhe(unidade);
        }
    }

    //endregion

    //relatorio cadastro
    public void estatisticaUniversidadeByCidadeId(String cidadeId) {
        firebaseFirestore.collection("universidade")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("cidadeId", cidadeId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {// inserindo universidades na unidade
                        Universidade uni = document.toObject(Universidade.class);
                        RelatorioObjeto obj = new RelatorioObjeto(uni.getId(), uni.getNome());
                        obj.setValor1(uni.getUnidadeId());
                        if(!universidadeObjeto.contains(obj)){
                            universidadeObjeto.add(obj);
                        }
                    }
                }

                estatisticasUniversidade = true;
                carregaEstatisticaParticipantes();
            }
        });
    }

    private void carregaEstatisticaParticipantes(){
        for(final RelatorioObjeto universidade: universidadeObjeto){
            firebaseFirestore.collection("acesso")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("universidadeId", universidade.getId())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {// inserindo universidades na unidade
                            Acesso acesso = document.toObject(Acesso.class);
                            RelatorioObjeto part = new RelatorioObjeto(acesso.getId(), "");
                            if(acesso.getNivelAcesso() <= 2 && !universidade.existeDetalhe(part.getId())){
                                universidade.addDetalhe(part);
                            }
                        }
                    }
                estatisticasAcesso = true;
                universidade.marcarDetalhesFinalizados();
                }
            });
        }
    }

    public void carregaEstatisticaUnidades(){
        final ArrayList<String> unidadesId = new ArrayList<>();
        for(final RelatorioObjeto universidade: universidadeObjeto){
            if(!unidadesId.contains(universidade.getValor1())){
                unidadesId.add(universidade.getValor1());//unidade Id
            }
        }

        if(unidadesId.size() > 0){
            firebaseFirestore.collection("unidade")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereIn("id", unidadesId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {// inserindo universidades na unidade
                            Unidade un = document.toObject(Unidade.class);
                            RelatorioObjeto obj = new RelatorioObjeto(un.getId(), un.getNome());
                            obj.setValor1(un.getRegiaoId());
                            if(!unidadeObjeto.contains(obj)){
                                unidadeObjeto.add(obj);
                            }
                        }
                    }

                    carregaEstatisticaRegiao();
                }
            });
        }
        else{
            carregaEstatisticaRegiao();
        }
    }

    private void carregaEstatisticaRegiao() {
        final ArrayList<String> regioesId = new ArrayList<>();
        for(final RelatorioObjeto unidade: unidadeObjeto){
            if(!regioesId.contains(unidade.getValor1())){
                regioesId.add(unidade.getValor1());//regiao id
            }
        }

        if(regioesId.size()> 0 ){
            firebaseFirestore.collection("regiao")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereIn("id", regioesId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {// inserindo universidades na unidade
                            Regiao reg = document.toObject(Regiao.class);
                            RelatorioObjeto obj = new RelatorioObjeto(reg.getId(), reg.getNome());
                            if(!regiaoObjeto.contains(obj)){
                                regiaoObjeto.add(obj);
                            }
                        }
                    }
                    estatisticasRegiao = true;
                    ajustarDados();
                }
            });
        }
        else{
            estatisticasRegiao = true;
            ajustarDados();
        }
    }

    private void ajustarDados() {
        for(RelatorioObjeto universidade: universidadeObjeto){
            for(RelatorioObjeto unidade: unidadeObjeto){
                if(unidade.getId().equals(universidade.getValor1())){
                    unidade.addDetalhe(universidade);
                }
            }
        }

        for(RelatorioObjeto unidade: unidadeObjeto){
            for(RelatorioObjeto regiao: regiaoObjeto){
                if(regiao.getId().equals(unidade.getValor1())){
                    regiao.addDetalhe(unidade);
                }
            }
        }

        relatorio = new RelatorioObjeto();
        for(RelatorioObjeto regiao: regiaoObjeto){
            relatorio.addDetalhe(regiao);
            regiao.marcarDetalhesFinalizados();
        }
    }

    public void ajustarDadosRegiao() {
        relatorio = new RelatorioObjeto();
        for(RelatorioObjeto unidade: unidadeObjeto){
            relatorio.addDetalhe(unidade);
            for(RelatorioObjeto universidade: universidadeObjeto){
                if(unidade.getId().equals(universidade.getValor1())){
                    unidade.addDetalhe(universidade);
                    universidade.marcarDetalhesFinalizados();
                }
            }
        }

    }

    public void ajustarDadosUniversidade() {
        relatorio = new RelatorioObjeto();
        for(RelatorioObjeto universidade: universidadeObjeto){
            relatorio.addDetalhe(universidade);
            universidade.marcarDetalhesFinalizados();
        }
    }

    public boolean estatisticaUniversidadesCarregada(){
        if(estatisticasUniversidade){
            for(RelatorioObjeto universidade: universidadeObjeto){
                if(!universidade.isDetalhesFinalizados()){
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public boolean estatisticaRegioesCarregada(){
        if(estatisticasRegiao){
            for(RelatorioObjeto regiao: regiaoObjeto){
                if(!regiao.isDetalhesFinalizados()){
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public void estatisticaUniversidadeByEstadoId(String estadoId) {
        final ArrayList<String> cidadesId = new ArrayList<>();

        Estado estado = new Estado();
        estado.setId(estadoId);

        for(Cidade cidade: getCidades(estado)){
            cidadesId.add(cidade.getId());
        }

        for(String cidade: cidadesId){
            estatisticaUniversidadeByCidadeId(cidade);
        }
    }

    public void estatisticaUnidadeByRegiaoId(final String regiaoId) {
        relatorio = new RelatorioObjeto();
        regiaoObjeto.add(new RelatorioObjeto(regiaoId, ""));

        firebaseFirestore.collection("unidade")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("regiaoId", regiaoId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Unidade un = document.toObject(Unidade.class);
                        RelatorioObjeto obj = new RelatorioObjeto(un.getId(), un.getNome());
                        obj.setValor1(un.getRegiaoId());
                        if(!unidadeObjeto.contains(obj)){
                            unidadeObjeto.add(obj);
                        }
                    }
                    estatisticaUniversidadeByUnidade();
                }
            }
        });
    }

    private void estatisticaUniversidadeByUnidade() {
        for(RelatorioObjeto unidade: unidadeObjeto){
            firebaseFirestore.collection("universidade")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("unidadeId", unidade.getId())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Universidade un = document.toObject(Universidade.class);
                            RelatorioObjeto obj = new RelatorioObjeto(un.getId(), un.getNome());
                            obj.setValor1(un.getUnidadeId());
                            if(!universidadeObjeto.contains(obj)){
                                universidadeObjeto.add(obj);
                                estatisticaParticipanteByUniversidade(obj);
                            }
                        }
                        estatisticasUniversidade = true;
                    }
                }
            });
        }
    }

    private void estatisticaParticipanteByUniversidade(final RelatorioObjeto universidade) {
        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("universidadeId", universidade.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        RelatorioObjeto obj = new RelatorioObjeto(ac.getId(),"");
                        obj.setValor1(ac.getUniversidadeId());
                        if(ac.getNivelAcesso() <= 2 && !universidade.existeDetalhe(obj.getId())){
                            universidade.addDetalhe(obj);
                        }
                    }
                }
                estatisticasAcesso = true;
            }
        });
    }

    //relatorio cadastro
    public void estatisticaRegiaoByUnidade() {
        for(final RelatorioObjeto regiao: regiaoObjeto){
            firebaseFirestore.collection("regiao")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("id", regiao.getId())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Regiao reg = document.toObject(Regiao.class);
                            regiao.setNome(reg.getNome());
                        }
                    }
                    estatisticasRegiao = true;
                    ajustarDados();
                }
            });
        }
    }

    public void estatisticaGeral() {
        firebaseFirestore.collection("universidade")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Universidade un = document.toObject(Universidade.class);
                        RelatorioObjeto obj = new RelatorioObjeto(un.getId(), un.getNome());
                        obj.setValor1(un.getUnidadeId());
                        if(!universidadeObjeto.contains(obj)){
                            universidadeObjeto.add(obj);
                            estatisticaParticipanteByUniversidade(obj);
                        }
                    }
                    estatisticasUniversidade = true;
                }
            }
        });
    }

    //region  relatorios representantes
    //relatorio Representante regiao
    public void estatisticaRegiao(String regiaoId, final Date dataInicial, final Date dataFinal){
        if(regiaoId!= null){
            firebaseFirestore.collection("unidade")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("regiaoId", regiaoId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Unidade un = document.toObject(Unidade.class);
                            RelatorioObjeto obj = new RelatorioObjeto(un.getId(),un.getNome());
                            obj.setValor1(un.getRegiaoId());
                            if(!unidadeObjeto.contains(obj)){
                                unidadeObjeto.add(obj);
                                buscarRepresentanteUnidade(obj);
                                estatisticaUnidade(obj.getId(), dataInicial, dataFinal);
                            }
                        }
                    }
                }
            });
        }
    }

    public void buscarRepresentanteUnidade(RelatorioObjeto unidade){
        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("unidadeId", unidade.getId())
                .whereEqualTo("representante", true)
                .whereEqualTo("tipo", AcessoTipoEnum.UNIDADE)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        RelatorioObjeto obj = new RelatorioObjeto();
                        obj.setId(ac.getUsuarioId());
                        obj.setValor2(ac.getUnidadeId());
                        buscarUsuarioRepresentante(obj);
                    }
                }
                estatisticarepresentanteUnidade = true;
            }
        });
    }

    public void buscarUsuarioRepresentante(final RelatorioObjeto usuario){
        firebaseFirestore.collection("usuario")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("id", usuario.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Usuario usr = document.toObject(Usuario.class);
                        usuario.setNome(usr.getNome());
                        if(usr.getId().equals(usuario.getId())){
                            for(RelatorioObjeto unidade:unidadeObjeto){
                                if(unidade.getId().equals(usuario.getValor2()) && !unidade.existeDetalhe2(usr.getId())){
                                    unidade.addDetalhe2(usuario);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean representantesTraduzidos(){
        if(estatisticarepresentanteUnidade){
            for(RelatorioObjeto representante: unidadeRepresentanteObjeto){
                if(representante.getNome() == null || representante.getNome().length() == 0){
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    //Relatorio Representante unidade
    public void estatisticaUnidade(String unidadeId, final Date dataInicial, final Date dataFinal){
        if(unidadeId!= null){
            firebaseFirestore.collection("universidade")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("unidadeId", unidadeId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Universidade un = document.toObject(Universidade.class);
                            RelatorioObjeto obj = new RelatorioObjeto(un.getId(),un.getNome());
//                            if(!unidadeObjeto.contains(obj)){
//                                unidadeObjeto.add(obj);
                                estatisticaUniversidade(obj.getId(), dataInicial, dataFinal);
//                            }
                        }
                    }
                }
            });
        }
    }

    // relatorio representante universidade
    public void estatisticaUniversidade(String universidadeId, final Date dataInicial, final Date dataFinal){
        if(universidadeId!= null){
            firebaseFirestore.collection("universidade")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("id", universidadeId)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Universidade un = document.toObject(Universidade.class);
                            RelatorioObjeto obj = new RelatorioObjeto(un.getId(),un.getNome());
                            obj.setValor1(un.getUnidadeId());
                            if(!universidadeObjeto.contains(obj)){
                                universidadeObjeto.add(obj);
                                estatisticaAcesso(obj, dataInicial, dataFinal);
                            }
                        }
                    }
                }
            });
        }
    }

    public void estatisticaAcesso(final RelatorioObjeto universidade, final Date dataInicial, final Date dataFinal){
        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("universidadeId", universidade.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso acesso = document.toObject(Acesso.class);
                        RelatorioObjeto participante = new RelatorioObjeto();
                        participante.setValor2(acesso.getUsuarioId());
                        participante.setValor1(acesso.getUniversidadeId());
                        participante.setValor3(acesso.getNivelAcesso().toString());
                        participante.setId(acesso.getId());

                        universidade.addDetalhe(participante);
                    }
                }

                for(RelatorioObjeto participante: universidade.getDetalhe()){
                    estatisticaAtendimentos(participante, dataInicial, dataFinal);
                }
              }
        });
    }

    public void estatisticaAtendimentos(final RelatorioObjeto acesso, Date dataInicial, Date dataFinal){
        dataFinal.setHours(23);
        dataFinal.setMinutes(59);
        dataFinal.setSeconds(59);

        firebaseFirestore.collection("atendimento")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("acessoId", acesso.getId())
                .whereGreaterThanOrEqualTo("dataAtendimento", dataInicial)
                .whereLessThan("dataAtendimento",dataFinal)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Atendimento at = document.toObject(Atendimento.class);
                        acesso.addDetalhe(new RelatorioObjeto(at.getId(), ""));
                    }
                }
                estatisticasUniversidade = true;
                acesso.marcarDetalhesFinalizados();
            }
        });
    }

    public void estatisticaUsuarios(RelatorioObjeto universidade){
        for(final RelatorioObjeto acesso: universidade.getDetalhe()){
            firebaseFirestore.collection("usuario")
                    .whereEqualTo("status", StatusEnum.ATIVO)
                    .whereEqualTo("id", acesso.getValor2())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Usuario us = document.toObject(Usuario.class);
                            acesso.setNome(us.getNome());
                        }
                    }
                }
            });
        }
    }

    public boolean estatisticaAcessoCompleta(){
        if(estatisticasUniversidade) {
            for (RelatorioObjeto universidade : universidadeObjeto) {
                for (RelatorioObjeto acesso : universidade.getDetalhe()) {
                    if (!acesso.isDetalhesFinalizados()) {
                        return false;
                    }
                }

            }
            return true;
        }
        return false;
    }

    private ArrayList<Atendimento> relatorioRanking = new ArrayList<>();
    private boolean carregouRanking = false;
    private RelatorioObjeto estruturaRanking = new RelatorioObjeto();

    public boolean verificarRankingFoiFinalizado(){
        for(RelatorioObjeto rel: estruturaRanking.getDetalhe()){
            if(!rel.isDetalhesFinalizados())
                return false;
        }

        return true;
    }

    public void rankingMarcacaoReflexiva(){
        for(RelatorioObjeto rel: estruturaRanking.getDetalhe()){
           for(RelatorioObjeto filho: rel.getDetalhe()){
               if(!filho.isDetalhesFinalizados())
                   return;
           }
           rel.marcarDetalhesFinalizados();
        }
        estruturaRanking.marcarDetalhesFinalizados();
    }

    public void rankingAtendimentos(FiltroRanking filtro) {
        relatorioRanking = new ArrayList<>();

        if("Participante".equals(filtro.getTipo())){
            RelatorioObjeto rel = new RelatorioObjeto();
            estruturaRanking.addDetalhe(rel);
            rankingParticipante(filtro, rel);
        }
        else if ("Universidade".equals(filtro.getTipo())){
            RelatorioObjeto rel = new RelatorioObjeto();
            rel.setId(filtro.getUniversidadeId());
            estruturaRanking.addDetalhe(rel);
            rankingUniversidade(filtro, rel);
        }
        else if ("Unidade".equals(filtro.getTipo())){
            RelatorioObjeto rel = new RelatorioObjeto();
            rel.setId(filtro.getUnidadeId());
            estruturaRanking.addDetalhe(rel);
            rankingUnidade(filtro, rel);
        }
        else if ("Regiao".equals(filtro)){
            RelatorioObjeto rel = new RelatorioObjeto();
            rel.setId(filtro.getRegiaoId());
            estruturaRanking.addDetalhe(rel);
            rankingRegiao(filtro,rel);
        }
    }

    private void rankingParticipante(final FiltroRanking filtro, final RelatorioObjeto estrutura){
        firebaseFirestore.collection("atendimento")
                .whereEqualTo("acessoId", filtro.getParticipanteId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereGreaterThanOrEqualTo("dataAtendimento", filtro.getDataInicial())
                .whereLessThan("dataAtendimento", filtro.getDataFinal())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Atendimento at = document.toObject(Atendimento.class);
                        if(!relatorioRanking.contains(at))
                            relatorioRanking.add(at);
                    }
                }

                RelatorioObjeto rel = new RelatorioObjeto(filtro.getParticipanteId(), "");
                rel.marcarDetalhesFinalizados();
                if(!estrutura.existeDetalhe(filtro.getParticipanteId()))
                    estrutura.addDetalhe(rel);
            }
        });
    }

    private void rankingUniversidade(final FiltroRanking filtro, final RelatorioObjeto relatorio){
        final ArrayList<String> participanteId = new ArrayList<>();

        firebaseFirestore.collection("acesso")
                .whereEqualTo("universidadeId", filtro.getUniversidadeId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        if(!participanteId.contains(ac.getId()))
                            participanteId.add(ac.getId());
                    }
                }

                for(String part: participanteId){
                    FiltroRanking filtroPart = new FiltroRanking();
                    filtroPart.setDataInicial(filtro.getDataInicial());
                    filtroPart.setDataFinal(filtro.getDataFinal());
                    filtroPart.setParticipanteId(part);

                    RelatorioObjeto rel = new RelatorioObjeto();
                    rel.setId(part);
                    relatorio.addDetalhe(rel);

                    rankingParticipante(filtroPart, rel);
                }
            }
        });
    }

    private void rankingUnidade(final FiltroRanking filtro, final RelatorioObjeto relatorio){
        final ArrayList<String> universidadeId = new ArrayList<>();

        firebaseFirestore.collection("universidade")
                .whereEqualTo("unidadeId", filtro.getUnidadeId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Universidade un  = document.toObject(Universidade.class);
                        if(!universidadeId.contains(un.getId()))
                            universidadeId.add(un.getId());
                    }
                }

                for(String univ: universidadeId){
                    FiltroRanking filtroPart = new FiltroRanking();
                    filtroPart.setDataInicial(filtro.getDataInicial());
                    filtroPart.setDataFinal(filtro.getDataFinal());
                    filtroPart.setUniversidadeId(univ);

                    RelatorioObjeto rel = new RelatorioObjeto();
                    rel.setId(univ);
                    relatorio.addDetalhe(rel);
                    rankingUniversidade(filtroPart, rel);
                }
            }
        });
    }

    private void rankingRegiao(final FiltroRanking filtro, final RelatorioObjeto relatorio){
        final ArrayList<String> unidadeId = new ArrayList<>();

        firebaseFirestore.collection("unidade")
                .whereEqualTo("regiaoId", filtro.getRegiaoId())
                .whereEqualTo("status", StatusEnum.ATIVO)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Unidade un = document.toObject(Unidade.class);
                        if(!unidadeId.contains(un.getId()))
                            unidadeId.add(un.getId());
                    }
                }

                for(String uni: unidadeId){
                    FiltroRanking filtroPart = new FiltroRanking();
                    filtroPart.setDataInicial(filtro.getDataInicial());
                    filtroPart.setDataFinal(filtro.getDataFinal());
                    filtroPart.setUnidadeId(uni);


                    RelatorioObjeto rel = new RelatorioObjeto();
                    rel.setId(uni);
                    relatorio.addDetalhe(rel);

                    rankingUnidade(filtroPart, rel);
                }
            }
        });
    }


    public void filtrarRanking(FiltroRanking filtro){
        ArrayList<Atendimento> relatorioFiltrado = new ArrayList<>();
        for(Atendimento at: relatorioRanking){
            if(filtrar(filtro, at))
                relatorioFiltrado.add(at);
        }

        carregouRanking = true;
    }

    private boolean filtrar(FiltroRanking filtro, Atendimento at) {
        if (filtro.getTipoDocumento() != null) {
            if (!filtro.getTipoDocumento().equals(at.getAtendidoTipoDocumento()))
                return false;
        }

        if (filtro.getConclusivo() != null){
            if ("Sim".equals(filtro.getConclusivo()) && !at.getConclusivo())
                return false;

            if ("Não".equals(filtro.getConclusivo()) && at.getConclusivo())
                return false;
        }

        if (filtro.getAtendimentoTipo() != null)
            if(!filtro.getAtendimentoTipo().getId().equals(at.getAtendidoTipoId()))
                return false;

        return true;
    }

    //endregion

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
//        if (versao == 0) {
            getUnidadesTipoLocal();
//        } else {
//            getUnidadesTipoBanco();
//        }
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

    public ArrayList<Estado> getEstadosByRegiao(Regiao regiao) {
        ArrayList<Estado> estadosLista = new ArrayList<>();
        for(Estado estado: estados){
            if(regiao.getEstados().contains(estado.getId()))
                estadosLista.add(estado);
        }

        return estadosLista;
    }

    public Unidade getUnidadeById(String unidadeId) {
        for(Unidade und: unidades){
            if(und.getId().equals(unidadeId))
                return und;
        }

        return null;
    }

    public ArrayList<AtendimentoTipo> getAtendimentosTipo() {
        return this.atendimentosTipo;
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

    public void setAtendimentos(ArrayList<AtendimentoTipo> atendimentos) {
        this.atendimentosTipo = atendimentos;
    }

    public ArrayList<AtendidoTipo> getAtendidoTipos() {
        return atendidoTipos;
    }

    public void setAtendidoTipos(ArrayList<AtendidoTipo> atendidoTipos) {
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

    public ArrayList<Atendimento> getAtendimentosLista() {
        return atendimentosLista;
    }

    public void setAtendimentosLista(ArrayList<Atendimento> atendimentosLista) {
        this.atendimentosLista = atendimentosLista;
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
        unidadeTipos = new ArrayList<>();
        lista.add(new UnidadeTipo("2fc6E3rkAkZcFKHpF9Eh", "Superintendência"));
        lista.add(new UnidadeTipo("G6WLURVH2Cd7TG4ACCD4", "Inspetoria"));
        lista.add(new UnidadeTipo("O6GnqBBkJZLJhIlzHycq", "Agência"));
        lista.add(new UnidadeTipo("TFN0ALlaiOwCc2paZkY0", "Delegacia"));
        lista.add(new UnidadeTipo("mLoI8CgDXH8WtVEcsC1j", "Alfandega"));
        lista.add(new UnidadeTipo("plFhYkxaF1eJq5VHtE5v", "Delegacia de Julgamento"));
        lista.add(new UnidadeTipo("usk8AUmOGY2nTu9mcV2c", "Posto"));
        for(UnidadeTipo uni: lista){
            if(!unidadeTipos.contains(uni)){
                unidadeTipos.add(uni);
            }
        }
    }

    private void getAtendidosLocal() {
        ArrayList<AtendidoTipo> lista = new ArrayList<AtendidoTipo>();
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
        ArrayList<AtendimentoTipo> lista = new ArrayList<AtendimentoTipo>();
        lista.add(new AtendimentoTipo("BFUw5tuJHZPmcKwb2u4f", "Auxílio à elaboração e orientações sobre a Declaração de Ajuste Anual do IRPF"));
        lista.add(new AtendimentoTipo("m7pHX9YXcasc4NCnLF83", "Auxílio à inscrição e Informações cadastrais de CPF"));
        lista.add(new AtendimentoTipo("efE4yAzydBFpPYEOhP46", "Auxílio à inscrição e Informações cadastrais do CNPJ"));
        lista.add(new AtendimentoTipo("eX4ddhUEeVkcgS4gIuo8", "Auxílio à emissão e informações sobre Certidões Negativas de Débitos PF e PJ"));
        lista.add(new AtendimentoTipo("wbLxjD9kU0nQGTZEDfp4", "Auxílio à consulta à situação fiscal"));
        lista.add(new AtendimentoTipo("4oTugvnwxE17vcHsMzO8", "Agendamento on-line de s na RFB"));
        lista.add(new AtendimentoTipo("4oTugnWwxE17vcHsMzO8", "Informações e auxílio à regularização de CPF Suspenso"));
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
        this.atendimentosTipo = lista;
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

    public ArrayList<Cidade> getCidadesLocal(String estadoId) {
        if ("YOHaadyseN9LJy6v2wzQ".equals(estadoId)) {
            return AC.getCidades();
        } else if ("M5R8LCulJcMtwd9jH2T2".equals(estadoId)) {
            return AL.getCidades();
        } else if ("U4socdTZxzTDU8WB6fIY".equals(estadoId)) {
            return AM.getCidades();
        } else if ("8rK4E9CTuN3YhwbCr9GL".equals(estadoId)) {
            return AP.getCidades();
        } else if ("6R8soQpFwwNYRMxhvqVJ".equals(estadoId)) {
            return BA.getCidades();
        } else if ("19LMXJKf2gQhrwvFKpCG".equals(estadoId)) {
            return CE.getCidades();
        } else if ("URuENL5wManmASqNRZlE".equals(estadoId)) {
            return DF.getCidades();
        } else if ("yTgVVuT0uf8wT03omvWX".equals(estadoId)) {
            return ES.getCidades();
        } else if ("vp6JrzOjGLJ1BUkxrUjo".equals(estadoId)) {
            return GO.getCidades();
        } else if ("pHJjtnzuXeDQ7bqmJItp".equals(estadoId)) {
            return MA.getCidades();
        } else if ("18adfRUvWf5yNxfV3zG3".equals(estadoId)) {
            return MG.getCidades();
        } else if ("82bo5VcIUDbVhAiiWKE0".equals(estadoId)) {
            return MS.getCidades();
        } else if ("bSVpIP0ywsHAwNpWWJWT".equals(estadoId)) {
            return MT.getCidades();
        } else if ("ItdD34xP0a63qxRmw2P0".equals(estadoId)) {
            return PA.getCidades();
        } else if ("h5RRp13ofgAMn9v37Ln8".equals(estadoId)) {
            return PB.getCidades();
        } else if ("YS1okckJJhDXNtmcKS9b".equals(estadoId)) {
            return PE.getCidades();
        } else if ("n30NTxhAuxqCa6OMcR42".equals(estadoId)) {
            return PI.getCidades();
        } else if ("ExaDaauaC2Jwt6W9rT8Y".equals(estadoId)) {
            return PR.getCidades();
        } else if ("piIRsvy9nHTMzBq3lRU0".equals(estadoId)) {
            return RJ.getCidades();
        } else if ("hVaznHuTvMcaCVJKgZjk".equals(estadoId)) {
            return RN.getCidades();
        } else if ("s1YxDxTODBAl79sFMfIs".equals(estadoId)) {
            return RO.getCidades();
        } else if ("xS8wVdtWmIj134cTBLTC".equals(estadoId)) {
            return RR.getCidades();
        } else if ("XE0DpyxK9DOXBlSqfma6".equals(estadoId)) {
            return RS.getCidades();
        } else if ("4niJNacnYflWAk7B2yA4".equals(estadoId)) {
            return SC.getCidades();
        } else if ("S2KhrsFJMDoPxfZc91tr".equals(estadoId)) {
            return SE.getCidades();
        } else if ("RhxB7uMtgwpjGI8ZCc69".equals(estadoId)) {
            return SP.getCidades();
        } else if ("3Y8KZ4fEQRlMakrppPfa".equals(estadoId)) {
            return TO.getCidades();
        }
        return null;
    }

    private boolean participantesRelatorioCarregado = false;

    public boolean isParticipantesRelatorioCarregado() {
        return participantesRelatorioCarregado;
    }

    public void carregaParticipantes() {
        participantesRelatorioCarregado = false;
        participantesRelatorio = new ArrayList<>();

        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("tipo", AcessoTipoEnum.UNIVERSIDADE)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        if(ac.getNivelAcesso() <= 2L){
                            participantesRelatorio.add(ac);
                            traduzirNomeParticipantes(ac);
                        }
                    }
                }
                participantesRelatorioCarregado = true;
            }
        });

    }

    private void traduzirNomeParticipantes(final Acesso acesso){
        DocumentReference dr =
                firebaseFirestore.collection("usuario").document(acesso.getUsuarioId());
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Usuario usr =  document.toObject(Usuario.class);
                    acesso.setNome(usr.getNome());
                }
            }
        });
    }

    public boolean traduziuNomesParticipantes(){
        for(Acesso acesso: participantesRelatorio){
            if(acesso.getNome() == null || acesso.getNome().length() == 0){
                return false;
            }
        }

        return true;
     }

    public ArrayList<Acesso> getParticipantesRelatorio() {
        return participantesRelatorio;
    }

    private boolean verificouExclusao = false;
    private boolean podeExcluir = false;
    public boolean isVerificouExclusao() {
        return verificouExclusao;
    }
    public boolean isPodeExcluir() {
        return podeExcluir;
    }

    public void validarRemocaoRegiao(String id) {
        verificouExclusao = false;
        podeExcluir = false;

        firebaseFirestore.collection("unidade")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("regiaoId", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() > 0)
                        podeExcluir = false;
                    else
                        podeExcluir = true;
                }

                verificouExclusao = true;
            }
        });
    }

    public void validarRemocaoUnidade(String id) {
        verificouExclusao = false;
        podeExcluir = false;

        firebaseFirestore.collection("universidade")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("unidadeId", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() > 0)
                        podeExcluir = false;
                    else
                        podeExcluir = true;
                }

                verificouExclusao = true;
            }
        });
    }

    public void validarRemocaoUniversidade(String id) {
        verificouExclusao = false;
        podeExcluir = false;

        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("universidadeId", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() > 0)
                        podeExcluir = false;
                    else
                        podeExcluir = true;
                }

                verificouExclusao = true;
            }
        });
    }

    public void validarRemocaoParticipante(String id) {
        verificouExclusao = false;
        podeExcluir = false;

        firebaseFirestore.collection("atendimento")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("acessoId", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() > 0)
                        podeExcluir = false;
                    else
                        podeExcluir = true;
                }

                verificouExclusao = true;
            }
        });
    }

    public void validarRemocaoUsuario(String id) {
        verificouExclusao = false;
        podeExcluir = false;

        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.ATIVO)
                .whereEqualTo("usuarioId", id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() > 0)
                        podeExcluir = false;
                    else
                        podeExcluir = true;
                }

                verificouExclusao = true;
            }
        });
    }

    public void removerSolicitacoesUniversidade(String universidadeId){
        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.INATIVO)
                .whereEqualTo("universidadeId", universidadeId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        firebaseFirestore.collection("acesso").document(ac.getId()).delete();
                    }
                }
            }
        });
    }

    public void removerSolicitacoesUnidade(String unidadeId){
        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.INATIVO)
                .whereEqualTo("unidadeId", unidadeId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        firebaseFirestore.collection("acesso").document(ac.getId()).delete();
                    }
                }
            }
        });
    }

    public void removerSolicitacoesRegiao(String regiaoId){
        firebaseFirestore.collection("acesso")
                .whereEqualTo("status", StatusEnum.INATIVO)
                .whereEqualTo("regiaoId", regiaoId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Acesso ac = document.toObject(Acesso.class);
                        firebaseFirestore.collection("acesso").document(ac.getId()).delete();
                    }
                }
            }
        });
    }

    public boolean existeRegiao(String regiaoId){
        for(Regiao regiao: regioes){
            if(regiao.getId().equals(regiaoId))
                return true;
        }
        return false;
    }

    public boolean existeUnidade(String unidadeId){
        for(Unidade unidade: unidades){
            if(unidade.getId().equals(unidadeId))
                return true;
        }
        return false;
    }

    public boolean existeUniversidade(String universidadeId){
        for(Universidade universidade: universidades){
            if(universidade.getId().equals(universidadeId))
                return true;
        }
        return false;
    }

//endregion
}