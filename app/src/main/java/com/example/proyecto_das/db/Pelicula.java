package com.example.proyecto_das.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "peliculas")
public class Pelicula {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "titulo")
    public String titulo;

    @ColumnInfo(name = "anno")
    public String anno;

    @ColumnInfo(name = "genero")
    public String genero;

    @ColumnInfo(name = "valoracion")
    public float valoracion;

    @ColumnInfo(name = "opinion")
    public String opinion;

    @ColumnInfo(name = "esFavorito")
    public boolean esFavorito;

    @ColumnInfo(name = "imagen")
    public String imagen;

    @ColumnInfo(name = "esPendiente")
    public boolean esPendiente;

    @ColumnInfo(name = "idUsuario")
    public int idUsuario;

    // Constructor
    public Pelicula(String titulo, String anno, String genero, float valoracion, String opinion, boolean esFavorito, String imagen, boolean esPendiente, int idUsuario ) {
        this.titulo = titulo;
        this.anno = anno;
        this.genero = genero;
        this.valoracion = valoracion;
        this.opinion = opinion;
        this.esFavorito = esFavorito;
        this.imagen = imagen;
        this.esPendiente = esPendiente;
        this.idUsuario = idUsuario;
    }

    // Getters
    public int getId() {
        return id;
    }

    public float getValoracion() {
        return valoracion;
    }

    public String getAnno() {
        return anno;
    }

    public String getGenero() {
        return genero;
    }

    public String getOpinion() {
        return opinion;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isEsFavorito() {return esFavorito;}

    public String getImagen(String imagen) {return imagen;}

    public boolean isEsPendiente() {return esPendiente;}

    public int getIdUsuario() {return idUsuario;}

    // Setters
    public void setAnno(String anno) {
        this.anno = anno;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public void setEsFavorito(boolean esFavorito) {this.esFavorito = esFavorito;}

    public void setImagen(String imagen) {this.imagen = imagen;}

    public void setEsPendiente(boolean esPendiente) {this.esPendiente = esPendiente;}

    public void setIdUsuario(int idUsuario) {this.idUsuario = idUsuario;}
}
