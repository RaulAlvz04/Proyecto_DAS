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

public class RegistroActivity extends AppCompatActivity {

    EditText regEmail, regPass;
    Button btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "cine-db")
                .allowMainThreadQueries()
                .build();

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

                Usuario usuarioExistente = db.usuarioDAO().buscarPorEmail(email);

                if (usuarioExistente != null){
                    Toast.makeText(RegistroActivity.this, R.string.existeUsuario,Toast.LENGTH_SHORT).show();
                }
                else {
                    Usuario nuevoUsuario = new Usuario(pass,email);
                    db.usuarioDAO().insert(nuevoUsuario);

                    enviarNotificacion();

                    Toast.makeText(RegistroActivity.this, R.string.usuario_creado, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

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