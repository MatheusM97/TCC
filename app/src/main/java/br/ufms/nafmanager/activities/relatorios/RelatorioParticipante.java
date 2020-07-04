package br.ufms.nafmanager.activities.relatorios;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.RelatorioActivity;
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.persistencies.Persistencia;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class RelatorioParticipante extends RelatorioActivity {
    private TextView tvDataInicio;
    private TextView tvDataFim;
    private ArrayList<Atendimento> atendimentos;
    private Date dataInicial;
    private Date dataFinal;
    private String dataInicialStr;
    private String dataFinalStr;

    private DatePickerDialog.OnDateSetListener dateInicioLister;
    private DatePickerDialog.OnDateSetListener dateFimLister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_participante);

        vincularComponentes();
        carregarTamanhoTela();
    }

    private void vincularComponentes() {

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
                        RelatorioParticipante.this,
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
                        RelatorioParticipante.this,
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

        btnGerar = findViewById(R.id.btn_gerarRelatorioAtendimento);
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

    private void consultarAtendimentos(){
        dataInicial = new Date();
        dataFinal = new Date();

        try {
            dataInicialStr = tvDataInicio.getText().toString();
            dataFinalStr = tvDataFim.getText().toString();
            dataInicial = sdf.parse(dataInicialStr);
            dataFinal = sdf.parse(dataFinalStr);

            Persistencia.getInstance().carregaAtendimentos(Persistencia.getInstance().getAcessoAtual().getId(), dataInicial, dataFinal);

            showDialog();
            gerarDados();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void gerarDados(){
        if(Persistencia.getInstance().carregouAtendimentos){
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
        }, 500);
    }

    public void gerar(){
        int indexLoop = 0;
        atendimentos = Persistencia.getInstance().getAtendimentosLista();
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

        for (Atendimento item : atendimentos) {
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(13.0f);
            canvas.drawText(String.format("%03d", index), 4,hIndex, paint); //atendimento numero
            canvas.drawText(sdf.format(item.getDataAtendimento()), 34, hIndex, paint);
            canvas.drawText(item.getConclusivoString(), 116 ,hIndex, paint);
            canvas.drawText(item.getAtendidoNome()!= null? ajustarNome(item.getAtendidoNome(),14): "Não informado", 151 ,hIndex, paint);
            canvas.drawText(item.getAtendidoDocumento()!= null? item.getAtendidoDocumento(): "Não informado", 268 ,hIndex, paint);

            verificarQuebra(110);

            index++;
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

        Intent intent = new Intent(RelatorioParticipante.this, PdfVisualizador.class);
        startActivity(intent);
    }

    private void imprimirRodape(PdfDocument.PageInfo pageInfo) {
        verificarQuebra(110);

        if(hIndex >= pageH - 100){
           quebraPagina(110);
        }

        int total = atendimentos.size();
        int totalConclusivo = 0;
        int totalNaoConclusivo = 0;

        for (Atendimento at: atendimentos) {
            if(at.getConclusivo() != null && at.getConclusivo() == true){
                totalConclusivo++;
            }
            else{
                totalNaoConclusivo++;
            }
        }

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        marcarCabecalho("Totais");
        verificarQuebra(110);
        canvas.drawText("Total: " + total , 4,hIndex+1, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Conclusivos: " + totalConclusivo , 4,hIndex, paint);

        hIndex+= 18;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        canvas.drawText("Não conclusivos: " + totalNaoConclusivo , 4,hIndex, paint);
    }

    @Override
    public void imprimirCabecalho(PdfDocument.PageInfo pageInfo) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25.0f);
        canvas.drawText("NAF Manager", pageInfo.getPageWidth()/2,28, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20.0f);
        canvas.drawText("Relatório do participante", pageInfo.getPageWidth()/2,50, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);
        canvas.drawText("Período: de " + dataInicialStr + " até " + dataFinalStr, pageInfo.getPageWidth()/2,68, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);

        String nome = "";
        if(Persistencia.getInstance().getUsuarioAtual() != null){
          nome = Persistencia.getInstance().getUsuarioAtual().getNome();
        }

        canvas.drawText("Participante: " + nome , pageInfo.getPageWidth()/2,84, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);

        marcarCabecalho("");
        paint.setColor(Color.WHITE);
        canvas.drawText( "Nº"+ "  |" + "      Data"+ "     |" + " Con." +  " |"  +  "      Atendido  " + "     |  " + "Documento", 4,100,paint);
        paint.setColor(Color.BLACK);
        verificarQuebra(110);
    }
}



