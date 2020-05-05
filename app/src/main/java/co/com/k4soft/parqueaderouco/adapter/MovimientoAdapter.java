package co.com.k4soft.parqueaderouco.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.com.k4soft.parqueaderouco.R;
import co.com.k4soft.parqueaderouco.entidades.Movimiento;

public class MovimientoAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private List<Movimiento> movimientosOut;
    private List<Movimiento> movimientosin;


    public MovimientoAdapter(Context context, List<Movimiento> movimientos){
        movimientosOut = movimientos;
        movimientosin = movimientos;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return movimientosOut.size();
    }

    @Override
    public Movimiento getItem(int position) {
        return movimientosOut.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.movimiento_item_layout, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.placaCarroTxt.setText(movimientosOut.get(position).getPlaca());
        holder.fechaIngresoTxt.setText(movimientosOut.get(position).getFechaEntrada());
        if(movimientosOut.get(position).isFinalizaMovimiento()){
            holder.fechaSalidaTxt.setText(movimientosOut.get(position).getFechaSalida());
            holder.valorPagadoTxt.setText(movimientosOut.get(position).getValorPagado());
        }else{
            holder.placaCarroTxt.setBackgroundColor(0xfff00000);
            holder.fechaSalidaTxt.setText(R.string.sin_fecha_salida);
            holder.valorPagadoTxt.setText(R.string.sin_fecha_salida);

        }


        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.placaCarroTxt2)
        TextView placaCarroTxt;
        @BindView(R.id.fechaIngresoTxt2)
        TextView fechaIngresoTxt;
        @BindView(R.id.fechaSalidaTxt)
        TextView fechaSalidaTxt;
        @BindView(R.id.valorPagadoTxt2)
        TextView valorPagadoTxt;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
