package com.example.reto1pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reto1pizzeria.viewmodel.UsuarioViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Declaración de los elementos de la interfaz de usuario

    private FirebaseAuth mAuth;
    private EditText editTextUser, editTextPassword;
    private Button buttonLogin,buttonRegister;

    private ImageView home;

    // Instancia de UsuarioViewModel
    private UsuarioViewModel usuarioViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Inicialización de Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        //Vinculación de las vistas con los objetos
        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        home = findViewById(R.id.headerImgHome);
        //Configuración del botón de Home para regresar a main
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Configuración del botón de registro para abrir la actividad de registro
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish(); // Finaliza esta actividad para que el usuario no pueda volver atrás
            }
        });

        //Configurar el boton Login para llamar metodo loginUser
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }
    //Metodo loginUser para iniciar Sesion

    private void loginUser(){
        String usuario = editTextUser.getText().toString().trim();
        String clave = editTextPassword.getText().toString().trim();

        //VAlidacion de campos
        if(usuario.isEmpty() || clave.isEmpty()){
            Toast.makeText(this,"Diligencie todos los campos",Toast.LENGTH_SHORT).show();
            return;
        }

        //Iniciar Sesión con Firebase Authentication
        mAuth.signInWithEmailAndPassword(usuario, clave)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso, mostrar mensaje de éxito y redirigir a la actividad principal
                            Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, ProductActivity.class);
                            startActivity(intent);
                            finish(); // Cierra esta actividad para que el usuario no pueda volver atrás
                        } else {
                            // Si falla el inicio de sesión, mostrar mensaje de error
                            Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}