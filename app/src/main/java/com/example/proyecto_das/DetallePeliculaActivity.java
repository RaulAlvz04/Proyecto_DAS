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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Pelicula;

import java.io.File;

public class DetallePeliculaActivity extends AppCompatActivity {

    TextView tvTitulo, tvGenero, tvOpinion;
    RatingBar rbValoracion;
    ImageView ivDetalle;
    int idPeli;
    Button btnEditar, btnCompartir;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pelicula);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "cine-db")
                .allowMainThreadQueries()
                .build();

        tvTitulo = findViewById(R.id.txtDetalleTitulo);
        tvGenero = findViewById(R.id.txtDetalleGenero);
        tvOpinion = findViewById(R.id.txtDetalleOpinion);
        rbValoracion = findViewById(R.id.ratingDetalle);
        ivDetalle = findViewById(R.id.imgDetalle);
        btnEditar = findViewById(R.id.btnEditar);
        btnCompartir = findViewById(R.id.btnCompartir);

        idPeli = getIntent().getIntExtra("ID_PELICULA", -1);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetallePeliculaActivity.this, EditarPeliculaActivity.class);
                intent.putExtra("ID_PELICULA", idPeli);
                startActivity(intent);
            }
        });

        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = "He visto esta película: " + tvTitulo.getText().toString() +
                        "\nEsto es lo que opino de ella: " + tvOpinion.getText().toString();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
                sendIntent.setType("text/plain");

                // Esto muestra el selector de aplicaciones del sistema
                Intent shareIntent = Intent.createChooser(sendIntent, "Compartir peli con...");
                startActivity(shareIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Pelicula peli = db.peliculaDao().getPeliPorId(idPeli);
        tvTitulo.setText(peli.getTitulo());
        tvGenero.setText(peli.getGenero());
        tvOpinion.setText(peli.getOpinion());
        rbValoracion.setRating(peli.getValoracion());

        if (peli.getImagen() != null && !peli.getImagen().isEmpty()) {
            File imagen = new File(peli.getImagen());
            if (imagen.exists()) {
                ivDetalle.setImageURI(Uri.fromFile(imagen));
            }
        } else {
            ivDetalle.setImageResource(android.R.drawable.ic_menu_gallery);
        }

    }
}