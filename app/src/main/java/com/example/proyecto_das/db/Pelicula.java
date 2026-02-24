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

    @ColumnInfo(name = "sinopsis")
    public String sinopsis;

    @ColumnInfo(name = "esFavorito")
    public boolean esFavorito;

    @ColumnInfo(name = "imagen")
    public String imagen;

    // Constructor
    public Pelicula(String titulo, String anno, String genero, float valoracion, String sinopsis, boolean esFavorito, String imagen ) {
        this.titulo = titulo;
        this.anno = anno;
        this.genero = genero;
        this.valoracion = valoracion;
        this.sinopsis = sinopsis;
        this.esFavorito = esFavorito;
        this.imagen = imagen;
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

    public String getSinopsis() {
        return sinopsis;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isEsFavorito() {return esFavorito;}

    public String getImagen(String imagen) {return imagen;}

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

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public void setEsFavorito(boolean esFavorito) {this.esFavorito = esFavorito;}

    public void setImagen(String imagen) {this.imagen = imagen;}
}
