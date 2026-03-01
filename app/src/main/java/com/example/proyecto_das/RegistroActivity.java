package com.example.proyecto_das;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
                    Toast.makeText(RegistroActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                Usuario usuarioExistente = db.usuarioDAO().buscarPorEmail(email);

                if (usuarioExistente != null){
                    Toast.makeText(RegistroActivity.this, "Ya existe un usuario con ese email",Toast.LENGTH_SHORT).show();
                }
                else {
                    Usuario nuevoUsuario = new Usuario(pass,email);
                    db.usuarioDAO().insert(nuevoUsuario);
                    Toast.makeText(RegistroActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}