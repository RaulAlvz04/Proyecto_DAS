package com.example.proyecto_das.db;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface PeliculaDAO {
    @Query("SELECT * from peliculas")
    List<Pelicula> getAll();

    @Insert
    void insert(Pelicula pelicula);

    @Delete
    void delete(Pelicula pelicula);

    @Update
    void update(Pelicula pelicula);

    @Query("SELECT * FROM peliculas WHERE genero = :genero")
    List<Pelicula> findByGenero(String genero);
}
