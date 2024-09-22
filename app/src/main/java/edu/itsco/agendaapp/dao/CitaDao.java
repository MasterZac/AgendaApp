package edu.itsco.agendaapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.itsco.agendaapp.models.Cita;

@Dao
public interface CitaDao {
    @Query("SELECT * FROM citas")
    List<Cita> obtenerCitas();

    @Insert
    void agregarCita(Cita cita);

    @Update
    void actualizarCita(Cita cita);

    @Delete
    void eliminarCita(Cita cita);
}
