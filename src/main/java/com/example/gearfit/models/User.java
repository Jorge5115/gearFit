package com.example.gearfit.models;

import java.util.regex.Pattern;

public class User {

    private int id;

    private String username;

    private String email;

    private double height;

    private double weight;

    public User(int id, String nombre, String email, double height, String weight) {
        setId(id);
        setUsername(username);
        setEmail(email);
    }

    public User(String username, String email){
        setUsername(username);
        setEmail(email);
        setHeight(0.0);
        setWeight(0.0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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
