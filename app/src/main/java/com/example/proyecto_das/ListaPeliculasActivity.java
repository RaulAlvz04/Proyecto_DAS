package com.example.proyecto_das;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Pelicula;
import com.example.proyecto_das.db.PeliculaDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

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

        Toolbar toolbar = findViewById(R.id.laBarra);
        setSupportActionBar(toolbar);

        DrawerLayout elMenuDesplegable = findViewById(R.id.drawer_layout);
        NavigationView elNavigation = findViewById(R.id.nav_view);

        elNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    // Cargar todas las películas
                    lista.clear();
                    lista.addAll(peliDao.getPelisPorUsuario(idUsuarioLogueado));
                    adapter.notifyDataSetChanged();
                    getSupportActionBar().setTitle("Todas las Películas");
                }
                else if (id == R.id.nav_pendientes) {
                    // Cargar solo pendientes
                    List<Pelicula> pendientes = peliDao.getPendientesUsuario(idUsuarioLogueado);
                    lista.clear();
                    lista.addAll(pendientes);
                    adapter.notifyDataSetChanged();
                    getSupportActionBar().setTitle("Mis Pendientes");
                }
                else if (id == R.id.nav_ajustes) {
                    // Ir a la actividad de Ajustes
                    //Intent i = new Intent(ListaPeliculasActivity.this, AjustesActivity.class);
                    //startActivity(i);
                }

                elMenuDesplegable.closeDrawers();
                return true;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DrawerLayout elMenuDesplegable = findViewById(R.id.drawer_layout);
                if (elMenuDesplegable.isDrawerOpen(GravityCompat.START)) {
                    elMenuDesplegable.closeDrawer(GravityCompat.START);
                }
                else {
                    // Si el menú está cerrado pero la lista está filtrada, volvemos a mostrar todas
                    List<Pelicula> todas = peliDao.getPelisPorUsuario(idUsuarioLogueado);
                    if (lista.size() < todas.size()) {
                        lista.clear();
                        lista.addAll(todas);
                        adapter.notifyDataSetChanged();
                        getSupportActionBar().setTitle("Todas las Películas");
                    } else {
                        finish(); // Si no hay filtros, cerramos la actividad
                    }
                }
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        Pelicula nuevaPeli = new Pelicula(titulo,anno,genero,valoracion,null, false, null, true, idUsuarioLogueado);
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

    protected void onResume() {
        super.onResume();

        List <Pelicula> listaActualizada = peliDao.getPelisPorUsuario(idUsuarioLogueado);

        lista.clear();
        lista.addAll(listaActualizada);

        adapter.notifyDataSetChanged();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        final DrawerLayout elMenuDesplegable = findViewById(R.id.drawer_layout);

        if (id == android.R.id.home) {
            elMenuDesplegable.openDrawer(GravityCompat.START);
            return true;
        }
        else if (id == R.id.accion_favoritos) {
            List<Pelicula> favoritos = peliDao.getFavoritosUsuario(idUsuarioLogueado);

            lista.clear();
            lista.addAll(favoritos);

            adapter.notifyDataSetChanged();

            Toast.makeText(this, "Mostrando favoritos", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.accion_ayuda) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ayuda");
            builder.setMessage("Para añadir una película usa el botón +, para eliminar mantén pulsado sobre ella.");
            builder.setPositiveButton("Entendido", null);
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}