package co.com.k4soft.parqueaderouco.view.moviento;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.adapter.MovimientoAdapter;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;
import co.com.k4soft.parqueaderouco.persistencia.room.DataBaseHelper;
import co.com.k4soft.parqueaderouco.utilities.ActionBarUtil;

public class MovimientoBaseAdapterActivity extends AppCompatActivity {
    @BindView(R.id.listViewMovimiento)
    public ListView listViewMovimientos;
    private MovimientoAdapter movimientoAdapter;
    DataBaseHelper db;
    private ActionBarUtil actionBarUtil;
    public List<Movimiento> movimientos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimiento_base_adapter);
        ButterKnife.bind(this);
        initComponents();
        loadMovimientos();
    }

    private void initComponents() {
        db = DataBaseHelper.getDBMainThread(this);
        actionBarUtil = new ActionBarUtil(this);
        actionBarUtil.setToolBar(getString(R.string.movimientos));
    }

    private void loadMovimientos() {
        movimientos = db.getMovimientoDAO().listar();
        movimientoAdapter = new MovimientoAdapter(this,movimientos);
        listViewMovimientos.setAdapter(movimientoAdapter);
    }
}
