package br.ufms.nafmanager.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.ufms.nafmanager.R;
import br.ufms.nafmanager.model.Cidade;
import br.ufms.nafmanager.model.Estado;
import br.ufms.nafmanager.model.Unidade;
import br.ufms.nafmanager.model.Universidade;
import br.ufms.nafmanager.model.Usuario;
import br.ufms.nafmanager.persistencies.Persistencia;


public class UniversidadeInserir extends AppCompatActivity {

    private EditText universidadeNome;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private Spinner spinnerUnidades;
    private Spinner spinnerCoordenador;
    private List<Cidade> cidadeLista;
    private List<Unidade> unidadeLista;
    private List<String> coordSel;
    private List<String> coorSelBk;
    private TextView coordenadorSelect;
    private List<Usuario> coordLista;
    private List<Usuario> coordListaSel;
    private ArrayAdapter<Usuario> usuarioAdapter;
    private ListView lv;
    private TextView btn_inserir;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.universidade_inserir);
        btn_inserir = (TextView) findViewById(R.id.btn_inserirUniversidade);
//        coordenadorSelect = (TextView) findViewById(R.id.sp_universidade_coordenador);
        universidadeNome = (EditText) findViewById(R.id.et_universidade_nome);
        coordSel = new ArrayList<>();
        coorSelBk = new ArrayList<>();
        List<Estado> estadoLista = new ArrayList<>();
        cidadeLista = new ArrayList<Cidade>();

        spinnerEstado = (Spinner) findViewById(R.id.sp_universidade_estado_nome);
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

        this.spinnerCidade = (Spinner) findViewById(R.id.sp_universidade_cidade_nome);

        this.spinnerUnidades = (Spinner) findViewById(R.id.sp_universidade_unidade);
        unidadeLista = Persistencia.getInstance().getUnidades();
        ArrayAdapter<Unidade> undAdp = new ArrayAdapter<Unidade>(this, android.R.layout.simple_spinner_dropdown_item, unidadeLista);
        undAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidades.setAdapter(undAdp);

        this.btn_inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Universidade universidade = new Universidade();

                    if (universidadeNome.getText() != null && universidadeNome.getText().length() > 0) {
                        universidade.setNome(universidadeNome.getText().toString());
                    }

                    if(spinnerEstado.getSelectedItem() != null && spinnerEstado.getSelectedItem().toString().length() >0){
                        Estado estado = (Estado) spinnerEstado.getSelectedItem();
                        universidade.setEstadoId(estado.getId());
                        universidade.setEstadoNome(estado.getNome());
                        universidade.setEstadoSigla(estado.getSigla());
                    }

                    if(spinnerCidade.getSelectedItem() != null && spinnerCidade.getSelectedItem().toString().length()>0){
                        Cidade cidade = (Cidade) spinnerCidade.getSelectedItem();
                        universidade.setCidadeId(cidade.getId());
                        universidade.setCidadeNome(cidade.getNome());
                    }

                    if(spinnerUnidades.getSelectedItem() != null && spinnerUnidades.getSelectedItem().toString().length()>0){
                        Unidade und = (Unidade)spinnerUnidades.getSelectedItem();
                        universidade.setUnidadeId(und.getId());
                        universidade.setUnidadeNome(und.getNome());
                    }

                    if (validar(universidade)) {
                        Persistencia.getInstance().persistirObjeto(universidade);
                        if (universidade.getId() != null && universidade.getId().length() > 0) {
                            Toast.makeText(UniversidadeInserir.this, "Universidade salva com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UniversidadeInserir.this, "Falha ao salvar a universidade!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(UniversidadeInserir.this, "Falha ao salvar a universidade!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAdapterCidade() {
        ArrayAdapter<Cidade> cidadeAdptr = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cidadeLista);
        cidadeAdptr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinnerCidade.setAdapter(cidadeAdptr);
    }

    public boolean validar(Universidade und) {
        boolean retorno = true;

        if (und.getNome() == null || und.getNome().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar um nome", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if (und.getEstadoId() == null || und.getEstadoId().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar um estado", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        if (und.getCidadeId() == null || und.getCidadeId().length() <= 0) {
            Toast.makeText(this, "É necessário selecionar uma cidade", Toast.LENGTH_SHORT).show();
            retorno = false;
        }

        return retorno;
    }
}
