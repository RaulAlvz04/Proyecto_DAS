package com.example.proyecto_das;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    Toast.makeText(MainActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Usuario user = db.usuarioDAO().login(email,pass);
                    if (user != null){
                        Intent intent = new Intent(MainActivity.this, ListaPeliculasActivity.class);
                        intent.putExtra("ID_USUARIO", user.id);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
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
}