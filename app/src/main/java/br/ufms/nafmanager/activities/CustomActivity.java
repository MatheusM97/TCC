package br.ufms.nafmanager.activities;

import android.app.ProgressDialog;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;

public class CustomActivity extends AppCompatActivity {
    protected int marcador = 0;
    protected boolean editando = false;
    protected ProgressDialog progressDialog;

    protected void showDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Aguarde...");
        this.progressDialog.setCancelable(false);
        this.progressDialog.setContentView(R.layout.layout_carregando);
        this.progressDialog.show();
    }

    protected void hideDialog(){
        this.progressDialog.dismiss();
    }
}
