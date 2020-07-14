package br.ufms.nafmanager.activities.relatorios;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.RelatorioActivity;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.persistencies.Persistencia;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class RelatorioCadastro extends RelatorioActivity {

    private Spinner spinnerRegiao;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;

    private ArrayList<Regiao> regiaoLista;
    private ArrayList<Estado> estadoLista;
    private ArrayList<Cidade> cidadeLista;

    private String regiaoId;
    private String regiaoNome;
    private String estadoId;
    private String estadoNome;
    private String cidadeId;
    private String cidadeNome;

    private ArrayAdapter<Regiao> regiaoAdapter;
    private ArrayAdapter<Cidade> cidadeAdapter;
    private ArrayAdapter<Estado> estadoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_estatisticas_cadastrais);
        vincularComponentes();
        carregarTamanhoTela();
    }

    @Override
    protected void imprimirCabecalho(PdfDocument.PageInfo pageInfo) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25.0f);
        canvas.drawText("NAFApp", pageInfo.getPageWidth()/2,28, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20.0f);
        canvas.drawText("Relatório de Estatísticas Cadastrais", pageInfo.getPageWidth()/2,50, paint);

        String regiao = "Todas";
        if(regiaoNome != null){
            regiao = regiaoNome;
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);
        canvas.drawText("Região Fiscal: " + regiao , pageInfo.getPageWidth()/2,68, paint);

        String estado = "Todos";
        if(estadoNome != null){
            estado = estadoNome;
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);
        canvas.drawText("Estado: " + estado, pageInfo.getPageWidth()/2,84, paint);


        String cidade = "Todas";
        if(cidadeNome != null){
            cidade = cidadeNome;
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);
        canvas.drawText("Cidade: " + cidade , pageInfo.getPageWidth()/2,102, paint);
    }

    private void imprimirRodape(PdfDocument.PageInfo pageInfo) {
        quebraPagina(120);

        if(hIndex+ 72 >= pageH){
            quebraPagina(120);
        }

        int unidades = 0;
        int universidades = 0;
        int participantes = 0;

            for(RelatorioObjeto unidade: relatorio.getDetalhe()){
                unidades++;
                for(RelatorioObjeto universidade: unidade.getDetalhe()){
                    universidades++;
                    if(universidade.getDetalhe()!= null)
                        participantes+= universidade.getDetalhe().size();
                }
            }

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        marcarCabecalho(" Totais");

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Unidades: " + unidades, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Universidades: " + universidades, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Participantes: " + participantes, 4,hIndex, paint);
    }

    public void gerar(){
        int indexLoop = 0;
        relatorio = Persistencia.getInstance().getRelatorio();
        document = new PdfDocument();

        pageNum = 1;
        hIndex = 120;

        pageInfo = new PdfDocument.PageInfo.Builder(pageW,pageH,pageNum).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        imprimirCabecalho(pageInfo);

        int unidadeIndex = 1;

        for(RelatorioObjeto unidade: relatorio.getDetalhe()) {//regioes

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(13.0f);

            marcarCabecalho(" Nº  | Unidade");

            paint.setColor(Color.WHITE);
            canvas.drawText("|         Quantidade", pageW - 132, hIndex, paint); //header
            paint.setColor(Color.BLACK);

            verificarQuebra(120);

            canvas.drawText(String.format("%03d", unidadeIndex), (indexLoop * 15) + 5, hIndex, paint); //unidade index
            canvas.drawText(ajustarNome(unidade.getNome(), 45), (indexLoop * 15) + 35, hIndex, paint); //unidade nome

            int qtdUniversidade = 0;
            if (unidade.getDetalhe() != null) {
                qtdUniversidade = unidade.getDetalhe().size();
            }

            canvas.drawText("Universidades: " + String.format("%04d", qtdUniversidade), 272, hIndex, paint); //qtd universidades


            int universidadeIndex = 1;

            if(unidade.getDetalhe().size() > 0){
                verificarQuebra(120);
                marcarCabecalho("     Nº   | Universidade");
                paint.setColor(Color.WHITE);
                canvas.drawText("|         Quantidade", pageW - 132, hIndex, paint); //header
                paint.setColor(Color.BLACK);
            }

            verificarQuebra(120);

            for (RelatorioObjeto universidade : unidade.getDetalhe()) {//unidades
                indexLoop++;

                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTextSize(13.0f);
                canvas.drawText(String.format("%03d", universidadeIndex), (indexLoop * 15) + 5, hIndex, paint); //universidade index
                canvas.drawText(ajustarNome(universidade.getNome(), 45), (indexLoop * 15) + 35, hIndex, paint); //universidade nome

                int qtdParticipantes = 0;
                if (universidade.getDetalhe() != null) {
                    qtdParticipantes = universidade.getDetalhe().size();
                }

                canvas.drawText("  Participantes: " + String.format("%04d", qtdParticipantes), 272, hIndex, paint); //participantes index
                verificarQuebra(120);

                universidadeIndex++;

                indexLoop--;
            }

            unidadeIndex++;
        }

        imprimirRodape(pageInfo);
        document.finishPage(page);
        hideDialog();

        File file = new File(Environment.getExternalStorageDirectory(), "/RelatorioNAF.pdf");
        try{
            document.writeTo(new FileOutputStream(file));
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(RelatorioCadastro.this, PdfVisualizador.class);
        startActivity(intent);
    }


    private void vincularComponentes() {

        this.spinnerRegiao = findViewById(R.id.sp_regiao);
        this.spinnerEstado = findViewById(R.id.sp_estado);
        this.spinnerCidade = findViewById(R.id.sp_cidade);

        this.regiaoLista = new ArrayList<>();
        this.regiaoLista.add(new Regiao(null,"Todas"));
        for(Regiao regiao: Persistencia.getInstance().getRegioes()){
            this.regiaoLista.add(regiao);
        }

        this.regiaoAdapter = new ArrayAdapter<Regiao>(this, android.R.layout.simple_spinner_dropdown_item, this.regiaoLista);
        this.regiaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerRegiao.setAdapter(regiaoAdapter);

        this.spinnerRegiao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Regiao regiao = (Regiao) parent.getSelectedItem();
                estadoLista = new ArrayList<>();
                estadoLista.add(new Estado(null,"Todos"));
                if (regiao.getEstados()!= null){
                    for(String estadoId: regiao.getEstados()){
                        Estado estado = new Estado(estadoId, "");
                        estadoLista.add(estado.buscaObjetoNaLista(Persistencia.getInstance().getEstados()));
                    }
                }
                setAdapterEstado();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        this.spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setAdapterCidades();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnGerar = findViewById(R.id.btn_gerarRelatorioEstatisticasCadastrais);
        btnGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23){
                    request();
                }
                else{
                    consultarAtendimentos();
                }
            }
        });
    }

    private void setAdapterEstado() {
        estadoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estadoLista);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerEstado.setAdapter(estadoAdapter);
    }

    private void setAdapterCidades() {
        cidadeLista = new ArrayList<>();
        cidadeLista.add(new Cidade(null, "Todas"));

        Estado estado = (Estado) spinnerEstado.getSelectedItem();
        if(estado.getId() != null){
            for(Cidade cidade: Persistencia.getInstance().getCidades(estado)){
                cidadeLista.add(cidade);
            }
        }

        cidadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeLista);
        cidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCidade.setAdapter(cidadeAdapter);
    }

    @AfterPermissionGranted(123)
    private void request() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        consultarAtendimentos();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {}

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {}
                })
                .check();
    }

    public void capturandoDados(){
       if(Persistencia.getInstance().isMarcarFinalizada()){
           gerar();
        }
        else{
            Persistencia.getInstance().cadastralMarcarFinalizadaReflexiva();
            aguardandoDados();
        }
    }

    private void aguardandoDados() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                capturandoDados();
            }
        }, 300);
    }

    private void consultarAtendimentos(){
        try {
            regiaoId = null;
            regiaoNome = null;
            estadoId = null;
            estadoNome = null;
            cidadeId = null;
            cidadeNome = null;

            Regiao regiao = (Regiao) spinnerRegiao.getSelectedItem();
            if(regiao.getId() != null){
                regiaoId = regiao.getId();
                regiaoNome = regiao.getNome();
            }

            Estado estado = (Estado) spinnerEstado.getSelectedItem();
            if(estado.getId() != null){
                estadoId = estado.getId();
                estadoNome = estado.getNome();
            }

            Cidade cidade = (Cidade) spinnerCidade.getSelectedItem();
            if(cidade.getId() != null){
                cidadeId = cidade.getId();
                cidadeNome = cidade.getNome();
            }

            Persistencia.getInstance().instanciarEstatisticas();

            showDialog();

            Persistencia.getInstance().carregaRegioes();
            Persistencia.getInstance().carregaUnidades();
            Persistencia.getInstance().carregaUniversidades();

            aguardandoCarregamento();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filtrar(){
        if(cidadeId != null){
            Persistencia.getInstance().relatorioCadastralByCidade(cidadeId);
        }
        else if (estadoId != null){
            Persistencia.getInstance().relatorioCadastralByEstado(estadoId);
        }
        else {
            Persistencia.getInstance().relatorioCadastralByRegiao(regiaoId);
        }
        aguardandoDados();
    }

    public void capturandoCarregamento(){
        if(Persistencia.getInstance().carregouRegioes && Persistencia.getInstance().carregouUnidades && Persistencia.getInstance().carregouUniversidades){
            filtrar();
        }
        else{
            aguardandoCarregamento();
        }
    }

    private void aguardandoCarregamento() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                capturandoCarregamento();
            }
        }, 300);
    }
}



