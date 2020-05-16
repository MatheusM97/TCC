package br.ufms.nafmanager.activities.relatorios;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.activities.CustomActivity;

public class RelatorioPrincipal extends CustomActivity {

    Button rel;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_relatorios);

        rel = findViewById(R.id.btn_relatorio);
        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gerar();
            }
        });
    }

    public void gerar(){
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400,600,1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawText("Relatorio", 40,50, paint);
        document.finishPage(page);

        File file = new File(Environment.getExternalStorageDirectory(), "/Relatorio.pdf");

        try{
            document.writeTo(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();

        Intent intent = new Intent(RelatorioPrincipal.this, PdfVisualizador.class);
        startActivity(intent);
    }

}
