package com.example.gearfit.models;

public class Routine {

    private int id;

    private int userId;

    private String name;

    public Routine(int id, int userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    // Constructor sin ID, para crear una nueva rutina (sin conocer aún el ID asignado en la BD)
    public Routine(int userId, String name) {
        this.userId = userId;
        this.name = name;
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
}
