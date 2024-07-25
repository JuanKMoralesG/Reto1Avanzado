package com.example.reto1pizzeria.model;

public class Usuario {
    // Atributos privados de la clase Usuario
    private String user,password;

    // Constructor de la clase Usuario que recibe user y password
    public Usuario(String user, String password) {
        this.user = user;
        this.password = password;
    }

    // Método para obtener y establecer el usuario de la persona
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // Método para obtener y establecer el password de la persona
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
