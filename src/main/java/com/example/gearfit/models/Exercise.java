package com.example.gearfit.models;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private int id;
    private String name;
    private String tempo;
    private int restTime;
    private int routineId; // ID de la rutina a la que pertenece
    private List<ExerciseSet> sets; // Lista de series asociadas al ejercicio

    // Constructor
    public Exercise(int id, String name, String tempo, int restTime, int routineId) {
        this.id = id;
        this.name = name;
        this.tempo = tempo;
        this.restTime = restTime;
        this.routineId = routineId;
        this.sets = new ArrayList<>(); // Inicializar la lista de sets
    }

    public Exercise() {
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

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    // Método para añadir una serie
    public void addSet(ExerciseSet set) {
        sets.add(set);
    }

    // Método para obtener las series
    public List<ExerciseSet> getSets() {
        return sets;
    }

    // Método para obtener el número total de sets
    public int getTotalSets() {
        return sets.size();
    }

    public void setSets(List<ExerciseSet> sets) {
        this.sets = sets;
    }
}