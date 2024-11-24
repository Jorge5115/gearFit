package com.example.gearfit.models;

public class Exercise {
    private int id;          // ID del ejercicio
    private String name;     // Nombre del ejercicio
    private String tempo;    // Tempo del ejercicio (ejemplo: '3-1-1-0')
    private int restTime;    // Tiempo de descanso en segundos
    private int routineDayId; // ID del día de rutina al que pertenece

    // Constructor
    public Exercise(int id, String name, String tempo, int restTime, int routineDayId) {
        this.id = id;
        this.name = name;
        this.tempo = tempo;
        this.restTime = restTime;
        this.routineDayId = routineDayId;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public int getRestTime() {
        return restTime;
    }

    public void setRestTime(int restTime) {
        this.restTime = restTime;
    }

    public int getRoutineDayId() {
        return routineDayId;
    }

    public void setRoutineDayId(int routineDayId) {
        this.routineDayId = routineDayId;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tempo='" + tempo + '\'' +
                ", restTime=" + restTime +
                ", routineDayId=" + routineDayId +
                '}';
    }
}