package com.example.proyecto_das;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class AjustesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicamos el idioma antes de nada
        aplicarConfiguracion();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        // Generamos la ToolBar con el titulo y un botón para ir hacia la anterior actividad
        Toolbar toolbar = findViewById(R.id.laBarra3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.ajustes);

        // Cargar el Fragment de Preferencias en el contenedor
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new Preferencias())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Cierra la actividad y vuelve a ListaPeliculasActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Leemos el idioma guardado en SharedPreferences para saber que idioma mostrar en la aplicación
    private void aplicarConfiguracion() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = prefs.getString("idioma_key", "es");

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}