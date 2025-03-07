package edu.itsco.agendaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.itsco.agendaapp.models.Cita;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.ViewHolder> {

    List<Cita> listaCitas;

    public OnItemClicked onClick;

    public CitasAdapter(List<Cita> listaCitas, OnItemClicked onClick) {
        this.listaCitas = listaCitas;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public CitasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_citas, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull CitasAdapter.ViewHolder holder, int position) {
        Cita cita = listaCitas.get(position);

        holder.tvNomCliente.setText(cita.nombre_cliente.toUpperCase());
        holder.tvTelCliente.setText(cita.tel_cliente);
        holder.tvAsuntoCliente.setText(cita.asuntoCita);
        holder.tvHoraCita.setText(cita.horaCita);
        holder.tvDiaCita.setText(cita.diaCita);

        holder.ibtnEditar.setOnClickListener(v -> {
            onClick.editarCita(cita);
        });

        holder.ibtnEliminar.setOnClickListener(v -> {
            onClick.borrarCita(cita);
        });
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNomCliente, tvTelCliente, tvAsuntoCliente, tvHoraCita, tvDiaCita;
        ImageButton ibtnEditar, ibtnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNomCliente = itemView.findViewById(R.id.tvNomCliente);
            tvTelCliente = itemView.findViewById(R.id.tvTelCliente);
            tvAsuntoCliente = itemView.findViewById(R.id.tvAsuntoCliente);
            tvHoraCita = itemView.findViewById(R.id.tvHoraCita);
            tvDiaCita = itemView.findViewById(R.id.tvDiaCita);
            ibtnEditar = itemView.findViewById(R.id.ibtnEditar);
            ibtnEliminar = itemView.findViewById(R.id.ibtnBorrar);
        }
    }

    public interface OnItemClicked{
        void editarCita(Cita cita);
        void borrarCita(Cita cita);
    }

    public void filtrarCliente(List<Cita> listaFiltrada){
        this.listaCitas = listaFiltrada;
        notifyDataSetChanged();
    }
}
