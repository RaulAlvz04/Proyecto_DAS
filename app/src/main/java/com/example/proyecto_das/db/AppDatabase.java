package com.example.proyecto_das.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Pelicula.class, Usuario.class}, version=3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PeliculaDAO peliculaDao();
    public abstract UsuarioDAO usuarioDAO();
}
