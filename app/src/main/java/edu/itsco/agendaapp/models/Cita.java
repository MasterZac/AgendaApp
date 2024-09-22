package edu.itsco.agendaapp.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "citas")
public class Cita {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id_cita")
    public String id_cita;
    @ColumnInfo(name = "nombre_cliente")
    public String nombre_cliente;
    @ColumnInfo(name = "tel_cliente")
    public String tel_cliente;
    @ColumnInfo(name = "asunto_cliente")
    public String asuntoCita;
    @ColumnInfo(name = "hora_cita")
    public String horaCita;
    @ColumnInfo(name = "dia_cita")
    public String diaCita;
}
