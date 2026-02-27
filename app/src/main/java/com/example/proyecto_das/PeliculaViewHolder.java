package com.example.proyecto_das;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class PeliculaViewHolder extends RecyclerView.ViewHolder {
    public TextView titulo;
    public TextView genero;
    public RatingBar valoracion;
    public ImageView imagen;

    public ImageView favorito;

    public PeliculaViewHolder(@NonNull View itemView) {
        super(itemView);
        titulo = itemView.findViewById(R.id.txtTitulo);
        genero = itemView.findViewById(R.id.txtGenero);
        valoracion = itemView.findViewById(R.id.ratingPeli);
        imagen = itemView.findViewById(R.id.imgPortada);
        favorito = itemView.findViewById(R.id.imgFavorito);

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int posicion = getAdapterPosition();

                if (v.getContext() instanceof AppCompatActivity) {
                    FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                    DialogBorrarPeli dialogo = new DialogBorrarPeli(posicion);
                    dialogo.show(fragmentManager, "tag_borrar");
                }

                return true;
            }
        });

        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicion = getAdapterPosition();

                if (v.getContext() instanceof ListaPeliculasActivity) {
                    ListaPeliculasActivity activity = (ListaPeliculasActivity) v.getContext();
                    activity.ponerFavorito(posicion);
                }
            }
        });
    }
}
