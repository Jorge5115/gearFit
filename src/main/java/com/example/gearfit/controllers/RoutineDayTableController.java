package com.example.gearfit.controllers;

import com.example.gearfit.models.Exercise;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class RoutineDayTableController {

    @FXML
    private TableView<Exercise> exerciseTable;

    @FXML
    private TableColumn<Exercise, String> nameColumn;

    @FXML
    private TableColumn<Exercise, String> tempoColumn;

    @FXML
    private TableColumn<Exercise, Integer> restTimeColumn;

    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tempoColumn.setCellValueFactory(new PropertyValueFactory<>("tempo"));
        restTimeColumn.setCellValueFactory(new PropertyValueFactory<>("restTime"));
    }

    public void setExercises(List<Exercise> exercises) {
        exerciseTable.getItems().clear();
        exerciseTable.getItems().addAll(exercises);
    }

    @FXML
    private void handleBackButton() {
        // Aquí puedes implementar la lógica para volver a la pantalla anterior
        // Por ejemplo, podrías cargar nuevamente el RoutineDaySelectorController
    }
}