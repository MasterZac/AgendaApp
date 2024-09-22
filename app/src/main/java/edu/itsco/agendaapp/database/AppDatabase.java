package edu.itsco.agendaapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import edu.itsco.agendaapp.dao.CitaDao;
import edu.itsco.agendaapp.models.Cita;

@Database( entities = {Cita.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CitaDao citaDao();
}
