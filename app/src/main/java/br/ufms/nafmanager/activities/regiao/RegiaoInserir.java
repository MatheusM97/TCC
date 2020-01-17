package br.ufms.nafmanager.activities.regiao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Regiao;
import br.ufms.nafmanager.persistencies.Persistencia;

public class RegiaoInserir extends AppCompatActivity{

    private Regiao regiao;
    private Button btnCadastrar;
    private EditText nome;
    private EditText numero;
    private boolean edicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regiao_inserir);

        this.edicao = false;
        if(getIntent().getSerializableExtra("regiao") != null){
            this.regiao = (Regiao) getIntent().getSerializableExtra("regiao");
            this.edicao = true;
        }

        vincularComponentes();

        if(edicao && regiao != null){
            carregarTela();
        }
    }

    private void vincularComponentes() {
        nome = (EditText) findViewById(R.id.et_regiaoNome);
        numero = (EditText) findViewById(R.id.et_regiaoNumero);
        btnCadastrar = (Button)findViewById(R.id.btn_criarRegiao);

        this.btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inserir();
            }
        });
    }

    private void inserir(){
        copiarTela();
        if(regiao.salvar()){
            Persistencia.getInstance().setRegiaoAtual(regiao);
            finish();
        }

        Toast.makeText(this, regiao.getMensagem(), Toast.LENGTH_SHORT).show();
    }

    private void carregarTela() {
        if(regiao.getNome() != null && regiao.getNome().length() >0)
            nome.setText(regiao.getNome());

        if(regiao.getNumero() != null && regiao.getNumero() != 0)
            numero.setText(regiao.getNumero().toString());
    }

    private void copiarTela(){
        if (!edicao && (regiao == null || regiao.getId() == null || regiao.getId().length() == 0)) {
            regiao = new Regiao();
        }
        if (this.nome.getText() != null && this.nome.getText().length() > 0) {
            regiao.setNome(this.nome.getText().toString());
        }

        if (this.numero.getText() != null && this.numero.getText().length() > 0) {
            regiao.setNumero(Long.parseLong(this.numero.getText().toString()));
        }
    }
}

