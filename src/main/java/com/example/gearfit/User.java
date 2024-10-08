package com.example.gearfit;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class User {
    private int id;
    private String nombre;
    private String email;


    // Constructor
    public User(int id, String nombre, String email) {
        setId(id);
        setNombre(nombre);
        setEmail(email);
    }

    public User() {
        // constructor vacio
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }


    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        if (esEmailValido(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }
    }

    private boolean esEmailValido(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"; // Expresión regular básica para el email
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

}
