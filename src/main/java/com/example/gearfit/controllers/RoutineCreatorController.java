package com.example.gearfit.controllers;

import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import com.example.gearfit.connections.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
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
    private CheckBox mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox, fridayCheckBox, saturdayCheckBox, sundayCheckBox;

    // Método para guardar la rutina
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
        if (mondayCheckBox.isSelected()) selectedDays.add("Lunes");
        if (tuesdayCheckBox.isSelected()) selectedDays.add("Martes");
        if (wednesdayCheckBox.isSelected()) selectedDays.add("Miércoles");
        if (thursdayCheckBox.isSelected()) selectedDays.add("Jueves");
        if (fridayCheckBox.isSelected()) selectedDays.add("Viernes");
        if (saturdayCheckBox.isSelected()) selectedDays.add("Sábado");
        if (sundayCheckBox.isSelected()) selectedDays.add("Domingo");

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

            // Anclar el nuevo contenido a los bordes del AnchorPane para asegurarnos de que ocupe todo el espacio
            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}