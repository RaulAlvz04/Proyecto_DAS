package com.example.proyecto_das;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_das.db.Pelicula;

import java.util.List;

public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaViewHolder> {

    private List<Pelicula> listaPeliculas;

    public PeliculaAdapter (List<Pelicula> plistaPeliculas){
        this.listaPeliculas = plistaPeliculas;
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula,null);
        return new PeliculaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula peli = listaPeliculas.get(position);
        holder.titulo.setText(peli.titulo);
        holder.valoracion.setRating(peli.valoracion);
        holder.genero.setText(peli.genero);
    }

    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }
}
