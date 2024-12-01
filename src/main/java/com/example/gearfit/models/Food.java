package com.example.gearfit.models;

public class Food {
    private int id; // ID en la base de datos
    private int userId; // ID del usuario propietario del alimento
    private String name; // Nombre del alimento
    private double fatsPer100g; // Grasas por cada 100g
    private double carbsPer100g; // Carbohidratos por cada 100g
    private double proteinsPer100g; // Proteínas por cada 100g
    private double kcalPer100g; // Calorías por cada 100g

    // Constructor
    public Food(int id, int userId, String name, double fatsPer100g, double carbsPer100g, double proteinsPer100g, double kcalPer100g) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.fatsPer100g = fatsPer100g;
        this.carbsPer100g = carbsPer100g;
        this.proteinsPer100g = proteinsPer100g;
        this.kcalPer100g = kcalPer100g;
    }

    public Food(int id, String name, double fatsPer100g, double carbsPer100g, double proteinsPer100g, double kcalPer100g) {
        this.id = id;
        this.name = name;
        this.fatsPer100g = fatsPer100g;
        this.carbsPer100g = carbsPer100g;
        this.proteinsPer100g = proteinsPer100g;
        this.kcalPer100g = kcalPer100g;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFats() {
        return  fatsPer100g;
    }

    public void setFatsPer100g(double fatsPer100g) {
        this.fatsPer100g = fatsPer100g;
    }

    public double getCarbs() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public double getProtein() {
        return  proteinsPer100g;
    }

    public void setProteinsPer100g(double proteinsPer100g) {
        this.proteinsPer100g = proteinsPer100g;
    }

    public double getCalories() {
        return kcalPer100g;
    }

    public void setKcalPer100g(double kcalPer100g) {
        this.kcalPer100g = kcalPer100g;
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", fatsPer100g=" + fatsPer100g +
                ", carbsPer100g=" + carbsPer100g +
                ", proteinsPer100g=" + proteinsPer100g +
                ", kcalPer100g=" + kcalPer100g +
                '}';
    }
}
