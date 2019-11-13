package br.ufms.nafmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.UnidadeTipo;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UnidadeActivity extends AppCompatActivity {

    private EditText unidadeNome;
    //    private EditText unidadeResponsavelId;
    private EditText unidadeRegiaoFiscal;
    private Spinner spinnerTipo;
    private Spinner spinnerEstado;
    private Button btn_inserirUnidade;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_inserir);
        List<UnidadeTipo> tipoLista = new ArrayList<>();
        List<Estado> estadoLista = new ArrayList<>();

        this.unidadeNome = (EditText) findViewById(R.id.et_unidadeNome);
        this.unidadeRegiaoFiscal = (EditText) findViewById(R.id.et_regiaoFiscal);
        this.btn_inserirUnidade = (Button) findViewById(R.id.btn_inserirUnidade);

        spinnerTipo = (Spinner) findViewById(R.id.sp_unidadeTipo);
        tipoLista = Persistencia.getInstance().getUnidadesTipo();
        ArrayAdapter<UnidadeTipo> adp = new ArrayAdapter<UnidadeTipo>(this, android.R.layout.simple_spinner_dropdown_item, tipoLista);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adp);

        spinnerEstado = (Spinner) findViewById(R.id.sp_estadoNome);
        estadoLista = Persistencia.getInstance().getEstados();
        ArrayAdapter<Estado> estAdp = new ArrayAdapter<Estado>(this, android.R.layout.simple_spinner_dropdown_item, estadoLista);
        estAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(estAdp);

        this.btn_inserirUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Unidade unidade = new Unidade();

                unidade.setNome(unidadeNome.getText().toString());
                unidade.setRegiaoFiscal(Integer.parseInt(unidadeRegiaoFiscal.getText().toString()));

                UnidadeTipo ut = (UnidadeTipo) spinnerTipo.getSelectedItem();
                unidade.setTipoId(ut.getId());
                unidade.setTipoNome(ut.getNome());

                Estado estado = (Estado) spinnerEstado.getSelectedItem();
                unidade.setEstadoId(estado.getId());
                unidade.setEstadoNome(estado.getNome());

                Persistencia.getInstance().persistirObjeto(unidade);
                if (unidade.getId() != null && unidade.getId().length() > 0) {
                    Toast.makeText(UnidadeActivity.this, "Unidade salva com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UnidadeActivity.this, "Falha ao salvar a unidade!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


}
