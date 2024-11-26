package com.example.gearfit.controllers;

import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import com.example.gearfit.connections.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RoutineCreatorController {


    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField routineNameField;

    @FXML
    private Button mondayButton, tuesdayButton, wednesdayButton, thursdayButton, fridayButton, saturdayButton, sundayButton;

    @FXML
    public void saveRoutine(ActionEvent event) {
        String routineName = routineNameField.getText().trim();

        if (routineName.isEmpty()) {
            System.out.println("Por favor ingresa un nombre para la rutina.");
            return;
        }

        // Obtener el ID del usuario actual desde SessionManager
        int userId = SessionManager.getCurrentUser().getId();

        // Obtener los días seleccionados
        List<String> selectedDays = new ArrayList<>();
        if (mondayButton.getStyleClass().contains("selected")) selectedDays.add("Lunes");
        if (tuesdayButton.getStyleClass().contains("selected")) selectedDays.add("Martes");
        if (wednesdayButton.getStyleClass().contains("selected")) selectedDays.add("Miércoles");
        if (thursdayButton.getStyleClass().contains("selected")) selectedDays.add("Jueves");
        if (fridayButton.getStyleClass().contains("selected")) selectedDays.add("Viernes");
        if (saturdayButton.getStyleClass().contains("selected")) selectedDays.add("Sábado");
        if (sundayButton.getStyleClass().contains("selected")) selectedDays.add("Domingo");

        // Crear el objeto Routine con el nombre y el ID del usuario
        Routine newRoutine = new Routine(userId, routineName);

        // Guardar la rutina en la base de datos
        boolean isRoutineCreated = RoutineDAO.createRoutine(newRoutine);

        if (isRoutineCreated) {
            System.out.println("Rutina creada con éxito.");
            System.out.println("Nombre de rutina: " + routineName);
            System.out.println("Días seleccionados: " + selectedDays);

            // Guardar los días seleccionados en la tabla routine_days
            boolean areDaysSaved = RoutineDAO.saveRoutineDays(newRoutine.getId(), selectedDays);

            if (areDaysSaved) {
                System.out.println("Días de la rutina guardados con éxito.");
            } else {
                System.out.println("Error al guardar los días de la rutina.");
            }

            replaceContent("/com/example/gearfit/RoutineSelector.fxml");
        } else {
            System.out.println("Error al crear la rutina.");
        }
    }

    @FXML
    private void toggleDay(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();

        if (clickedButton.getStyleClass().contains("selected")) {
            // Si ya está seleccionado, lo deseleccionamos
            clickedButton.getStyleClass().remove("selected");
            clickedButton.getStyleClass().add("deselected");
        } else {
            // Si no está seleccionado, lo seleccionamos
            clickedButton.getStyleClass().add("selected");
            clickedButton.getStyleClass().remove("deselected");
        }
    }

    @FXML
    private void cancelRoutine(ActionEvent event) {
        replaceContent("/com/example/gearfit/RoutineSelector.fxml");

    }

    private void replaceContent(String fxmlPath) {
        try {
            // Cargar el archivo FXML de la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newContent = loader.load();

            // Reemplazar solo el contenido interior del rootPane (sin cambiar la estructura principal)
            rootPane.getChildren().setAll(newContent);

            // Anclar el nuevo contenido a los bordes del AnchorPane para asegurarnos de que ocupe el espacio entero
            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}