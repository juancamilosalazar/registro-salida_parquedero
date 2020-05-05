package co.com.k4soft.parqueaderouco.view.moviento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.cert.Extension;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.entidades.Tarifa;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;
import co.com.k4soft.parqueaderouco.utilities.ActionBarUtil;
import co.com.k4soft.parqueaderouco.utilities.DateUtil;

public class MovimientoActivity extends AppCompatActivity {

    private DataBaseHelper db;
    private ActionBarUtil actionBarUtil;
    @BindView(R.id.txtPlaca)
    public EditText txtPlaca;
    @BindView(R.id.textView7)
    public TextView txtTotalHoras;
    @BindView(R.id.txtTotal)
    public TextView txtTotal;
    @BindView(R.id.tipoTarifaSpinner)
    public  Spinner tipoTarifaSpinner;
    @BindView(R.id.btnIngreso)
    public Button btnIngreso;
    @BindView(R.id.btnSalida)
    public Button btnSalida;
    @BindView(R.id.layoutDatos)
    public ConstraintLayout layoutDatos;
    private List<Tarifa> listaTarifas;
    private Movimiento movimiento;
    private Tarifa tarifa;
    private String[] arrayTarifas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento);
        ButterKnife.bind(this);
        initComponents();
        hideComponents();
        cargarSpinner();
        spinnerOnItemSelected();
    }

    private void spinnerOnItemSelected() {
        tipoTarifaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tarifa = listaTarifas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargarSpinner() {
       listaTarifas = db.getTarifaDAO().listar();
        if(listaTarifas.isEmpty()){
            Toast.makeText(getApplication(),R.string.sin_tarifas,Toast.LENGTH_SHORT).show();
            finish();
        }else{
           arrayTarifas = new String[listaTarifas.size()];
            for(int i = 0; i < listaTarifas.size(); i++){
                arrayTarifas[i] = listaTarifas.get(i).getNombre();
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,arrayTarifas);
            tipoTarifaSpinner.setAdapter(arrayAdapter);

        }
    }

    private void hideComponents() {
        tipoTarifaSpinner.setVisibility(View.GONE);
        btnIngreso.setVisibility(View.GONE);
        btnSalida.setVisibility(View.GONE);
        layoutDatos.setVisibility(View.GONE);
    }

    private void initComponents() {
        db = DataBaseHelper.getDBMainThread(this);
        actionBarUtil = new ActionBarUtil(this);
        actionBarUtil.setToolBar(getString(R.string.registrsr_ingreso_salida));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void buscarPlaca(View view) {
       movimiento = db.getMovimientoDAO().findByPLaca(txtPlaca.getText().toString());
       hideComponents();
      if(movimiento == null){
          showComponentesIngreso();
      }else if(movimiento.isFinalizaMovimiento())
          showComponentesSalida();
      else
          showComponentesSalida();
    }

    private void showComponentesSalida() {
        btnSalida.setVisibility(View.VISIBLE);
    }


    private void showComponentesIngreso() {
        tipoTarifaSpinner.setVisibility(View.VISIBLE);
        btnIngreso.setVisibility(View.VISIBLE);
    }

    public void registrarIngreso(View view) {
        if(tarifa == null){
            Toast.makeText(getApplicationContext(),R.string.debe_seleccionar_tarifa, Toast.LENGTH_SHORT).show();
        }else if(movimiento == null){
            movimiento = new Movimiento();
            movimiento.setPlaca(txtPlaca.getText().toString());
            movimiento.setIdTarifa(tarifa.getIdTarifa());
            movimiento.setFechaEntrada(DateUtil.convertDateToString(new Date()));
            new InsercionMoviento().execute(movimiento);
            movimiento = null;
            hideComponents();
        }
    }

    public void registrarSalida(View view) throws ParseException {
        layoutDatos.setVisibility(View.VISIBLE);
        Date entrada = DateUtil.convertStringToDate(movimiento.getFechaEntrada());
        Date salida = new Date();
        Long horas = calcularHoras(entrada.getTime(),salida.getTime());
        String valorPorPagar = calcularValorPorPagar(horas);
        movimiento.setFechaSalida(DateUtil.convertDateToString(salida));
        movimiento.setValorPagado(valorPorPagar);
        movimiento.setFinalizaMovimiento(true);
        db.getMovimientoDAO().update(movimiento);
        setComponentsSalida(horas.toString(),valorPorPagar);
        showComponentesSalida();
        disableButtonRegistrar();
    }

    private void disableButtonRegistrar() {
        btnSalida.setVisibility(View.INVISIBLE);
    }

    private void setComponentsSalida(String horas, String valorPorPagar) {
        txtTotalHoras.setText(horas);
        txtTotal.setText(valorPorPagar);
    }

    private String calcularValorPorPagar(Long horas) {
        Tarifa valorTarifa = db.getTarifaDAO().findById(movimiento.getIdTarifa());
        Double valor = valorTarifa.getPrecio()*horas.doubleValue();
        return valor.toString();
    }

    private Long calcularHoras(long entrada, long salida) {
        long diff = salida-entrada ;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;

        if(diffMinutes>0){
            diffHours = diffHours+1;
        }
        return diffHours;
    }

    private class InsercionMoviento extends AsyncTask<Movimiento, Void,Void>{

        @Override
        protected Void doInBackground(Movimiento... movimientos) {
            DataBaseHelper.getSimpleDB(getApplicationContext()).getMovimientoDAO().insert(movimientos[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(),R.string.informacion_guardada_exitosamente, Toast.LENGTH_SHORT).show();
        }
    }

}
