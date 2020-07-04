package br.ufms.nafmanager.activities.relatorios;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.RelatorioActivity;
import br.ufms.nafmanager.model.Acesso;
import br.ufms.nafmanager.model.AcessoTipoEnum;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.model.TipoDocumentoEnum;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.persistencies.Persistencia;

public class RelatorioRanking  extends RelatorioActivity {

    private SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

    private ArrayList<RelatorioObjeto> meses = new ArrayList<>();
    private Spinner spinnerTipo;
    private Spinner spinnerRegiao;
    private Spinner spinnerUnidade;
    private Spinner spinnerUniversidade;
    private Spinner spinnerParticipante;
    private Spinner spinnerConclusivo;
    private Spinner spinnerTipoAtendimento;
    private Spinner spinnerTipoDocumento;

    private TextView tvDataInicio;
    private TextView tvDataFim;
    private Date dataInicial;
    private Date dataFinal;
    private String dataInicialStr;
    private String dataFinalStr;

    private TextView tvUnidade;
    private TextView tvUniversidade;
    private TextView tvParticipante;

    private ArrayList<String> tipoLista;
    private ArrayList<Regiao> regiaoLista;
    private ArrayList<Unidade> unidadeLista;
    private ArrayList<Universidade> universidadeLista;
    private ArrayList<Acesso> participanteLista;
    private ArrayList<AtendimentoTipo> atendimentoTipoLista;

    private ArrayAdapter<String> tipoAdapter;
    private ArrayAdapter<Regiao> regiaoAdapter;
    private ArrayAdapter<Unidade> unidadeAdapter;
    private ArrayAdapter<Universidade> universidadeAdapter;
    private ArrayAdapter<Acesso> participanteAdapter;
    private ArrayAdapter<TipoDocumentoEnum> documentoAdapter;
    private ArrayAdapter<AtendimentoTipo> atendimentoTipoAdapter;
    private ArrayAdapter<String> conclusivoAdapter;

    private DatePickerDialog.OnDateSetListener dateInicioLister;
    private DatePickerDialog.OnDateSetListener dateFimLister;

    private FiltroRanking filtro;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_ranking);
        vincularComponentes();
        carregarTamanhoTela();
    }

    public void gerar(){
        int indexLoop = 0;

        relatorio = Persistencia.getInstance().getRelatorio();
        document = new PdfDocument();

        pageNum = 1;
        hIndex = 100;
        int index = 1;
        pageInfo = new PdfDocument.PageInfo.Builder(pageW,pageH,pageNum).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        imprimirCabecalho(pageInfo);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(13.0f);

        for (RelatorioObjeto mes : meses) {
            marcarCabecalho(mes.getValor3());
            verificarQuebra(110);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(13.0f);
//            canvas.drawText(String.format("%03d", index), 4,hIndex, paint); //atendimento numero

            verificarQuebra(110);

            index++;
        }

//        imprimirRodape(pageInfo);
        document.finishPage(page);

        File file = new File(Environment.getExternalStorageDirectory(), "/RelatorioNAF.pdf");
        try{
            document.writeTo(new FileOutputStream(file));
            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(RelatorioRanking.this, PdfVisualizador.class);
        startActivity(intent);
    }

    private void vincularComponentes() {
        spinnerTipo = findViewById(R.id.sp_tipoRelatorio);
        spinnerRegiao = findViewById(R.id.sp_regiao);
        spinnerUnidade = findViewById(R.id.sp_unidade);
        spinnerUniversidade = findViewById(R.id.sp_universidade);
        spinnerParticipante = findViewById(R.id.sp_participante);
        spinnerConclusivo = findViewById(R.id.sp_atendimentoConclusivo);
        spinnerTipoAtendimento = findViewById(R.id.sp_tipoAtendimentoId);
        spinnerTipoDocumento = findViewById(R.id.sp_tipoPessoa);

        tvUnidade = findViewById(R.id.tv_unidade);
        tvUniversidade = findViewById(R.id.tv_universidade);
        tvParticipante = findViewById(R.id.tv_participante);

        tvDataInicio = findViewById(R.id.et_rankingInicio);
        tvDataFim = findViewById(R.id.et_rankingFim);

        tvDataInicio.setText(sdf.format(new Date()));
        tvDataFim.setText(sdf.format(new Date()));

        tipoLista = new ArrayList<>();
        tipoLista.add("Participante");
        tipoLista.add("Universidade");
        tipoLista.add("Unidade");
        tipoLista.add("Região");

        tipoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipoLista);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipo = (String) parent.getItemAtPosition(position);

                spinnerUnidade.setVisibility(View.INVISIBLE);
                spinnerUniversidade.setVisibility(View.INVISIBLE);
                spinnerParticipante.setVisibility(View.INVISIBLE);

                tvUnidade.setVisibility(View.INVISIBLE);
                tvUniversidade.setVisibility(View.INVISIBLE);
                tvParticipante.setVisibility(View.INVISIBLE);
                controlaTipo(tipo);
           }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        regiaoLista = Persistencia.getInstance().getRegioes();
        regiaoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, regiaoLista);
        regiaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(regiaoAdapter);
        spinnerRegiao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Regiao regiao = (Regiao) parent.getItemAtPosition(position);
                filtrarUnidades(regiao);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerUnidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Unidade unidade = (Unidade) parent.getItemAtPosition(position);
                filtrarUniversidades(unidade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerUniversidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Universidade universidade = (Universidade) parent.getItemAtPosition(position);
                filtrarParticipante(universidade);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        atendimentoTipoLista = Persistencia.getInstance().getAtendimentosTipo();
        atendimentoTipoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, atendimentoTipoLista);
        atendimentoTipoAdapter .setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinnerTipoAtendimento.setAdapter(atendimentoTipoAdapter);

        documentoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TipoDocumentoEnum.values());
        documentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoDocumento.setAdapter(documentoAdapter);

        ArrayList<String> conclusivoLista = new ArrayList<>();
        conclusivoLista.add("Todos");
        conclusivoLista.add("Sim");
        conclusivoLista.add("Não");

        conclusivoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, conclusivoLista);
        conclusivoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConclusivo.setAdapter(conclusivoAdapter);

        tvDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date data = new Date();
                try{
                    data = sdf.parse(tvDataInicio.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar cal = new GregorianCalendar();
                cal.setTime(data);

                int dia = cal.get(Calendar.DAY_OF_MONTH);
                int mes = cal.get(Calendar.MONTH);
                int ano = cal.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        RelatorioRanking.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateInicioLister,
                        ano, mes , dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        tvDataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date data = new Date();
                try{
                    data = sdf.parse(tvDataFim.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar cal = new GregorianCalendar();
                cal.setTime(data);

                int dia = cal.get(Calendar.DAY_OF_MONTH);
                int mes = cal.get(Calendar.MONTH);
                int ano = cal.get(Calendar.YEAR);

                DatePickerDialog dialog = new DatePickerDialog(
                        RelatorioRanking.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateFimLister,
                        ano, mes, dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        dateInicioLister = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                mes = mes + 1;
                tvDataInicio.setText((dia < 10 ? "0" + dia : dia) + "/" + (mes < 10 ? "0" + mes : mes) + "/" + ano);
            }
        };

        dateFimLister = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                mes = mes + 1;
                tvDataFim.setText((dia < 10 ? "0" + dia : dia) + "/" + (mes < 10 ? "0" + mes : mes) + "/" + ano);
            }
        };

        btnGerar = findViewById(R.id.btn_gerarRelatorioRanking);
        btnGerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caputarDados();
            }
        });
    }

    private void caputarDados() {
        dataInicial = new Date();
        dataFinal = new Date();

        try {
            dataInicialStr = tvDataInicio.getText().toString();
            dataFinalStr = tvDataFim.getText().toString();
            dataInicial = sdf.parse(dataInicialStr);
            dataFinal = sdf.parse(dataFinalStr);

            if(dataFinal.after(new Date())){
                Toast.makeText(this, "Data fim maior que hoje!", Toast.LENGTH_SHORT).show();
                return;
            }

            long diferencaDias = (dataFinal.getTime() - dataInicial.getTime()) / (1000*60*60*24);
            long diferencaMes = (dataFinal.getTime() - dataInicial.getTime()) / (1000*60*60*24) / 30;
            int mesBase = dataInicial.getMonth() + 1;

            if(diferencaDias > 365){
                Toast.makeText(this,"O período deve ser menor que um ano", Toast.LENGTH_SHORT).show();
                return;
            }

           meses = new ArrayList<>();
            for(int i = mesBase; i<= mesBase + diferencaMes; i++){
                RelatorioObjeto rel = new RelatorioObjeto();
                rel.setValor3(retornaMes(Long.valueOf(i)));
                meses.add(rel);
            }

            String tipo = (String) spinnerTipo.getSelectedItem();
            TipoDocumentoEnum tipoDoc = (TipoDocumentoEnum) spinnerTipoDocumento.getSelectedItem();
            AtendimentoTipo at = (AtendimentoTipo) spinnerTipoAtendimento.getSelectedItem();
            String conclusivo = (String) spinnerConclusivo.getSelectedItem();
            Acesso participante = (Acesso) spinnerParticipante.getSelectedItem();
            Unidade unidade = (Unidade) spinnerUnidade.getSelectedItem();
            Universidade universidade = (Universidade) spinnerUniversidade.getSelectedItem();

            filtro = new FiltroRanking();

            filtro.setAtendimentoTipo(at);
            filtro.setDataInicial(dataInicial);
            filtro.setDataFinal(dataFinal);

            if(conclusivo.equals("Sim")){
                filtro.setConclusivo("Sim");
            }
            else if (conclusivo.equals("Não")){
                filtro.setConclusivo("Não");
            }

            filtro.setTipo(tipo);
            filtro.setTipoDocumento(tipoDoc);

            if(participante!= null && participante.getId() != null)
                filtro.setParticipanteId(participante.getId());

            if(universidade!= null && universidade.getId() != null)
                filtro.setUniversidadeId(universidade.getId());

            if(unidade!= null && unidade.getId() != null)
                filtro.setUnidadeId(unidade.getId());


            Persistencia.getInstance().rankingAtendimentos(filtro);

//            gerar();
            showDialog();
            aguardando();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public void gerarDados(){
        Persistencia.getInstance().rankingMarcacaoReflexiva();
        if(Persistencia.getInstance().verificarRankingFoiFinalizado()){
            Persistencia.getInstance().filtrarRanking(filtro);
            hideDialog();
            gerar();
        }
        else{
            aguardando();
        }
    }

    private void aguardando() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                gerarDados();
            }
        }, 200);
    }

    private void controlaTipo(String tipo) {
        if("Região".equals(tipo)){

        }
        else if("Unidade".equals(tipo)){
            spinnerUnidade.setVisibility(View.VISIBLE);
            tvUnidade.setVisibility(View.VISIBLE);
        }
        else if("Universidade".equals(tipo)){
            spinnerUnidade.setVisibility(View.VISIBLE);
            spinnerUniversidade.setVisibility(View.VISIBLE);

            tvUnidade.setVisibility(View.VISIBLE);
            tvUniversidade.setVisibility(View.VISIBLE);
        }
        else if("Participante".equals(tipo)){
            spinnerUnidade.setVisibility(View.VISIBLE);
            spinnerUniversidade.setVisibility(View.VISIBLE);
            spinnerParticipante.setVisibility(View.VISIBLE);

            tvUnidade.setVisibility(View.VISIBLE);
            tvUniversidade.setVisibility(View.VISIBLE);
            tvParticipante.setVisibility(View.VISIBLE);
        }
    }

    private void filtrarUnidades(Regiao regiao){
        spinnerUnidade.setEnabled(true);
        unidadeLista = new ArrayList<>();

        String tipo = (String) spinnerTipo.getSelectedItem();
        if("Unidade".equals(tipo) || "Universidade".equals(tipo) || "Participante".equals(tipo)) {
            for (Unidade unidade : Persistencia.getInstance().getUnidades()) {
                if (unidade.getRegiaoId().equals(regiao.getId()) && !unidadeLista.contains(unidade)) {
                    unidadeLista.add(unidade);
                }
            }
        }
        verificarListaVaziaUnidade();
    }

    private void verificarListaVaziaUnidade() {
        if(unidadeLista.size()==0){
            unidadeLista.add(new Unidade(null, "Nenhum registro encontrado"));
            spinnerUnidade.setEnabled(false);
        }

        geraListagemUnidades();
    }

    private void geraListagemUnidades(){
        unidadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, unidadeLista);
        unidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidade.setAdapter(unidadeAdapter);
    }

    private void filtrarUniversidades(Unidade unidade){
        spinnerUniversidade.setEnabled(true);
        universidadeLista = new ArrayList<>();

        String tipo = (String) spinnerTipo.getSelectedItem();
        if("Universidade".equals(tipo) || "Participante".equals(tipo)) {
            for (Universidade universidade : Persistencia.getInstance().getUniversidades()) {
                if (universidade.getUnidadeId().equals(unidade.getId()) && !universidadeLista.contains(universidade)) {
                    universidadeLista.add(universidade);
                }
            }
        }
        verificarListaVaziaUniversidade();
    }

    private void verificarListaVaziaUniversidade() {
        if(universidadeLista.size()==0){
            universidadeLista.add(new Universidade(null, "Nenhum registro encontrado"));
            spinnerUniversidade.setEnabled(false);
        }

        geraListagemUniversidades();
    }

    private void geraListagemUniversidades(){
        universidadeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, universidadeLista);
        universidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUniversidade.setAdapter(universidadeAdapter);
    }

    private void filtrarParticipante(Universidade universidade){
        spinnerParticipante.setEnabled(true);
        participanteLista= new ArrayList<>();

        String tipo = (String) spinnerTipo.getSelectedItem();
        if("Participante".equals(tipo)){
            for(Acesso acesso: Persistencia.getInstance().getParticipantesRelatorio()){
                if(acesso.getUniversidadeId().equals(universidade.getId()) && acesso.getTipoValor().equals(AcessoTipoEnum.UNIVERSIDADE.getValor())){
                    participanteLista.add(acesso);
                }
            }
        }

        verificarListaVaziaParticipante();
    }

    private void verificarListaVaziaParticipante(){
        if(participanteLista.size()==0){
            Acesso ac = new Acesso();
            ac.setNome("Nenhum registro encontrado");
            participanteLista.add(ac);
            spinnerParticipante.setEnabled(false);
        }

        gerarListagemParticipantes();
    }

    private void gerarListagemParticipantes(){
        participanteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, participanteLista);
        participanteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParticipante.setAdapter(participanteAdapter);
    }

    private String retornaMes(Long mes){
        if(mes.equals(1L)){
            return "Janeiro";
        }
        else if(mes.equals(2L)){
            return "Fevereiro";
        }
        else if(mes.equals(3L)){
            return "Março";
        }
        else if(mes.equals(4L)){
            return "Abril";
        }
        else if(mes.equals(5L)){
            return "Maio";
        }
        else if(mes.equals(6L)){
            return "Junho";
        }
        else if(mes.equals(7L)){
            return "Julho";
        }
        else if(mes.equals(8L)){
            return "Agosto";
        }
        else if(mes.equals(9L)){
            return "Setembro";
        }
        else if(mes.equals(10L)){
            return "Outubro";
        }
        else if(mes.equals(11L)){
            return "Novembro";
        }
        else if(mes.equals(12L)){
            return "Dezembro";
        }
        else {
            return retornaMes(mes %12);
        }
    }
}



