package com.example.proyecto_das;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistroActivity extends AppCompatActivity {

    EditText regEmail, regPass;
    Button btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        
        regEmail = findViewById(R.id.regEmail);
        regPass = findViewById(R.id.regPass);

        btnRegistro = findViewById(R.id.btnRegistrar);

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmail.getText().toString().trim();
                String pass = regPass.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, R.string.rellenaCampos, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                registrarUsuario(email,pass);
            }
        });
    }

    private void registrarUsuario(String email, String pass) {
        // Hilo secundario para conectarse al servidor remoto
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Configurar conexión para registrarnos
                    URL url = new URL("http://34.175.247.221:81/registro.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Parámetros para el PHP
                    String parametros = "email=" + email + "&password=" + pass;

                    OutputStream os = conn.getOutputStream();
                    os.write(parametros.getBytes());
                    os.flush();
                    os.close();

                    // Leer respuesta
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder respuesta = new StringBuilder();
                        String linea;
                        while ((linea = in.readLine()) != null) {
                            respuesta.append(linea);
                        }
                        in.close();

                        // Procesamos JSON
                        JSONObject json = new JSONObject(respuesta.toString());
                        String status = json.getString("status");

                        // Volver al hilo principal para actualizar la pantalla
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (status.equals("ok")) {
                                        enviarNotificacion();
                                        Toast.makeText(RegistroActivity.this, R.string.usuario_creado, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        // Muestra el mensaje de "Ese email ya existe" que manda el PHP
                                        String msg = json.getString("message");
                                        Toast.makeText(RegistroActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(RegistroActivity.this, "Error de red", Toast.LENGTH_SHORT).show());
                }
            }
        }).start();
    }

    // Enviamos notificación al registrarnos correctamente
    private void enviarNotificacion() {

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Canal01");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel("Canal01", "CanalBienvenida",
                    NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(canal);
        }

        builder.setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(getString(R.string.bienvenida))
                .setContentText(getString(R.string.mensajeBienvenida))
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setAutoCancel(true);

        manager.notify(1, builder.build());

    }
}