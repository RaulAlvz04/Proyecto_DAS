package com.example.proyecto_das;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.File;

public class DetallePeliculaActivity extends AppCompatActivity {

    TextView tvTitulo, tvGenero, tvOpinion, tvPendiente;
    RatingBar rbValoracion;
    ImageView ivDetalle;
    int idPeli;
    Button btnEditar, btnCompartir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pelicula);

        tvTitulo = findViewById(R.id.txtDetalleTitulo);
        tvGenero = findViewById(R.id.txtDetalleGenero);
        tvOpinion = findViewById(R.id.txtDetalleOpinion);
        rbValoracion = findViewById(R.id.ratingDetalle);
        ivDetalle = findViewById(R.id.imgDetalle);
        btnEditar = findViewById(R.id.btnEditar);
        btnCompartir = findViewById(R.id.btnCompartir);
        tvPendiente = findViewById(R.id.txtDetallePendiente);

        // Recuperamos el idPeli para saber los datos de que pelicula tenemos que mostrar.
        idPeli = getIntent().getIntExtra("ID_PELICULA", -1);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallePeliculaActivity.this, EditarPeliculaActivity.class);
                intent.putExtra("ID_PELICULA", idPeli);
                startActivity(intent);
            }
        });

        // Intent implicito para compartir la opinión por mensaje
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = getString(R.string.msg_compartir_1) + tvTitulo.getText().toString() +
                        getString(R.string.msg_compartir_2) + tvOpinion.getText().toString();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
                sendIntent.setType("text/plain");

                // Esto muestra el selector de aplicaciones del sistema
                Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.titulo_chooser));
                startActivity(shareIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idPeli != -1){
            cargarDetallesDesdeServidor(); // Obtenemos los datos de la peli desde el servidor
        }
    }

    private void cargarDetallesDesdeServidor() {
        new Thread(() -> {
            try {
                // Usamos el siguiente endpoint para buscar la peli por el id
                URL url = new URL("http://34.136.199.32:81/peliculas.php?accion=por_id&idPeli=" + idPeli);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder out = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) out.append(line);
                    in.close();

                    JSONObject obj = new JSONObject(out.toString());

                    // Actualizamos la UI en el hilo principal
                    runOnUiThread(() -> {
                        tvTitulo.setText(obj.optString("titulo"));
                        tvGenero.setText(obj.optString("genero"));
                        tvOpinion.setText(obj.optString("opinion", ""));
                        rbValoracion.setRating((float) obj.optDouble("valoracion", 0));

                        if (obj.optInt("esPendiente") == 1) {
                            tvPendiente.setVisibility(View.VISIBLE);
                        } else {
                            tvPendiente.setVisibility(View.GONE);
                        }

                        // Para la imagen, si es una URL o ruta:
                        String rutaImagen = obj.optString("imagen");
                        if (!rutaImagen.isEmpty()) {
                            ivDetalle.setImageURI(Uri.parse(rutaImagen));
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar detalle", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


}