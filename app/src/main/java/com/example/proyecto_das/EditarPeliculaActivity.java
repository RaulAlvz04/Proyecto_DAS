package com.example.proyecto_das;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Pelicula;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EditarPeliculaActivity extends AppCompatActivity {

    EditText etTitulo, etGenero, etOpinion;
    TextView tvCambiarImagen;
    ImageView imagenPeli;
    RatingBar rbValoracion;
    Button btnGuardar, btnCancelar;
    CheckBox cbPendiente;
    AppDatabase db;
    Pelicula peliculaActual;
    int idPeli;

    // Launcher para manejar el resultado de abrir la galeria
    private ActivityResultLauncher<Intent> launcherGaleria = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null){
                        // Al seleccionar una imagen de la galería, la copiamos a la aplicación.
                        String ruta = copiarImagen(uri);
                        if (ruta != null){
                            imagenPeli.setImageURI(Uri.fromFile(new File(ruta)));
                            peliculaActual.setImagen(ruta); // Guardamos la ruta interna
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_pelicula);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "cine-db")
                .allowMainThreadQueries()
                .build();

        etTitulo = findViewById(R.id.editTitulo);
        etGenero = findViewById(R.id.editGenero);
        etOpinion = findViewById(R.id.editOpinion);
        rbValoracion = findViewById(R.id.ratingEditar);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
        btnCancelar = findViewById(R.id.btnCancelar);
        tvCambiarImagen = findViewById(R.id.tvCambiarImagen);
        imagenPeli = findViewById(R.id.imgPeli);
        cbPendiente = findViewById(R.id.checkEditarPendiente);

        // Recuperamos el idPeli para saber los datos de que pelicula tenemos que mostrar.
        idPeli = getIntent().getIntExtra("ID_PELICULA", -1);

        if (idPeli != -1){
            // Rellenamos los datos de la película
            peliculaActual = db.peliculaDao().getPeliPorId(idPeli);
            etTitulo.setText(peliculaActual.getTitulo());
            etGenero.setText(peliculaActual.getGenero());
            etOpinion.setText(peliculaActual.getOpinion());
            rbValoracion.setRating(peliculaActual.getValoracion());
            cbPendiente.setChecked(peliculaActual.isEsPendiente());

            if (peliculaActual.getImagen() != null && !peliculaActual.getImagen().isEmpty()) {
                File imagen = new File(peliculaActual.getImagen());
                if (imagen.exists()) {
                    imagenPeli.setImageURI(Uri.fromFile(imagen));
                }
            }
        }

        tvCambiarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPermisosYGaleria();
            }
        });

        // Al guardar los datos, los actualizamos en la vista.
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peliculaActual.setTitulo(etTitulo.getText().toString());
                peliculaActual.setGenero(etGenero.getText().toString());
                peliculaActual.setOpinion(etOpinion.getText().toString());
                peliculaActual.setValoracion((int) rbValoracion.getRating());
                peliculaActual.setEsPendiente(cbPendiente.isChecked());

                db.peliculaDao().update(peliculaActual);
                finish();
            }
        });

        // Volvemos a DetallePeliculaActivity
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    // Aplicamos un permiso u otro depende de la versión de Android
    private void verificarPermisosYGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            }
        } else {
            // Versiones anteriores
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcherGaleria.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        } else {
            Toast.makeText(this, getString(R.string.msg_permiso_denegado), Toast.LENGTH_SHORT).show();
        }
    }

    // Copiamos la imagen desde la galeria al almacenamiento interno de la aplicación.
    private String copiarImagen(Uri uri){
        try {
            // Borramos si hay alguna imagen antigua para ahorrar espacio
            if (peliculaActual.getImagen() != null) {
                File imagenVieja = new File(peliculaActual.getImagen());
                if (imagenVieja.exists()) { imagenVieja.delete(); }
            }

            String nombreImagen = "img_" + System.currentTimeMillis() + ".jpg";

            File archivoDestino = new File(getFilesDir(), nombreImagen);

            InputStream is = getContentResolver().openInputStream(uri);
            OutputStream os = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            is.close();
            os.close();

            return archivoDestino.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
}