package br.ufms.nafmanager.activities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.util.DisplayMetrics;
import android.widget.Button;

import java.text.SimpleDateFormat;

import br.ufms.nafmanager.activities.relatorios.RelatorioObjeto;

public class RelatorioActivity extends CustomActivity{
    protected PdfDocument document = new PdfDocument();
    protected Paint paint = new Paint();
    protected Canvas canvas;
    protected PdfDocument.Page page;
    protected PdfDocument.PageInfo pageInfo;


    protected int pageW = 400;
    protected int pageH = 600;
    protected int pageNum = 1;
    protected int hIndex = 120;

    protected Button btnGerar;

    protected SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    protected RelatorioObjeto relatorio = new RelatorioObjeto();

    protected void verificarQuebra(int quebra) {
        hIndex += 15;

        if(hIndex+ 10 >= pageH){
            quebraPagina(quebra);
        }
    }

    protected void carregarTamanhoTela(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if(displayMetrics.densityDpi == 480){
            pageH = 620;
            pageW = 400;
        }
    }

    protected void quebraPagina(int quebra){
        hIndex = quebra;
        pageNum++;

        document.finishPage(page);
        pageInfo = new PdfDocument.PageInfo.Builder(pageW,pageH,pageNum).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        imprimirCabecalho(pageInfo);
    }

    protected void imprimirCabecalho(PdfDocument.PageInfo pageInfo){
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

    protected String ajustarNome(String nome, int valor) {
        if(nome.length() > valor){
            return nome.substring(0, valor);
        }
        return nome;
    }
}
