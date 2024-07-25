package com.example.reto1pizzeria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reto1pizzeria.adapter.ProductAdapter;
import com.example.reto1pizzeria.helper.ProductDatabaseHelper;
import com.example.reto1pizzeria.helper.ProductFirebaseHelper;
import com.example.reto1pizzeria.model.Product;
import com.example.reto1pizzeria.monitor.NetworkMonitor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductActivity extends AppCompatActivity {

    private EditText editTextId, editTextName, editTextPrice, editTextDescription, editTextCant;
    private Button buttonAdd, buttonGetFirebase, buttonSichronized, buttonGetSqlite;
    private ListView listViewProducts;

    private ImageView home;

    // Objetos de ayuda
    private ProductDatabaseHelper databaseHelper;
    private ProductAdapter productAdapter;
    private ProductFirebaseHelper firebaseHelper;
    private NetworkMonitor networkMonitor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        initializeUIComponents();
        initializeHelpers();
        setUpEventListeners();
        authenticateFirebaseUser();

        home = findViewById(R.id.headerImgHome);
        //Configuración del botón de Home para regresar a main
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    // Inicializa los componentes de la interfaz de usuario

    private void initializeUIComponents() {
        editTextId = findViewById(R.id.editTextId);
        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextDescription = findViewById(R.id.editTextDescrip);
        editTextCant = findViewById(R.id.editTextStock);

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonGetFirebase = findViewById(R.id.buttonGetFirebase);
        buttonSichronized = findViewById(R.id.buttonSichronized);
        buttonGetSqlite = findViewById(R.id.buttonGetSqlite);

        listViewProducts = findViewById(R.id.listViewProducts);

        // Inicializar la base de datos y el adaptador
        databaseHelper = new ProductDatabaseHelper(this);
        List<Product> products = databaseHelper.getAllProducts();
        productAdapter = new ProductAdapter(this, R.layout.list_item_product, products);
        listViewProducts.setAdapter(productAdapter);
    }

    // Inicializa los objetos de ayuda
    private void initializeHelpers() {
        networkMonitor = new NetworkMonitor(this);
        firebaseHelper = new ProductFirebaseHelper();
    }

    // Configura los eventos de los botones
    private void setUpEventListeners() {
        buttonGetSqlite.setOnClickListener(v -> loadProductsFromDatabase());
        buttonSichronized.setOnClickListener(v -> synchronizeData());
        buttonGetFirebase.setOnClickListener(v -> loadProductsFromFirebase(true));
        buttonAdd.setOnClickListener(v -> handleAddOrUpdateProduct());
    }

    // Autentica al usuario de Firebase
    private void authenticateFirebaseUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                // Puedes usar el user.getUid() para identificar al usuario si es necesario
            } else {
                Toast.makeText(ProductActivity.this, "Error al iniciar sesión anónimo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Maneja la operación de agregar o actualizar producto
    private void handleAddOrUpdateProduct() {
        if (buttonAdd.getText().toString().equalsIgnoreCase("Agregar")) {
            addProduct();
        } else {
            saveProduct();
        }
    }

    // Métodos relacionados con la carga de datos
    private void loadProductsFromDatabase() {
        List<Product> products = databaseHelper.getAllProducts();
        updateProductList(products, false);
    }

    private void loadProductsFromFirebase(boolean hideButtons) {
        firebaseHelper.getAllProducts(new ProductFirebaseHelper.GetProductsCallback() {
            @Override
            public void onProductsRetrieved(List<Product> products) {
                updateProductList(products, hideButtons);
            }

            @Override
            public void onError() {
                Toast.makeText(ProductActivity.this, "Error al obtener productos de Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductList(List<Product> products, boolean hideButtons) {
        productAdapter.clear();
        productAdapter.addAll(products);
        productAdapter.setHideButtons(hideButtons);
        productAdapter.notifyDataSetChanged();
    }

    // Métodos relacionados con la sincronización de datos
    private void synchronizeData() {
        if (!networkMonitor.isNetworkAvailable()) {
            Toast.makeText(ProductActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            return;
        }
        synchronizeAndRemoveData();
        synchronizeAndLoadData();
    }

    private void synchronizeAndLoadData() {
        List<Product> productsFromSQLite = databaseHelper.getAllProducts();
        synchronizeProductsToFirebase(productsFromSQLite);
        loadProductsFromFirebase(true);
    }

    private void synchronizeAndRemoveData() {
        firebaseHelper.getAllProducts(new ProductFirebaseHelper.GetProductsCallback() {
            @Override
            public void onProductsRetrieved(List<Product> productsFromFirebase) {
                List<Product> productsFromSQLite = databaseHelper.getAllProducts();
                Set<String> sqliteProductIds = new HashSet<>();

                for (Product sqliteProduct : productsFromSQLite) {
                    sqliteProductIds.add(sqliteProduct.getId());
                }

                List<Product> productsToDeleteFromFirebase = new ArrayList<>();
                for (Product firebaseProduct : productsFromFirebase) {
                    if (!sqliteProductIds.contains(firebaseProduct.getId())) {
                        productsToDeleteFromFirebase.add(firebaseProduct);
                    }
                }

                deleteProductsFromFirebase(productsToDeleteFromFirebase);
            }

            @Override
            public void onError() {
                Toast.makeText(ProductActivity.this, "Error al obtener productos de Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void synchronizeProductsToFirebase(List<Product> productsFromSQLite) {
        for (Product product : productsFromSQLite) {
            firebaseHelper.checkIfProductExists(product.getId(), new ProductFirebaseHelper.ProductExistsCallback() {
                @Override
                public void onProductExists(boolean exists) {
                    if (exists) {
                        firebaseHelper.updateProduct(product);
                    } else {
                        firebaseHelper.addProduct(product, new ProductFirebaseHelper.AddProductCallback() {
                            @Override
                            public void onSuccess() {
                                // Producto agregado exitosamente
                            }

                            @Override
                            public void onError(Exception e) {
                                Toast.makeText(ProductActivity.this, "Error al agregar producto a Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(ProductActivity.this, "Error al verificar existencia del producto en Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void deleteProductsFromFirebase(List<Product> productsToDeleteFromFirebase) {
        for (Product productToDelete : productsToDeleteFromFirebase) {
            firebaseHelper.deleteProduct(productToDelete.getId(), new ProductFirebaseHelper.DeleteProductCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ProductActivity.this, "Producto eliminado de Firebase", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(ProductActivity.this, "Error al eliminar producto de Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        loadProductsFromFirebase(true);
    }

    // Métodos relacionados con la manipulación de productos
    private void addProduct() {
        if (areFieldsEmpty()) {
            return;
        }

        String name = editTextName.getText().toString();
        double price = Double.parseDouble(editTextPrice.getText().toString());
        String description = editTextDescription.getText().toString();
        double cant = Double.parseDouble(editTextCant.getText().toString());
        Product newProduct = new Product(name,price,description,cant);
        databaseHelper.addProduct(newProduct);
        loadProductsFromDatabase();
        clearInputFields();
        Toast.makeText(this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show();
    }

    private void saveProduct() {
        if (areFieldsEmpty()) {
            return;
        }

        String name = editTextName.getText().toString();
        double price = Double.parseDouble(editTextPrice.getText().toString());
        String description = editTextDescription.getText().toString();
        double cant = Double.parseDouble(editTextCant.getText().toString());
        Product newProduct = new Product(name,price,description,cant);
        databaseHelper.addProduct(newProduct);
        loadProductsFromDatabase();
        clearInputFields();
        Toast.makeText(this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show();
    }

    // Métodos de utilidad
    private boolean areFieldsEmpty() {
        if (editTextName.getText().toString().trim().isEmpty() || editTextPrice.getText().toString().trim().isEmpty()
                || editTextDescription.getText().toString().trim().isEmpty() || editTextCant.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void clearInputFields() {
        editTextId.setText("");
        editTextName.setText("");
        editTextPrice.setText("");
        editTextDescription.setText("");
        editTextCant.setText("");
    }

    public void editProduct(Product product) {
        editTextId.setText(product.getId());
        editTextName.setText(product.getName());
        editTextPrice.setText(String.valueOf(product.getPrice()));
        editTextDescription.setText(product.getDescription());
        editTextCant.setText(String.valueOf(product.getCant()));
        buttonAdd.setText("Guardar");
    }

    public void deleteProduct(Product product) {
        if (product.isDeleted()) {
            Toast.makeText(this, "Producto ya está eliminado", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.deleteProduct(product.getId());
            loadProductsFromDatabase();
            Toast.makeText(this, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();
        }
    }
}