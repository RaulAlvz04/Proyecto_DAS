package com.example.proyecto_das;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PeliculaViewHolder extends RecyclerView.ViewHolder {
    public TextView titulo;
    public TextView genero;
    public RatingBar valoracion;

    public PeliculaViewHolder(@NonNull View itemView) {
        super(itemView);
        titulo = itemView.findViewById(R.id.txtTitulo);
        genero = itemView.findViewById(R.id.txtGenero);
        valoracion = itemView.findViewById(R.id.ratingPeli);
    }
}
