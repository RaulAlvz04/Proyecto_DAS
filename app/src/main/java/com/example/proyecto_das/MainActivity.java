package com.example.proyecto_das;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.proyecto_das.db.AppDatabase;
import com.example.proyecto_das.db.Usuario;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarConfiguracion();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // PEDIR PERMISO PARA ENVIAR NOTIFICACIONES
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new
                    String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
        }

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cine-db").allowMainThreadQueries().build();

        //1234
        //prueba@gmail.com

        etEmail = findViewById(R.id.emailText);
        etPassword = findViewById(R.id.passText);
        Button btnEntrar = findViewById(R.id.botonAcceso);
        Button btnRegistro = findViewById(R.id.btnRegistrarse);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString();
                String pass = etPassword.getText().toString();

                if (email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this, R.string.rellenaCampos, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Usuario user = db.usuarioDAO().login(email,pass);
                    if (user != null){

                        // Resetear el flag
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        prefs.edit().putBoolean("notif_enviada_" + user.id, false).apply();
                        Intent intent = new Intent(MainActivity.this, ListaPeliculasActivity.class);
                        intent.putExtra("ID_USUARIO", user.id);
                        intent.putExtra("EMAIL_USUARIO", user.email);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, R.string.loginMal, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    private void aplicarConfiguracion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = prefs.getString("idioma_key", "es"); // "es" por defecto

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

}