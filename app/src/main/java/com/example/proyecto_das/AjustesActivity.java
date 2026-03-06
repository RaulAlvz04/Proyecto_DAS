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
        aplicarConfiguracion();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        Toolbar toolbar = findViewById(R.id.laBarra3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.ajustes);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new Preferencias())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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