package com.example.reto1pizzeria.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.reto1pizzeria.model.Usuario;

public class UsuarioViewModel extends ViewModel {
    private Usuario usuario;

    // Método para inicializar Usuario con un mail y una password
    public void init(String user,String password){
        usuario = new Usuario(user, password);
    }

    public String getUser(){
        return usuario.getUser();
    }

    public void setUser(String user){
        usuario.setUser(user);
    }

    public String getPassword(){
        return usuario.getPassword();
    }

    public void setPassword(String password){
        usuario.setPassword(password);
    }

}