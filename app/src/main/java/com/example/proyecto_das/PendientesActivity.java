package com.example.proyecto_das;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Pelicula;
import com.example.proyecto_das.db.PeliculaDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PendientesActivity extends AppCompatActivity {

    private PeliculaAdapter adapter;
    private List<Pelicula> lista = new ArrayList<>();

    private int idUsuarioLogueado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pendientes);

        Toolbar toolbar = findViewById(R.id.laBarra2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.mis_pendientes);

        idUsuarioLogueado = getIntent().getIntExtra("ID_USUARIO",-1);

        if (lista == null) {
            lista = new ArrayList<>();
        }

        RecyclerView rv = findViewById(R.id.rvPendientes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PeliculaAdapter(lista);
        rv.setAdapter(adapter);

        cargarPendientesServidor();

    }

    private void cargarPendientesServidor() {
        new Thread(() -> {
            try {
                // Usamos la acción 'pendientes' para obtener la lista de pelis pendientes de un usuario concreto
                URL url = new URL("http://34.175.247.221:81/peliculas.php?accion=pendientes&idUsuario=" + idUsuarioLogueado);
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

                    // Actualizar la interfaz en el hilo principal
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}