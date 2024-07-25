package com.example.reto1pizzeria.model;

import java.util.HashMap;
import java.util.Map;

public class Product {
    // Atributos de la clase Product
    private String id; // Identificador único del producto
    private String name; // Nombre del producto
    private double price; // Precio del producto
    private String description; // Descripción del producto

    private double cant; //Cantidad en Stock

    private boolean deleted; // Indica si el producto ha sido eliminado

    // Constructor vacío necesario para ciertas integraciones como Firebase
    public Product() {
    }

    // Constructor con parámetros para inicializar un producto con un ID, nombre y precio
    public Product(String id, String name,double price,String description, double cant ) {
        this.id = id; // Asigna el ID proporcionado
        this.name = name; // Asigna el nombre proporcionado
        this.price = price; // Asigna el precio proporcionado
        this.description = description; //Asigna la descripción
        this.cant = cant; //Asigna la Cant en Stock

    }

    // Constructor sin ID, para inicializar un producto solo con nombre y precio

    public Product(String name, double price, String description, double cant) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.cant = cant;
    }


    // Getters y setters para los atributos

    // Obtiene el ID del producto
    public String getId() {
        return id;
    }

    // Establece el ID del producto
    public void setId(String id) {
        this.id = id;
    }

    // Obtiene el nombre del producto
    public String getName() {
        return name;
    }

    // Establece el nombre del producto
    public void setName(String name) {
        this.name = name;
    }


    // Obtiene el precio del producto
    public double getPrice() {
        return price;
    }

    // Establece el precio del producto
    public void setPrice(double price) {
        this.price = price;
    }

    // Obtiene a descripcion del producto
    public String getDescription() {
        return description;
    }

    // Establece la descripcion del producto
    public void setDescription(String description) {
        this.description = description;
    }

    // Obtiene la cantidad en stock
    public double getCant() {
        return cant;
    }

    // Establece la cantidad en stock
    public void setCant(double cant) {
        this.cant = cant;
    }

    // Obtiene el estado de eliminación del producto
    public boolean isDeleted() {
        return deleted;
    }

    // Establece el estado de eliminación del producto
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    // Convierte las propiedades del producto en un mapa para almacenamiento o envío
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id); // Agrega el ID al mapa
        result.put("name", name); // Agrega el nombre al mapa
        result.put("price", price); // Agrega el precio al mapa
        result.put("description", description); //Agrega la descripcion al mapa
        result.put("cant", cant);//Agrega la Cantidad en Stock al mapa
        return result; // Devuelve el mapa con las propiedades del producto
    }
}
