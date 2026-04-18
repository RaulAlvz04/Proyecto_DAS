package com.example.proyecto_das;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
    Button btnEditar, btnCompartir, btnRecordatorio;

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

        Button btnAbrirDialogo = findViewById(R.id.btnAbrirDialogoAmigo);
        btnAbrirDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoAmigo(null, null);
            }
        });

        btnRecordatorio = findViewById(R.id.btnRecordatorio);
        btnRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetallePeliculaActivity.this);
                builder.setTitle("Recordatorio de sesión");
                builder.setMessage("¿Dentro de cuantos minutos quieres que se te avise?");

                EditText etMins = new EditText(DetallePeliculaActivity.this);
                etMins.setInputType(InputType.TYPE_CLASS_NUMBER);

                LinearLayout container = new LinearLayout(DetallePeliculaActivity.this);
                container.setPadding(60, 20, 60, 0);
                container.setOrientation(LinearLayout.VERTICAL);
                container.addView(etMins);

                builder.setView(container);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String valor = etMins.getText().toString();

                        if (!valor.isEmpty()){
                            int mins = Integer.parseInt(valor);
                            long minisecs = (long) mins * 60 * 1000;
                            long momentoAlarma = System.currentTimeMillis() + minisecs;

                            Intent intent = new Intent(DetallePeliculaActivity.this, AlarmReceiver.class);
                            intent.putExtra("TITULO_PELI", tvTitulo.getText().toString());

                            PendingIntent pIntent = PendingIntent.getBroadcast(DetallePeliculaActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                            AlarmManager gestor = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            if (gestor != null) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                                    if (gestor.canScheduleExactAlarms()) {
                                        gestor.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, momentoAlarma, pIntent);
                                        Toast.makeText(DetallePeliculaActivity.this, "Aviso programado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DetallePeliculaActivity.this,"No tienes permiso para alarmas exactas", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    gestor.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, momentoAlarma, pIntent);
                                    Toast.makeText(DetallePeliculaActivity.this, "Aviso programado", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                });

                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });

        // Cargar la lista de amigos al iniciar
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.WRITE_CONTACTS}, 1);
        } else {
            actualizarListaAmigos();
        }
    }

    private void mostrarDialogoAmigo(String nombre, String telefono) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (nombre == null) {
            builder.setTitle("Nuevo amigo");
        }
        else {
            builder.setTitle("Editar Teléfono");
        }

        // Layout del diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        EditText etNom = new EditText(this);
        etNom.setHint("Nombre");
        if (nombre != null) {
            etNom.setText(nombre.replace("Cine - ", ""));
            etNom.setEnabled(false); // Hacemos que no se pueda editar el nombre una vez puesto
        }

        EditText etTlf = new EditText(this);
        etTlf.setHint("Teléfono");
        etTlf.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
        if (telefono != null){
            etTlf.setText(telefono);
        }

        layout.addView(etNom);
        layout.addView(etTlf);
        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            if (nombre == null) {
                // Si el nombre es null significa que no se ha creado todavia, así que llamamos a la función para añadirlo
                añadirAmigoCine(etNom.getText().toString(), etTlf.getText().toString());
            } else {
                // Si no es null, llamamos a la función para que lo modifique.
                modificarAmigoCine(nombre, etTlf.getText().toString());
            }
            actualizarListaAmigos(); // Refrescar lista
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void actualizarListaAmigos() {
        // Limpiamos el contenedor para volver a pintarlo con los nuevos datos
        LinearLayout contenedor = findViewById(R.id.contenedorAmigos);
        contenedor.removeAllViews();


        Uri uriPhones = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] columnas = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor c = getContentResolver().query(
                uriPhones,
                columnas,
                ContactsContract.Data.DISPLAY_NAME + " LIKE ?",
                new String[]{"Cine - %"},
                null
        );

        if (c != null) {
            while (c.moveToNext()) {
                // Obtenemos por cada dato su nombre y el telefono
                String nombre = c.getString(0);
                String telefono = c.getString(1);

                // Creamos fila, añadiendole el nombre y el botón de borrar
                LinearLayout fila = new LinearLayout(this);

                TextView tv = new TextView(this);
                tv.setText("👤 " + nombre.replace("Cine - ", ""));
                tv.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1f));
                // Si se hace click en el nombre te deja editar el número de telefono por si lo has escirto mal
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarDialogoAmigo(nombre, telefono);
                    }
                });

                Button btnBorrar = new Button(this);
                btnBorrar.setText("Eliminar");
                // Si se hace click en Eliminar, se borra el dato y se actualiza la lista para no mostrarlo
                btnBorrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        eliminarAmigoCine(nombre);
                        actualizarListaAmigos();
                    }
                });

                fila.addView(tv);
                fila.addView(btnBorrar);
                contenedor.addView(fila);
            }
            c.close();
        }
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
                URL url = new URL("http://34.175.102.229:81/peliculas.php?accion=por_id&idPeli=" + idPeli);
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

    private void añadirAmigoCine(String nombre, String telefono) {

        ContentValues values = new ContentValues();
        Uri contactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long contactId = android.content.ContentUris.parseId(contactUri);

        ContentValues nameValues = new ContentValues();
        nameValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        nameValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        nameValues.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Cine - " + nombre);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, nameValues);

        ContentValues phoneValues = new ContentValues();
        phoneValues.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
        phoneValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, telefono);
        phoneValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(ContactsContract.Data.CONTENT_URI, phoneValues);
    }

    private void modificarAmigoCine(String nombre, String telefono){

        ContentValues cambios = new ContentValues();
        cambios.put(ContactsContract.CommonDataKinds.Phone.NUMBER, telefono);

        String cond = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ?";
        String[] args = { nombre, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE };

        getContentResolver().update(ContactsContract.Data.CONTENT_URI, cambios, cond, args);
    }

    private void eliminarAmigoCine(String nombre) {
        String cond = ContactsContract.Data.DISPLAY_NAME + " = ?";
        String[] args = { nombre };

        getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, cond, args);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Una vez que el usuario acepta el permiso, cargamos la lista de amigos
            actualizarListaAmigos();
        } else {
            Toast.makeText(this, "Permiso denegado para leer contactos", Toast.LENGTH_SHORT).show();
        }
    }
}