package br.ufms.nafmanager.activities.relatorios;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.persistencies.Persistencia;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class RelatorioRepresentanteUnidade extends CustomActivity {

    private Button btnGerar;

    private String unidadeId;
    private String unidadeNome;

    private ArrayList<Unidade> unidadeLista;
    private ArrayAdapter<Unidade> unidadeAdapter;
    private Spinner spinnerUnidade;

    private TextView tvDataInicio;
    private TextView tvDataFim;
    private Date dataInicial;
    private Date dataFinal;
    private String dataInicialStr;
    private String dataFinalStr;

    private DatePickerDialog.OnDateSetListener dateInicioLister;
    private DatePickerDialog.OnDateSetListener dateFimLister;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private PdfDocument document = new PdfDocument();
    private Paint paint = new Paint();
    private Canvas canvas;
    private PdfDocument.Page page;
    private PdfDocument.PageInfo pageInfo;

    private int pageW = 400;
    private int pageH = 600;
    private int pageNum = 1;
    private int hIndex = 100;

    private RelatorioObjeto relatorio = new RelatorioObjeto();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_representante_unidade);

        vincularComponentes();
    }

    private void imprimirCabecalho(PdfDocument.PageInfo pageInfo) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25.0f);
        canvas.drawText("NAF Manager", pageInfo.getPageWidth()/2,28, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20.0f);
        canvas.drawText("Relatório de Representante da Unidade", pageInfo.getPageWidth()/2,50, paint);

        String unidade = "Todas";
        if(unidadeNome != null){
            unidade = unidadeNome;
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);
        canvas.drawText("Unidade: " + unidade , pageInfo.getPageWidth()/2,68, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);
        canvas.drawText("Período: de " + dataInicialStr + " até " + dataFinalStr, pageInfo.getPageWidth()/2,82, paint);
    }

    public void marcarCabecalho(String header){
        Paint.FontMetrics fm = new Paint.FontMetrics();
        paint.setColor(Color.GRAY);
        paint.getFontMetrics(fm);
        canvas.drawRect(5, hIndex - 12,395, hIndex+ 4, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText(header, 5, hIndex, paint); //header

        paint.setColor(Color.BLACK);
    }

    private void imprimirRodape(PdfDocument.PageInfo pageInfo) {
        verificarQuebra();

        if(hIndex + 120 > pageH){
            quebrarPagina();
        }

        int universidades = 0;
        int representanteUnidade =0;
        int participantes = 0;
        int atendimentos = 0;
        int alunos = 0;
        int professores = 0;
        int representantes = 0;

        representanteUnidade += relatorio.getDetalhe2().size();

        for(RelatorioObjeto universidade: relatorio.getDetalhe()){
            universidades++;
            for(RelatorioObjeto participante: universidade.getDetalhe()){
                if(participante.getValor3() != null && Integer.parseInt(participante.getValor3()) == 1){
                    atendimentos+= participante.getDetalhe().size();
                    participantes++;
                    alunos++;
                }

                if(participante.getValor3() != null && Integer.parseInt(participante.getValor3()) == 2){
                    atendimentos+= participante.getDetalhe().size();
                    participantes++;
                    professores++;
                }

                if(participante.getValor3() != null && Integer.parseInt(participante.getValor3()) == 4){
                    representantes++;
                }
            }
        }

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        paint.setColor(Color.BLACK);
        marcarCabecalho(" Totais");

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Representantes da Unidade: " + representanteUnidade, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Universidades: " + universidades, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Representantes da Universidade: " + representantes, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Participantes: " + participantes, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Professores: " + professores, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Alunos: " + alunos, 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Atendimentos: " + atendimentos, 4,hIndex, paint);
    }

    public void gerar(){
        int indexLoop = 0;
        relatorio = Persistencia.getInstance().getRelatorio();
        document = new PdfDocument();

        pageNum = 1;
        hIndex = 100;

        pageInfo = new PdfDocument.PageInfo.Builder(pageW,pageH,pageNum).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        imprimirCabecalho(pageInfo);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(13.0f);
        paint.setColor(Color.BLACK);

        marcarCabecalho("Nº  | Unidade                                                            |       Quantidade");
        verificarQuebra();

        canvas.drawText(String.format("%03d", 1), (indexLoop * 15) + 5, hIndex, paint); //unidade index
        canvas.drawText(ajustarNome(unidadeNome, 45), (indexLoop * 15) + 35, hIndex, paint); //unidade nome
        verificarQuebra();

        int universidadeIndex = 1;
        for (RelatorioObjeto universidade : relatorio.getDetalhe()) {//universidades
            indexLoop++;

            marcarCabecalho("     Nº  | Universidade                                              |       Quantidade");
            verificarQuebra();

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(13.0f);
            canvas.drawText(String.format("%03d", universidadeIndex), (indexLoop * 15) + 5, hIndex, paint); //universidade index
            canvas.drawText(ajustarNome(universidade.getNome(), 45), (indexLoop * 15) + 35, hIndex, paint); //universidade nome

            int qtdParticipantes = 0;
            if (universidade.getDetalhe() != null) {
                qtdParticipantes = universidade.getDetalhe().size();
            }

            canvas.drawText("  Participantes: " + String.format("%04d", qtdParticipantes), 272, hIndex, paint); //participantes index

            ArrayList<RelatorioObjeto> alunos = new ArrayList<>();
            ArrayList<RelatorioObjeto> profs = new ArrayList<>();
            ArrayList<RelatorioObjeto> rep = new ArrayList<>();

            for(RelatorioObjeto participante: universidade.getDetalhe()) {//participantes
                if(!alunos.contains(participante) && participante.getValor3() != null && Integer.parseInt(participante.getValor3()) == 1){
                    alunos.add(participante);
                }
                else if (!profs.contains(participante) && participante.getValor3() != null && Integer.parseInt(participante.getValor3()) == 2){
                    profs.add(participante);
                }
                else if (!rep.contains(participante) && participante.getValor3() != null && Integer.parseInt(participante.getValor3()) == 4){
                    rep.add(participante);
                }
            }

            verificarQuebra();
            for(RelatorioObjeto representante: rep){
                canvas.drawText("Representante: "+ representante.getNome(), (indexLoop * 15) + 5 , hIndex, paint); //representante da universidade
                verificarQuebra();
            }

            int alunoIndex = 1;
            if(alunos.size() > 0){

                indexLoop++;

                canvas.drawText("Alunos", (indexLoop * 15) , hIndex, paint); //header
                verificarQuebra();

                for(RelatorioObjeto aluno: alunos){

                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setTextSize(13.0f);
                    canvas.drawText(String.format("%03d", alunoIndex), (indexLoop * 15),hIndex , paint); // aluno index
                    canvas.drawText(ajustarNome(aluno.getNome(),35), (indexLoop * 15) + 30,hIndex, paint); //aluno nome

                    int atendimentos = 0;
                    if(aluno.getDetalhe() != null){
                        atendimentos= aluno.getDetalhe().size();
                    }

                    canvas.drawText("Atendimentos: "+ String.format("%04d", atendimentos), 274,hIndex , paint); //aluno atendimentos

                    verificarQuebra();

                    alunoIndex++;
                }

                indexLoop--;
            }

            int profIndex = 1;
            if(profs.size() > 0){

                indexLoop ++;

                canvas.drawText("Professores", (indexLoop * 15), hIndex, paint); //header
                verificarQuebra();

                for(RelatorioObjeto prof: profs){

                    paint.setTextAlign(Paint.Align.LEFT);
                    paint.setTextSize(13.0f);
                    canvas.drawText(String.format("%03d", profIndex), (indexLoop * 15),hIndex , paint); // prof index
                    canvas.drawText(ajustarNome(prof.getNome(),35), (indexLoop * 15) + 30,hIndex, paint); //prof nome

                    int atendimentos = 0;
                    if(prof.getDetalhe() != null){
                        atendimentos= prof.getDetalhe().size();
                    }

                    canvas.drawText("Atendimentos: "+ String.format("%04d", atendimentos), 274,hIndex , paint); //prof atendimentos

                    verificarQuebra();

                    profIndex++;
                }
                indexLoop --;
            }
            universidadeIndex++;

            indexLoop--;
        }

        imprimirRodape(pageInfo);
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

        Intent intent = new Intent(RelatorioRepresentanteUnidade.this, PdfVisualizador.class);
        startActivity(intent);
    }

    private void verificarQuebra() {
        hIndex += 15;

        if(hIndex >= pageH){
            quebrarPagina();
        }
    }

    private void quebrarPagina(){
        hIndex = 120;
        pageNum++;

        document.finishPage(page);
        pageInfo = new PdfDocument.PageInfo.Builder(pageW,pageH,pageNum).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        imprimirCabecalho(pageInfo);
    }

    private void vincularComponentes() {

        this.spinnerUnidade = findViewById(R.id.sp_unidade);
        this.unidadeLista = Persistencia.getInstance().getUnidades();

        this.unidadeAdapter = new ArrayAdapter<Unidade>(this, android.R.layout.simple_spinner_dropdown_item, this.unidadeLista);
        this.unidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.spinnerUnidade.setAdapter(unidadeAdapter);

        this.tvDataInicio = (TextView) findViewById(R.id.et_participanteInicio);
        this.tvDataInicio.setText(sdf.format(new Date()));

        this.tvDataFim = findViewById(R.id.et_participanteFim);
        this.tvDataFim.setText(sdf.format(new Date()));

        this.tvDataInicio.setOnClickListener(new View.OnClickListener() {
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
                        RelatorioRepresentanteUnidade.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateInicioLister,
                        ano, mes , dia);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        this.tvDataFim.setOnClickListener(new View.OnClickListener() {
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
                        RelatorioRepresentanteUnidade.this,
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

        btnGerar = findViewById(R.id.btn_gerarRelatorio);
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
        if(Persistencia.getInstance().estatisticaAcessoCompleta()){
            for(RelatorioObjeto universidade: Persistencia.getInstance().getUniversidadeObjeto()){
                Persistencia.getInstance().estatisticaUsuarios(universidade);
            }
            capturarUsuarios();
        }
        else{
            aguardandoDados();
        }
    }

    private void aguardandoDados() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                capturandoDados();
            }
        }, 100);
    }

    public void capturarUsuarios(){
        if(Persistencia.getInstance().estatisticasUsuarioComAcesso()){
            Persistencia.getInstance().ajustarDadosUniversidade();
            hideDialog();
            gerar();
        }
        else{
            aguardandoUsuarios();
        }
    }

    private void aguardandoUsuarios() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                capturarUsuarios();
            }
        }, 100);
    }

    private void consultarAtendimentos(){
        dataInicial = new Date();
        dataFinal = new Date();

        try {
            dataInicialStr = tvDataInicio.getText().toString();
            dataFinalStr = tvDataFim.getText().toString();
            dataInicial = sdf.parse(dataInicialStr);
            dataFinal = sdf.parse(dataFinalStr);

            unidadeId = null;
            unidadeNome = null;

            Unidade unidade = (Unidade) spinnerUnidade.getSelectedItem();
            if(unidade.getId() != null){
                unidadeId = unidade.getId();
                unidadeNome = unidade.getNome();
            }

            Persistencia.getInstance().instanciarEstatisticas();
            Persistencia.getInstance().estatisticaUnidade(unidadeId, dataInicial, dataFinal);

            showDialog();
            aguardandoDados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String ajustarNome(String nome, int valor) {
        if(nome.length() > valor){
            return nome.substring(0, valor);
        }
        return nome;
    }
}



