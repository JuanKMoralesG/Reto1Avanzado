package com.example.reto1pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // Declaración de los elementos de la interfaz de usuario
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText editTextUser, editTextPassword;
    private Button buttonRegister;
    private ImageView home;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Inicializa FireBase Authentication
        mAuth = FirebaseAuth. getInstance();

        //Vincula los componente de la interfaz del usuario con las variables
        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Inicializa Firebase Firestore
        db = FirebaseFirestore. getInstance();

        home = findViewById(R.id.headerImgHome);
        //Configuración del botón de Home para regresar a main
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // Configura el listener del botón "Register" para registrar al usuario
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();//Metodo para registrar usuario
            }
        });
    }

    // Método para registrar al usuario en Firebase Authentication y Firestore
    private void registerUser(){
        //Obtiene los datos ingresados por el usuario
        String usuario = editTextUser.getText().toString().trim();
        String clave = editTextPassword.getText().toString().trim();

        //Verificar que no haya campos vacios
        if(usuario.isEmpty()||clave.isEmpty()) {
            Toast.makeText(this, "Diligencie todos los campos", Toast.LENGTH_SHORT).show();
            return;

            //Verificar que la contraseña tenga al menos 8 caracteres

        }

        if(clave.length()<6){
            Toast.makeText(this,"La contraseña debe tener al menos 8 caracteres",Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea el usuario en Firebase Authentication
        mAuth.createUserWithEmailAndPassword(usuario,clave).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Usuario registrado con exito en Firebase Authentication
                    Log.d("Firebase Authentication", "Usuario registrado con éxito");
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user!= null){
                        //REgistrar la informacion del usuario en Firestore
                        Map<String,Object> userData = new HashMap<>();
                        userData.put("Usuario",usuario);
                        userData.put("Password",clave);

                        db.collection("Usuarios")
                                .document(user.getUid())
                                .set(userData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void Void) {
                                        // Usuario registrado exitosamente en Firestore
                                        Log.d("Firestore", "Usuario registrado exitosamente en Firestore");
                                        Toast.makeText(RegisterActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        //Limpiar los campos despues del registro exitoso
                        editTextUser.setText("");
                        editTextPassword.setText("");

                        //Redirige al usuario a la pantalla de Inicio de Sesion
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // Si falla la autenticación, muestra un mensaje de error
                    Log.e("Firebase Authentication", "Error al registrar usuario en Firebase Authentication", task.getException());
                    Toast.makeText(RegisterActivity.this, "Error al registrar usuario en Firebase Authentication", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}