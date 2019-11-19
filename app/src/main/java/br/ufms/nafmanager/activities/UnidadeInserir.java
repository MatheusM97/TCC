package br.ufms.nafmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.UnidadeTipo;
import br.ufms.nafmanager.persistencies.Persistencia;

public class UnidadeInserir extends AppCompatActivity {

    private EditText unidadeNome;
    private EditText unidadeRegiaoFiscal;
    private Spinner spinnerTipo;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private Button btn_inserirUnidade;
    private List<Cidade> cidadeLista;
    private AutoCompleteTextView acCidade;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unidade_inserir);
        List<UnidadeTipo> tipoLista = new ArrayList<>();
        List<Estado> estadoLista = new ArrayList<>();
        cidadeLista = new ArrayList<Cidade>();

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
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cidadeLista = Persistencia.getInstance().getCidades((Estado) parent.getSelectedItem());
                setAdapterCidade();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spinnerCidade = (Spinner) findViewById(R.id.sp_cidadeNome);

        this.btn_inserirUnidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Unidade unidade = new Unidade();

                    if (unidadeNome.getText() != null && unidadeNome.getText().length() > 0) {
                        unidade.setNome(unidadeNome.getText().toString());
                    }

                    if (unidadeRegiaoFiscal.getText() != null && unidadeRegiaoFiscal.getText().length() > 0) {
                        unidade.setRegiaoFiscal(Integer.parseInt(unidadeRegiaoFiscal.getText().toString()));
                    }

                    if (spinnerTipo.getSelectedItem() != null && spinnerTipo.getSelectedItem().toString().length() > 0) {
                        UnidadeTipo ut = (UnidadeTipo) spinnerTipo.getSelectedItem();
                        unidade.setTipoId(ut.getId());
                        unidade.setTipoNome(ut.getNome());
                    }

                    if (spinnerEstado.getSelectedItem() != null && spinnerEstado.getSelectedItem().toString().length() > 0) {
                        Estado estado = (Estado) spinnerEstado.getSelectedItem();
                        unidade.setEstadoId(estado.getId());
                        unidade.setEstadoNome(estado.getNome());
                        unidade.setEstadoSigla(estado.getSigla());
                    }

                    if (spinnerCidade.getSelectedItem() != null && spinnerCidade.getSelectedItem().toString().length() > 0) {
                        Cidade cidade = (Cidade) spinnerCidade.getSelectedItem();
                        unidade.setCidadeId(cidade.getId());
                        unidade.setCidadeNome(cidade.getNome());
                    }

                    if (validar(unidade)) {
                        Persistencia.getInstance().persistirObjeto(unidade);
                        if (unidade.getId() != null && unidade.getId().length() > 0) {
                            Toast.makeText(UnidadeInserir.this, "Unidade salva com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UnidadeInserir.this, "Falha ao salvar a unidade!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(UnidadeInserir.this, "Falha ao salvar a unidade!", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void setAdapterCidade() {
        ArrayAdapter<Cidade> cidadeAdptr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeLista);
        cidadeAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCidade.setAdapter(cidadeAdptr);
    }

    public boolean validar(Unidade und) {

        if (und.getNome() == null || und.getNome().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar um nome", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (und.getTipoId() == null || und.getTipoId().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar um tipo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (und.getEstadoId() == null || und.getEstadoId().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar um estado", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (und.getCidadeId() == null || und.getCidadeId().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar uma cidade", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (und.getRegiaoFiscal() == 0) {
            Toast.makeText(this, "É necessário selecionar uma região fiscal", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
