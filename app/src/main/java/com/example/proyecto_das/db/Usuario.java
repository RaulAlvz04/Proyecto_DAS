package com.example.proyecto_das.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    public int id;;
    public String password;

    public String email;

    public Usuario(@NonNull String password, String email) {
        this.email = email;
        this.password = password;
    }
}