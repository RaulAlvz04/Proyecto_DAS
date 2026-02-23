package com.example.proyecto_das.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Pelicula.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PeliculaDAO peliculaDao();
}
