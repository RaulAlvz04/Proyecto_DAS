package com.example.proyecto_das;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import com.example.proyecto_das.db.Pelicula;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListaPeliculasActivity extends AppCompatActivity implements DialogAnnadirPeli.ListenerDAP, DialogBorrarPeli.ListenerDBP {

    private PeliculaAdapter adapter;
    private List<Pelicula> lista = new ArrayList<>();

    private int idUsuarioLogueado;
    private String emailUsuario;
    private String idiomaActual;

    private ImageView ivPerfilHeader;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_peliculas);

        // Recogemos el email para mostrarlo en el NavigationDrawer
        emailUsuario = getIntent().getStringExtra("EMAIL_USUARIO");
        idUsuarioLogueado = getIntent().getIntExtra("ID_USUARIO", -1);

        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Obtener la miniatura (Bitmap)
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        // Mostrar la imagen en el ImageView del header
                        if (ivPerfilHeader != null) {
                            ivPerfilHeader.setImageBitmap(imageBitmap);
                            subirFotoPerfil(imageBitmap);
                        }
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.laBarra);
        setSupportActionBar(toolbar);

        DrawerLayout elMenuDesplegable = findViewById(R.id.drawer_layout);
        NavigationView elNavigation = findViewById(R.id.nav_view);
        View headerView = elNavigation.getHeaderView(0);

        TextView tvEmail = headerView.findViewById(R.id.tvUserEmail);
        tvEmail.setText(emailUsuario);

        ivPerfilHeader = headerView.findViewById(R.id.imgPerfilHeader);
        Button btnCamara = headerView.findViewById(R.id.btnCamaraHeader);

        descargarFotoPerfil();

        btnCamara.setOnClickListener(v -> {
            // Comprobar permisos para sacer foto
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 102);
                } else {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureLauncher.launch(takePictureIntent);
                }
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureLauncher.launch(takePictureIntent);
            }
        });

        // Se hace una acción distinta dependiendo de la opción que seleccionemos
        elNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    // Cargar todas las películas
                    cargarPelisDesdeServidor("por_usuario");
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
                else if (id == R.id.nav_mapa) {
                    Intent i = new Intent(ListaPeliculasActivity.this, MapaActivity.class);
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
                    if (!getSupportActionBar().getTitle().equals(getString(R.string.titulo_lista))) {
                        cargarPelisDesdeServidor("por_usuario");
                        getSupportActionBar().setTitle(R.string.titulo_lista);
                    } else {
                        finish(); // Si ya estamos en la lista principal, cerramos
                    }
                }
            }
        });

        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_sort_by_size);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.titulo_lista);

        // Configurar RecyclerView para mostrar películas.
        RecyclerView rv = findViewById(R.id.miRecyView);
        rv.setLayoutManager(new LinearLayoutManager(this));

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

    private void cargarPelisDesdeServidor(String accion) {
        new Thread(() -> {
            try {
                // Usamos este endpoint para obtener todas la películas de un usuario o solo las favoritas desde el servidor
                URL url = new URL("http://34.175.144.158:81/peliculas.php?accion=" + accion + "&idUsuario=" + idUsuarioLogueado);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder out = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) out.append(line);
                    in.close();

                    JSONArray array = new JSONArray(out.toString());
                    lista.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Pelicula p = new Pelicula(
                                obj.getString("titulo"),
                                obj.getString("anno"),
                                obj.getString("genero"),
                                (float) obj.getDouble("valoracion"),
                                obj.optString("opinion", ""),
                                obj.getInt("esFavorito") == 1,
                                obj.optString("imagen", ""),
                                obj.getInt("esPendiente") == 1,
                                obj.getInt("idUsuario")
                        );
                        p.setId(obj.getInt("id"));
                        lista.add(p);
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    boolean notifEnviada = prefs.getBoolean("notif_enviada_" + idUsuarioLogueado, false);

                    // Enviamos notificación si hay alguna pendiente, no se envia si ya se ha enviado antes en esta sesión
                    if (!notifEnviada) {
                        for (Pelicula p : lista) {
                            if (p.isEsPendiente()) {
                                enviarNotificacion();
                                prefs.edit().putBoolean("notif_enviada_" + idUsuarioLogueado, true).apply();
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    @Override
    public void alPulsarAnnadir(String titulo, String genero, String anno, float valoracion, boolean esPendiente) {

        new Thread(() -> {
            try {
                // Usamos el siguiente endpoint para añadir la peli a la base de datos remota
                URL url = new URL("http://34.175.144.158:81/peliculas.php?accion=insertar");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String params = "titulo=" + titulo + "&genero=" + genero + "&anno=" + anno +
                        "&valoracion=" + valoracion + "&idUsuario=" + idUsuarioLogueado +
                        "&esPendiente=" + (esPendiente ? 1 : 0);

                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes());
                os.flush();
                os.close();

                if (conn.getResponseCode() == 200) {
                    // Recargamos para mostrar la nueva peli añadida
                    cargarPelisDesdeServidor("por_usuario");
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

    }

    @Override
    public void alConfirmarBorrado(int posicion) {

        // Borramos la pelicula
        Pelicula peliABorrar = lista.get(posicion);

        new Thread(() -> {
            try {
                // Usamos este endpoint para eliminar la peli y borrarla de la base de datos remota
                URL url = new URL("http://34.175.144.158:81/peliculas.php?accion=eliminar");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String params = "idPeli=" + peliABorrar.getId();

                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes());
                os.flush(); os.close();

                if (conn.getResponseCode() == 200) {
                    runOnUiThread(() -> {
                        lista.remove(posicion);
                        adapter.notifyItemRemoved(posicion);
                        // Mostramos mensaje avisando de que la acción es irreversible
                        Toast.makeText(this, R.string.msg_borrar, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public void ponerFavorito(int posicion) {

        Pelicula peli = lista.get(posicion);
        boolean nuevoEstado = !peli.isEsFavorito();

        new Thread(() -> {
            try {
                URL url = new URL("http://34.175.144.158:81/peliculas.php?accion=actualizar");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String params = "id=" + peli.getId() + "&titulo=" + peli.getTitulo() + "&anno=" + peli.getAnno() +
                        "&genero=" + peli.getGenero() + "&valoracion=" + peli.getValoracion() +
                        "&opinion=" + peli.getOpinion() + "&esFavorito=" + (nuevoEstado ? 1 : 0) +
                        "&esPendiente=" + (peli.isEsPendiente() ? 1 : 0) + "&imagen=" + peli.getImagen();

                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes());
                os.flush(); os.close();

                if (conn.getResponseCode() == 200) {
                    runOnUiThread(() -> {
                        peli.setEsFavorito(nuevoEstado);
                        adapter.notifyItemChanged(posicion);
                    });
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
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

        // Recargamos todas la pelis por si hubiera habido algún cambio
        cargarPelisDesdeServidor("por_usuario");
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
            cargarPelisDesdeServidor("favoritos");
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, R.string.camaraPermiso, Toast.LENGTH_SHORT).show();
        }
    }

    private void subirFotoPerfil(Bitmap bitmap) {
        new Thread(() -> {
            try {
                // Convertir Bitmap a Base64
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream); // 80% calidad para no saturar
                byte[] fototransformada = stream.toByteArray();
                String fotoen64 = Base64.encodeToString(fototransformada, Base64.DEFAULT);

                // Conectarnos al servidor
                URL url = new URL("http://34.175.144.158:81/subirImagenPerfil.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Enviamos la acción, el ID del usuario y el String de la imagen
                String params = "accion=subir_foto" +
                        "&idUsuario=" + idUsuarioLogueado +
                        "&imagen=" + URLEncoder.encode(fotoen64, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(params.getBytes());
                os.flush();
                os.close();

                if (conn.getResponseCode() == 200) {
                    descargarFotoPerfil();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void descargarFotoPerfil() {
        new Thread(() -> {
            try {
                // La dirección apunta al archivo .jpg correspondiente a la foto de el usuario logueado
                String direccion = "http://34.175.144.158:81/imagenesPerfil/user_" + idUsuarioLogueado + ".jpg";
                URL destino = new URL(direccion);

                HttpURLConnection conn = (HttpURLConnection) destino.openConnection();
                conn.setUseCaches(false);
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Bitmap elBitmap = BitmapFactory.decodeStream(conn.getInputStream());

                    // Actualizamos la interfaz
                    runOnUiThread(() -> {
                        if (ivPerfilHeader != null) {
                            ivPerfilHeader.setImageBitmap(elBitmap);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}


