package br.ufms.nafmanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.persistencies.Persistencia;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash);
        Persistencia.getInstance().buscaVersao();

//        Persistencia.getInstance().inserirDadosDefault();
        Persistencia.getInstance().carregaRegioes();
        Persistencia.getInstance().carregaUniversidades();
        Persistencia.getInstance().carregaUnidades();
        Persistencia.getInstance().carregaEstados();

        telaAguardando();
    }

    private void telaAguardando() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                podeInicializar();
            }
        }, 500);
    }

    private void podeInicializar(){
        if(Persistencia.getInstance().carregouEstados && Persistencia.getInstance().carregouUniversidades){
            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            telaAguardando();
        }
    }
}
