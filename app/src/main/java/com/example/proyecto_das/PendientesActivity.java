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

import java.util.List;

public class PendientesActivity extends AppCompatActivity {

    private PeliculaAdapter adapter;
    private List<Pelicula> lista;
    private PeliculaDAO peliDao;

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

        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "cine-db").allowMainThreadQueries().build();
        List<Pelicula> listaPendientes = db.peliculaDao().getPendientesUsuario(idUsuarioLogueado);

        RecyclerView rv = findViewById(R.id.rvPendientes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new PeliculaAdapter(listaPendientes));

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