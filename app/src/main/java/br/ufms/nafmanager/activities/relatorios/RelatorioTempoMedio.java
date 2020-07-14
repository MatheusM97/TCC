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
import br.ufms.nafmanager.model.Atendimento;
import br.ufms.nafmanager.model.AtendimentoTipo;
import br.ufms.nafmanager.model.Participante;
import br.ufms.nafmanager.model.Relatorios;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.persistencies.Persistencia;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class RelatorioTempoMedio extends CustomActivity {

    private Button btnGerar;
    private TextView tvDataInicio;
    private TextView tvDataFim;
    private ArrayList<Atendimento> atendimentos;
    private Date dataInicial;
    private Date dataFinal;
    private String dataInicialStr;
    private String dataFinalStr;
    private String universidadeId = null;
    private String atendidoTipoId = null;
    private String atendidoTipoNome = null;
    private String universidadeNome = null;
    private Spinner spUniversidade;
    private Spinner spTipoAtendimento;
    private PdfDocument document = new PdfDocument();
    private Paint paint = new Paint();
    private Canvas canvas;
    private PdfDocument.Page page;
    private PdfDocument.PageInfo pageInfo;

    private ArrayList<AtendimentoTipo> atendimentoTipoLista = new ArrayList<AtendimentoTipo>();
    private ArrayAdapter<AtendimentoTipo> atendimentoTipoAdp;

    private ArrayList<Universidade> universidadeLista = new ArrayList<>();
    private ArrayAdapter<Universidade> univAdp;

    private DatePickerDialog.OnDateSetListener dateInicioLister;
    private DatePickerDialog.OnDateSetListener dateFimLister;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    private int pageW = 400;
    private int pageH = 600;
    private int pageNum = 1;
    private int hIndex = 140;
    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorio_tempo_medio);

        vincularComponentes();
    }

    private void vincularComponentes() {

        this.tvDataInicio = (TextView) findViewById(R.id.et_participanteInicio);
        this.tvDataInicio.setText(sdf.format(new Date()));

        this.tvDataFim = findViewById(R.id.et_participanteFim);
        this.tvDataFim.setText(sdf.format(new Date()));

        this.spTipoAtendimento = findViewById(R.id.sp_tipoAtendimentoId);
        this.spUniversidade = findViewById(R.id.sp_universidadeId);

        universidadeLista = new ArrayList<>();
        universidadeLista.add(new Universidade(null, "Todas"));

        for(Universidade un: Persistencia.getInstance().getUniversidades()){
            universidadeLista.add(un);
        }

        univAdp = new ArrayAdapter<Universidade>(this, android.R.layout.simple_spinner_dropdown_item, universidadeLista);
        univAdp .setDropDownViewResource(android.R.layout.simple_list_item_1);
//        univAdp .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spUniversidade.setAdapter(univAdp);

        atendimentoTipoLista = new ArrayList<>();
        atendimentoTipoLista.add(new AtendimentoTipo(null, "Todos"));

        Persistencia.getInstance().getAtendimentoTipoLocal();

        for(AtendimentoTipo at: Persistencia.getInstance().getAtendimentosTipo()){
            atendimentoTipoLista.add(at);
        }

        atendimentoTipoAdp = new ArrayAdapter<AtendimentoTipo>(this, android.R.layout.simple_spinner_dropdown_item, atendimentoTipoLista);
//        atendimentoTipoAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        atendimentoTipoAdp.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spTipoAtendimento.setAdapter(atendimentoTipoAdp);

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
                        RelatorioTempoMedio.this,
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
                        RelatorioTempoMedio.this,
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            dataInicialStr = tvDataInicio.getText().toString();
            dataFinalStr = tvDataFim.getText().toString();
            dataInicial = sdf.parse(dataInicialStr);
            dataFinal = sdf.parse(dataFinalStr);

            universidadeId = null;
            atendidoTipoId = null;
            universidadeNome = null;
            atendidoTipoNome = null;

            if (spUniversidade.getSelectedItem() != null && spUniversidade.getSelectedItem().toString().length() > 0) {
                Universidade univ = (Universidade) spUniversidade.getSelectedItem();
                if(univ.getId() != null){
                    universidadeId = univ.getId();
                    universidadeNome = univ.getNome();
                }
            }

            if (spTipoAtendimento.getSelectedItem() != null && spTipoAtendimento.getSelectedItem().toString().length() > 0) {
                AtendimentoTipo atTipo = (AtendimentoTipo) spTipoAtendimento.getSelectedItem();
                if(atTipo.getId() != null){
                    atendidoTipoId = atTipo.getId();
                    atendidoTipoNome = atTipo.getNome();
                }
            }

            Persistencia.getInstance().carregaAtendimentosTempoMedio(dataInicial, dataFinal, atendidoTipoId, universidadeId);

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
        ArrayList<Relatorios> relatorios = new ArrayList<>();
        relatorios = Persistencia.getInstance().getRelatorios();

        hIndex = 120;
        index = 1;

        document = new PdfDocument();
        pageInfo = new PdfDocument.PageInfo.Builder(pageW,pageH,pageNum).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        imprimirCabecalho(pageInfo);

        for (Relatorios relatorio: relatorios) {
            for(Participante participante: relatorio.getParticipantes()){
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setTextSize(13.0f);
                canvas.drawText(String.format("%03d", index), 4,hIndex, paint); //atendimento numero

                String atendimento = "Atendimentos em geral";
                if(atendidoTipoNome != null && atendidoTipoNome.length() >0){
                    atendimento = atendidoTipoNome;
                }

                canvas.drawText(ajustarNome(atendimento, 50), 32, hIndex, paint);
                canvas.drawText(participante.getMediaString(), 340, hIndex, paint);
            }
        }

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

        Intent intent = new Intent(RelatorioTempoMedio.this, PdfVisualizador.class);
        startActivity(intent);
    }

    private void imprimirCabecalho(PdfDocument.PageInfo pageInfo) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25.0f);
        canvas.drawText("NAFApp", pageInfo.getPageWidth()/2,28, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(20.0f);
        canvas.drawText("Relatório de tempo médio de atendimento", pageInfo.getPageWidth()/2,50, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);
        canvas.drawText("Período: de " + dataInicialStr + " até " + dataFinalStr, pageInfo.getPageWidth()/2,68, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15.0f);

        String universidade = "";
        if(universidadeNome != null && universidadeNome.length() > 0){
            universidade = universidadeNome;
        }
        else{
            universidade = "Todas";
        }

        canvas.drawText("Universidade: " + universidade , pageInfo.getPageWidth()/2,84, paint);

        String atendimento = "";
        if(atendidoTipoNome != null && atendidoTipoNome.length() > 0){
            atendimento = ajustarNome(atendidoTipoNome,38);
        }
        else{
            atendimento = "Todos";
        }

        canvas.drawText("Tipo de serviço: " + atendimento , pageInfo.getPageWidth()/2,100, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(15.0f);
        marcarCabecalho( "Nº"+ "  |" + "                           Atendimento"+ "                        |" +  " Média");
        hIndex+= 15;
    }

    private String ajustarNome(String nome, int valor) {
        if(nome.length() > valor){
            return nome.substring(0, valor);
        }
        return nome;
    }

    protected void marcarCabecalho(String header){
        Paint.FontMetrics fm = new Paint.FontMetrics();
        paint.setColor(Color.GRAY);
        paint.getFontMetrics(fm);
        canvas.drawRect(5, hIndex - 12,pageW-5, hIndex+ 4, paint);

        paint.setColor(Color.WHITE);
        canvas.drawText(header, 5, hIndex, paint); //header

        paint.setColor(Color.BLACK);
    }
}