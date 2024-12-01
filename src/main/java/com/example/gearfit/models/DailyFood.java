package com.example.gearfit.models;

import java.time.LocalDate;

public class DailyFood {
    private int id; // ID en la base de datos
    private int foodId; // ID del alimento (relación con Food)
    private LocalDate date; // Fecha del consumo
    private String mealType; // Tipo de comida (desayuno, almuerzo, cena, etc.)
    private double grams; // Gramos consumidos

    // Constructor
    public DailyFood(int id, int foodId, LocalDate date, String mealType, double grams) {
        this.id = id;
        this.foodId = foodId;
        this.date = date;
        this.mealType = mealType;
        this.grams = grams;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public double getGrams() {
        return grams;
    }

    public void setGrams(double grams) {
        this.grams = grams;
    }

    @Override
    public String toString() {
        return "DailyFood{" +
                "id=" + id +
                ", foodId=" + foodId +
                ", date=" + date +
                ", mealType='" + mealType + '\'' +
                ", grams=" + grams +
                '}';
    }
}
