package com.example.gearfit.models;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public class User {

    private int id;

    private String username;

    private String email;

    private int height;

    private double weight;

    private String password; // Añadir campo para la contraseña

    private int calories;

    public User(int id, String username, String email, int height, double weight, int calories) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setHeight(height);
        setWeight(weight);
        setCalories(calories);
    }

    public User(String username, String email){
        setUsername(username);
        setEmail(email);
    }

    public User() {
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
        if (username == null || username.length() < 3) {
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres.");
        }
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getCalories() { return calories; }

    public void setCalories(int calories) { this.calories = calories; }

    public void setEmail(String email) {
        if (esEmailValido(email)) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("El formato del email no es válido.");
        }
    }

    private boolean esEmailValido(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // Expresión regular básica para el email
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public String getPassword() {
        // No implementamos un getter para la contraseña para evitar exponerla
        return password;
    }
    public void setPassword(String password) {
        // Hashea la contraseña y la almacena
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

}
