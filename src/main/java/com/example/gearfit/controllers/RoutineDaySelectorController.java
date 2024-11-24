package com.example.gearfit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.List;

public class RoutineDaySelectorController {

    @FXML
    private HBox RoutineDaysList;

    public void setDays(List<String> days) {
        // Limpiar el HBox antes de añadir nuevos días
        RoutineDaysList.getChildren().clear();

        // Añadir un botón por cada día a la lista
        for (String day : days) {
            Button dayButton = new Button(day);
            dayButton.getStyleClass().add("day-button"); // Añadir clase CSS si es necesario

            // Aquí puedes añadir un evento al botón si lo deseas
            dayButton.setOnAction(event -> {
                System.out.println("Día seleccionado: " + day);
                // Aquí puedes añadir la lógica que desees al seleccionar un día
            });

            // Añadir el botón al HBox
            RoutineDaysList.getChildren().add(dayButton);
        }
    }
}