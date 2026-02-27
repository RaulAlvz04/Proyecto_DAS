package com.example.proyecto_das;

import android.net.Uri;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula, parent ,false);
        return new PeliculaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula peli = listaPeliculas.get(position);
        holder.titulo.setText(peli.titulo);
        holder.valoracion.setRating(peli.valoracion);
        holder.genero.setText(peli.genero);

        if (peli.imagen !=null && !peli.imagen.isEmpty()){
            holder.imagen.setImageURI(Uri.parse(peli.imagen));
        } else {
            holder.imagen.setImageResource(android.R.drawable.ic_menu_gallery);
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
