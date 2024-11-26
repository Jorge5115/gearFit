package com.example.gearfit.models;

import java.util.Objects;

public class ExerciseSet {
    private int id;
    private int exerciseId;
    private int setNumber;
    private int repetitions;
    private double weight;

    public ExerciseSet(int id, int exerciseId, int setNumber, int repetitions, double weight) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.setNumber = setNumber;
        this.repetitions = repetitions;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ExerciseSet that = (ExerciseSet) obj;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public double getWeight() {
        return weight;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}