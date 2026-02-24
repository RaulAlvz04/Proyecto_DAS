package com.example.proyecto_das;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Pelicula;
import com.example.proyecto_das.db.PeliculaDAO;

import java.util.List;

public class ListaPeliculasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_peliculas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView rv = findViewById(R.id.miRecyView);
        rv.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "cine-db")
                .allowMainThreadQueries() // Importante para que no te de error de hilos hoy
                .build();

        PeliculaDAO peliDao = db.peliculaDao();
        List<Pelicula> lista = peliDao.getAll();

        if (lista.isEmpty()) {
            Pelicula prueba = new Pelicula("Batman", "2022", "Acción", 4.5f, "El caballero oscuro...");
            peliDao.insert(prueba);
            lista = peliDao.getAll(); // Volvemos a cargar la lista
        }

        PeliculaAdapter adapter = new PeliculaAdapter(lista);
        rv.setAdapter(adapter);

    }
}