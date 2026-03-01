package com.example.proyecto_das.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UsuarioDAO {
    @Query("SELECT * from usuarios")
    List<Usuario> getAll();

    @Insert
    void insert(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Update
    void update(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :pass LIMIT 1")
    Usuario login(String email, String pass);

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    Usuario buscarPorEmail(String email);
}