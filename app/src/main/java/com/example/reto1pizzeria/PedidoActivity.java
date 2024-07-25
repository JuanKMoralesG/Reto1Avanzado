package com.example.reto1pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reto1pizzeria.adapter.PedidoAdapter;
import com.example.reto1pizzeria.helper.ProductDatabaseHelper;
import com.example.reto1pizzeria.helper.ProductFirebaseHelper;
import com.example.reto1pizzeria.model.Product;
import com.example.reto1pizzeria.monitor.NetworkMonitor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PedidoActivity extends AppCompatActivity {

    private ImageView home;

    private Button buttonSichronized;
    private ListView listViewProductsPedido;

    private ProductDatabaseHelper databaseHelper;
    private PedidoAdapter pedidoAdapter;
    private ProductFirebaseHelper firebaseHelper;
    private NetworkMonitor networkMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pedido);

        initializeUIComponents();
        initializeHelpers();
        setUpEventListeners();
        authenticateFirebaseUser();

        home = findViewById(R.id.headerImgHome);
        //Configuración del botón de Home para regresar a main
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PedidoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Inicializa los componentes de la interfaz de usuario

    private void initializeUIComponents() {
        buttonSichronized = findViewById(R.id.buttonSichronized);

        listViewProductsPedido = findViewById(R.id.listViewProductsPedido);

        // Inicializar la base de datos y el adaptador
        databaseHelper = new ProductDatabaseHelper(this);
        List<Product> products = databaseHelper.getAllProducts();
        pedidoAdapter = new PedidoAdapter(this, R.layout.list_item_pedido, products);
        listViewProductsPedido.setAdapter(pedidoAdapter);
    }

    // Inicializa los objetos de ayuda
    private void initializeHelpers () {
        networkMonitor = new NetworkMonitor(this);
        firebaseHelper = new ProductFirebaseHelper();
    }

    // Configura los eventos de los botones
    private void setUpEventListeners () {
        buttonSichronized.setOnClickListener(v -> synchronizeData());
    }

    // Autentica al usuario de Firebase
    private void authenticateFirebaseUser () {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                // Puedes usar el user.getUid() para identificar al usuario si es necesario
            } else {
                Toast.makeText(PedidoActivity.this, "Error al iniciar sesión anónimo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Métodos relacionados con la carga de datos
    private void loadProductsFromDatabase () {
        List<Product> products = databaseHelper.getAllProducts();
        updateProductList(products, false);
    }

    private void loadProductsFromFirebase ( boolean hideButtons){
        firebaseHelper.getAllProducts(new ProductFirebaseHelper.GetProductsCallback() {
            @Override
            public void onProductsRetrieved(List<Product> products) {
                updateProductList(products, hideButtons);
            }

            @Override
            public void onError() {
                Toast.makeText(PedidoActivity.this, "Error al obtener productos de Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductList (List < Product > products,boolean hideButtons){
        pedidoAdapter.clear();
        pedidoAdapter.addAll(products);
        pedidoAdapter.setHideButtons(hideButtons);
        pedidoAdapter.notifyDataSetChanged();
    }

    // Métodos relacionados con la sincronización de datos
    private void synchronizeData () {
        if (!networkMonitor.isNetworkAvailable()) {
            Toast.makeText(PedidoActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            return;
        }
    }


}
