package com.example.proyecto_das;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Pelicula;
import com.example.proyecto_das.db.PeliculaDAO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Locale;

public class ListaPeliculasActivity extends AppCompatActivity implements DialogAnnadirPeli.ListenerDAP, DialogBorrarPeli.ListenerDBP {

    private PeliculaAdapter adapter;
    private List<Pelicula> lista;
    private PeliculaDAO peliDao;

    private int idUsuarioLogueado;
    private String emailUsuario;
    private String idiomaActual;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_peliculas);

        // Recogemos el email para mostrarlo en el NavigationDrawer
        emailUsuario = getIntent().getStringExtra("EMAIL_USUARIO");

        Toolbar toolbar = findViewById(R.id.laBarra);
        setSupportActionBar(toolbar);

        DrawerLayout elMenuDesplegable = findViewById(R.id.drawer_layout);
        NavigationView elNavigation = findViewById(R.id.nav_view);
        View headerView = elNavigation.getHeaderView(0);

        TextView tvEmail = headerView.findViewById(R.id.tvUserEmail);
        tvEmail.setText(emailUsuario);

        // Se hace una acción distinta dependiendo de la opción que seleccionemos
        elNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    // Cargar todas las películas
                    lista.clear();
                    lista.addAll(peliDao.getPelisPorUsuario(idUsuarioLogueado));
                    adapter.notifyDataSetChanged();
                    getSupportActionBar().setTitle(R.string.titulo_lista);
                }
                else if (id == R.id.nav_pendientes) {
                    // Abrimos PendientesActivity para mostrar las peliculas pendientes
                    Intent intent = new Intent(ListaPeliculasActivity.this, PendientesActivity.class);
                    intent.putExtra("ID_USUARIO",idUsuarioLogueado);
                    startActivity(intent);
                }
                else if (id == R.id.nav_ajustes) {
                    // Ir a la actividad de Ajustes
                    Intent i = new Intent(ListaPeliculasActivity.this, AjustesActivity.class);
                    startActivity(i);
                }

                elMenuDesplegable.closeDrawers();
                return true;
            }
        });

        // Al darle al botón de atrás, cerrar el menú lateral si está abierto o limpiar filtros
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
                        getSupportActionBar().setTitle(R.string.titulo_lista);
                    } else {
                        finish(); // Si no hay filtros, cerramos la actividad
                    }
                }
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.titulo_lista);

        idUsuarioLogueado = getIntent().getIntExtra("ID_USUARIO", -1);

        // Configurar RecyclerView para mostrar películas.
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

        // Guardamos el idioma actual para detectar cambios en onResume
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        idiomaActual = prefs.getString("idioma_key", "es");

    }

    @Override
    public void alPulsarAnnadir(String titulo, String genero, String anno, float valoracion, boolean esPendiente) {

        // Creamos nueva pelicula
        Pelicula nuevaPeli = new Pelicula(titulo,anno,genero,valoracion,null, false, null, esPendiente, idUsuarioLogueado);
        peliDao.insert(nuevaPeli);

        // Recargamos la lista
        lista.clear();
        lista.addAll(peliDao.getPelisPorUsuario(idUsuarioLogueado));
        adapter.notifyDataSetChanged();

    }

    @Override
    public void alConfirmarBorrado(int posicion) {

        // Borramos la pelicula
        Pelicula peliABorrar = lista.get(posicion);

        peliDao.delete(peliABorrar);

        lista.remove(posicion);
        adapter.notifyItemRemoved(posicion);

        Toast.makeText(this, R.string.msg_borrar, Toast.LENGTH_SHORT).show();
    }

    public void ponerFavorito(int posicion) {

        Pelicula peli = lista.get(posicion);

        peli.setEsFavorito(!peli.esFavorito);

        peliDao.update(peli);

        adapter.notifyItemChanged(posicion);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String langPref = prefs.getString("idioma_key", "es");


        if (idiomaActual != null && !idiomaActual.equals(langPref)) {
            // Si el idioma cambia reiniciamos
            Intent intent = new Intent(this, ListaPeliculasActivity.class);
            intent.putExtra("ID_USUARIO", idUsuarioLogueado);
            intent.putExtra("EMAIL_USUARIO", emailUsuario);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
            return;
        }

        idiomaActual = langPref; // Guardar idioma actual

        List<Pelicula> listaActualizada = peliDao.getPelisPorUsuario(idUsuarioLogueado);
        lista.clear();
        lista.addAll(listaActualizada);
        adapter.notifyDataSetChanged();

        // Verificamos si ya se ha mandado la notificación de recordatorio
        boolean notifEnviada = prefs.getBoolean("notif_enviada_" + idUsuarioLogueado, false);
        if (!notifEnviada) {
            List<Pelicula> pendientes = peliDao.getPendientesUsuario(idUsuarioLogueado);
            if (!pendientes.isEmpty()) {
                enviarNotificacion();
            }
            prefs.edit().putBoolean("notif_enviada_" + idUsuarioLogueado, true).apply();
        }
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
            return true;

        } else if (id == R.id.accion_ayuda) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogDAS);
            builder.setTitle(R.string.ayuda);
            builder.setMessage(R.string.mensajeAyuda);
            builder.setPositiveButton(R.string.entendido, null);
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Añade el idioma seleccionado en el contexto de la actividad antes de que se cree.
    @Override
    protected void attachBaseContext(android.content.Context newBase) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(newBase);
        String lang = prefs.getString("idioma_key", "es");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        // Heredar config base del sistema
        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.setLocale(locale);

        android.content.Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    private void enviarNotificacion() {

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Canal02");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel("Canal02", "CanalRecordatorio",
                    NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(canal);
        }

        builder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(getString(R.string.recordatorio))
                .setContentText(getString(R.string.mensajeRecuerdo))
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        manager.notify(1, builder.build());

    }
}


