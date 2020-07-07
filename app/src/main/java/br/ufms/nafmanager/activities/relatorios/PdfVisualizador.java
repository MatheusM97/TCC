package br.ufms.nafmanager.activities.relatorios;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;

import java.io.File;

import br.ufms.nafmanager.R;

public class PdfVisualizador extends AppCompatActivity {

    PDFView pdf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pdf);
        pdf = findViewById(R.id.visualizador);

        File file = new File(Environment.getExternalStorageDirectory() + "/RelatorioNAF.pdf");
        if(file.exists()){
            pdf.fromFile(file)
                    .password(null)
                    .defaultPage(0)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .onDraw(new OnDrawListener() {
                        @Override
                        public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                        }
                    })
                    .onPageError(new OnPageErrorListener() {
                        @Override
                        public void onPageError(int page, Throwable t) {
                            Toast.makeText(PdfVisualizador.this, "ERRO", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .onRender(new OnRenderListener() {
                        @Override
                        public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                            pdf.fitToWidth();
                        }
                    })
                    .enableAnnotationRendering(true)
                    .invalidPageColor(Color.WHITE)
                    .load();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

//        if(id == R.id.compartilhar){
//            File file =  new File(Environment.getExternalStorageDirectory() + "/RelatorioNAF.pdf");
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_SEND);
//            intent.setType("application/pdf");
//            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//            startActivity(intent.createChooser(intent,"Compartilhar"));
//        }

        return true;
    }
}