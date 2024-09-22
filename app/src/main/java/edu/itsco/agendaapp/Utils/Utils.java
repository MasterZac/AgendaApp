package edu.itsco.agendaapp.Utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import edu.itsco.agendaapp.database.AppDatabase;

public class Utils {

    final private String DB_NAME = "dbAgendaApp";

    AppDatabase db;

    public AppDatabase getAppDatabase(Context context){
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME).build();
        return db;
    }
}
