package com.example.reto1pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonLogin, buttonPedido;
    private ImageView home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Vinculación de las vistas con los objetos
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonPedido = findViewById(R.id.buttonPedido);

        //Configuración del botón de Login para abrir la actividad de inicio de sesion
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finaliza esta actividad para que el usuario no pueda volver atrás
            }
        });

        //Configuración del Boton para hacer pedido
        buttonPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PedidoActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}