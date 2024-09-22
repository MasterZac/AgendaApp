package edu.itsco.agendaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import edu.itsco.agendaapp.Utils.Utils;
import edu.itsco.agendaapp.database.AppDatabase;
import edu.itsco.agendaapp.databinding.FragmentAdminCitasBinding;
import edu.itsco.agendaapp.models.Cita;
import es.dmoral.toasty.Toasty;

public class AdminCitasFragment extends Fragment implements CitasAdapter.OnItemClicked {

    FragmentAdminCitasBinding binding;

    List<Cita> listaCitas = new ArrayList<>();
    CitasAdapter citasAdapter = new CitasAdapter(listaCitas, this);

    AppDatabase db;
    Cita cita = new Cita();
    Boolean isValido = false;
    Boolean isEditando = false;

    public AdminCitasFragment() {
        // Required empty public constructor
    }

    public static AdminCitasFragment newInstance(String param1, String param2) {
        return new AdminCitasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_admin_citas, container, false);
        binding = FragmentAdminCitasBinding.inflate(getLayoutInflater());
        View vista = binding.getRoot();

        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Administra Citas");

        db = new Utils().getAppDatabase(getContext());

        setupToolbarMenu();
        obtenerCitas();

        binding.svCliente.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarCliente(newText);
                return false;
            }
        });
        return vista;
    }

    private void filtrarCliente(String newText) {
        ArrayList<Cita> listaFiltrada = new ArrayList<>();
        for (Cita cita : listaCitas){
            if (cita.nombre_cliente.toLowerCase().contains(newText.toLowerCase())){
                listaFiltrada.add(cita);
            }
        }
        citasAdapter.filtrarCliente(listaFiltrada);
    }

    //Realizar las peticiones en un hilo secundario para que gestione y procese la informacion
    //para que no sature los hilos principales
    private void obtenerCitas() {
        AsyncTask.execute(() -> {//Con esta linea se ejecuta en un hilo secundario
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread(() -> {//Con esta linea se ejecuta en el hilo principal
                setupRecyclerView();
            });
        });
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.rvCitas.setLayoutManager(layoutManager);
        citasAdapter = new CitasAdapter(listaCitas, this);
        binding.rvCitas.setAdapter(citasAdapter);
    }

    private void setupToolbarMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {

            //Aqui se pone la instancia u/o xml del menu
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_toolbar, menu);
            }

            //Detecta que elemento del toolbar se oprimio y que quiere que realize
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_agregar){
                    lanzarAlertDialogCita(getActivity());
                    return true;
                }

                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void lanzarAlertDialogCita(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View vista = inflater.inflate(R.layout.alert_dialog_add_update_cita, null);
        builder.setView(vista);
        builder.setCancelable(false);

        EditText etNomCliente, etTelCliente, etAsuntoCita;
        TextView tvTituloAlert, tvHora;
        ImageButton ibtnHora;
        Spinner spiDias;

        etNomCliente = vista.findViewById(R.id.etNomCliente);
        etTelCliente = vista.findViewById(R.id.etTelCliente);
        etAsuntoCita = vista.findViewById(R.id.etAsuntoCita);
        tvTituloAlert = vista.findViewById(R.id.tvTituloAlert);
        tvHora = vista.findViewById(R.id.tvHora);
        ibtnHora = vista.findViewById(R.id.ibtnHora);
        spiDias = vista.findViewById(R.id.spiDias);

        //aqui obtengo la lista de dias que esta en strings.xml que son los dias de la semana
        String[] listaDias = activity.getResources().getStringArray(R.array.dias_semana);
        ArrayAdapter arrayAdapter = new ArrayAdapter(activity, R.layout.item_spinner, listaDias);
        spiDias.setAdapter(arrayAdapter);

        if (isEditando){
            tvTituloAlert.setText("ACTUALIZAR CITA");
            etNomCliente.setText(cita.nombre_cliente);
            etTelCliente.setText(cita.tel_cliente);
            etAsuntoCita.setText(cita.asuntoCita);
            tvHora.setText(cita.horaCita);
            spiDias.setSelection(arrayAdapter.getPosition(cita.diaCita));
        }

        //Obtener hora del timepickerDialog
        ibtnHora.setOnClickListener(v -> {
            obtenerHora(tvHora);
        });

        builder.setPositiveButton("Aceptar", (dialogInterface, i) -> {

            if (!isEditando){
                cita.id_cita = String.valueOf(System.currentTimeMillis());
            }

            cita.nombre_cliente = etNomCliente.getText().toString().trim();
            cita.tel_cliente = etTelCliente.getText().toString().trim();
            cita.asuntoCita = etAsuntoCita.getText().toString().trim();
            cita.horaCita = tvHora.getText().toString().trim();
            cita.diaCita = spiDias.getSelectedItem().toString();

            validarCampos();
            if (isValido){
                if (isEditando){
                    actualizarCita();
                    isEditando = false;
                } else {
                    agregarCita();
                }
            } else {
                Toasty.error(getContext(), "Faltaron campos por llenar obligatorios", Toasty.LENGTH_LONG, true).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialogInterface, i) -> {
            Toast.makeText(activity, "Cancelar", Toast.LENGTH_SHORT).show();
        });

        builder.create();
        builder.show();
    }

    private void agregarCita() {
        AsyncTask.execute( () -> {
            db.citaDao().agregarCita(cita);
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread( () ->{
                setupRecyclerView();
            });
        });
    }

    private void validarCampos() {
        if (
                cita.nombre_cliente.isEmpty()
                || cita.tel_cliente.isEmpty()
                || cita.horaCita.isEmpty()
                || cita.diaCita.contains("*")

        ) {
            isValido = false;
        } else {
            isValido = true;
        }
    }

    private void obtenerHora(TextView tvHora) {
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
            String horaFormateada = (hourOfDay < 10)? "0" + hourOfDay : String.valueOf(hourOfDay);
            String minutoFormateado = (minute < 10)? "0" + minute : String.valueOf(minute);
            tvHora.setText(horaFormateada + ":" + minutoFormateado);
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false);

        recogerHora.show();
    }

    private void actualizarCita(){
        AsyncTask.execute( () -> {
            db.citaDao().actualizarCita(cita);
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread(() -> {
                setupRecyclerView();
                Toasty.success(getContext(), "Cita actualizada", Toasty.LENGTH_LONG, true).show();
            });
        });
    }

    @Override
    public void editarCita(Cita cita) {
        isEditando = true;
        this.cita = cita;
        lanzarAlertDialogCita(getActivity());
    }

    @Override
    public void borrarCita(Cita cita) {
        AsyncTask.execute( ()->{
            db.citaDao().eliminarCita(cita);
            listaCitas = db.citaDao().obtenerCitas();
            getActivity().runOnUiThread( () ->{
                setupRecyclerView();
            });
        });
        Toasty.info(getContext(), "Se eliminp el registro exitosamente", Toasty.LENGTH_LONG).show();
    }
}