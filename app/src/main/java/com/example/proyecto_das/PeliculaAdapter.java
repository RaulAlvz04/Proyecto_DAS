package com.example.proyecto_das;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_das.db.Pelicula;

import java.io.File;
import java.util.List;

public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaViewHolder> {

    private List<Pelicula> listaPeliculas;

    public PeliculaAdapter (List<Pelicula> plistaPeliculas){
        this.listaPeliculas = plistaPeliculas;
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent ,false);
        return new PeliculaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula peli = listaPeliculas.get(position);
        holder.titulo.setText(peli.titulo);
        holder.valoracion.setRating(peli.valoracion);
        holder.genero.setText(peli.genero);
        holder.idPeliculaActual = peli.getId();

        if (peli.isEsPendiente()) {
            holder.pendiente.setVisibility(View.VISIBLE);
        } else {
            holder.pendiente.setVisibility(View.GONE);
        }

        if (peli.getImagen() != null && !peli.getImagen().isEmpty()) {
            File imagen = new File(peli.getImagen());
            if (imagen.exists()) {
                holder.imagen.setImageURI(Uri.fromFile(imagen));
            } else {
                // Si el archivo no existe, ponemos uno por defecto
                holder.imagen.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        if (peli.esFavorito) {
            holder.favorito.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            holder.favorito.setImageResource(android.R.drawable.btn_star_big_off);
        }

    }

    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }
}
