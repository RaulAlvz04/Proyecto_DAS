package com.example.proyecto_das;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Pelicula;
import com.example.proyecto_das.db.PeliculaDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListaPeliculasActivity extends AppCompatActivity implements DialogAnnadirPeli.ListenerDAP, DialogBorrarPeli.ListenerDBP {

    private PeliculaAdapter adapter;
    private List<Pelicula> lista;
    private PeliculaDAO peliDao;

    private int idUsuarioLogueado;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_peliculas);

        idUsuarioLogueado = getIntent().getIntExtra("ID_USUARIO", -1);

        RecyclerView rv = findViewById(R.id.miRecyView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "cine-db")
                .allowMainThreadQueries()
                .build();

        peliDao = db.peliculaDao();
        lista = peliDao.getPelisPorUsuario(idUsuarioLogueado);

        adapter = new PeliculaAdapter(lista);
        rv.setAdapter(adapter);

        FloatingActionButton btnAnnadir = findViewById(R.id.btnAdd);
        btnAnnadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogoAnnadir = new DialogAnnadirPeli();
                dialogoAnnadir.show(getSupportFragmentManager(), "dialogoAnnadir");
            }
        });

    }

    @Override
    public void alPulsarAnnadir(String titulo, String genero, String anno, float valoracion) {

        Pelicula nuevaPeli = new Pelicula(titulo,anno,genero,valoracion,"Sinopsis...", false, null, true, idUsuarioLogueado);
        peliDao.insert(nuevaPeli);

        lista.clear();
        lista.addAll(peliDao.getPelisPorUsuario(idUsuarioLogueado));
        adapter.notifyDataSetChanged();

    }

    @Override
    public void alConfirmarBorrado(int posicion) {

        Pelicula peliABorrar = lista.get(posicion);

        peliDao.delete(peliABorrar);

        lista.remove(posicion);
        adapter.notifyItemRemoved(posicion);

        Toast.makeText(this, "Película eliminada", Toast.LENGTH_SHORT).show();
    }

    public void ponerFavorito(int posicion) {

        Pelicula peli = lista.get(posicion);

        peli.setEsFavorito(!peli.esFavorito);

        peliDao.update(peli);

        adapter.notifyItemChanged(posicion);
    }
}