package com.example.gearfit.controllers;

import com.example.gearfit.exceptions.RoutineException;
import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import com.example.gearfit.connections.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
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
        try {
            String routineName = routineNameField.getText().trim();

            if (routineName.isEmpty()) {
                throw new RoutineException("El nombre de la rutina no puede estar vacío.");
            }

            // Obtener el ID del usuario actual desde SessionManager
            int userId = SessionManager.getCurrentUser().getId();

            // Obtener los días seleccionados
            List<String> selectedDays = getSelectedDays();

            // Crear el objeto Routine con el nombre y el ID del usuario
            Routine newRoutine = new Routine(userId, routineName);

            // Guardar la rutina en la base de datos
            if (!RoutineDAO.createRoutine(newRoutine)) {
                throw new RoutineException("Error al crear la rutina en la base de datos.");
            }

            // Guardar los días seleccionados en la tabla routine_days
            if (!RoutineDAO.saveRoutineDays(newRoutine.getId(), selectedDays)) {
                throw new RoutineException("Error al guardar los días de la rutina.");
            }

            System.out.println("Rutina creada con éxito.");
            replaceContent("/com/example/gearfit/RoutineSelector.fxml");
        } catch (RoutineException e) {
            handleException(e);
        } catch (Exception e) {
            handleException(new RoutineException("Error inesperado al guardar la rutina.", e));
        }
    }

    @FXML
    private void toggleDay(ActionEvent event) {
        try {
            Button clickedButton = (Button) event.getSource();

            if (clickedButton.getStyleClass().contains("selected")) {
                clickedButton.getStyleClass().remove("selected");
                clickedButton.getStyleClass().add("deselected");
            } else {
                clickedButton.getStyleClass().add("selected");
                clickedButton.getStyleClass().remove("deselected");
            }
        } catch (Exception e) {
            handleException(new RoutineException("Error al seleccionar el día.", e));
        }
    }

    @FXML
    private void cancelRoutine(ActionEvent event) {
        try {
            replaceContent("/com/example/gearfit/RoutineSelector.fxml");
        } catch (Exception e) {
            handleException(new RoutineException("Error al cancelar la creación de la rutina.", e));
        }
    }

    private void replaceContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newContent = loader.load();

            rootPane.getChildren().setAll(newContent);
            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);
        } catch (IOException e) {
            throw new RoutineException("Error al cargar la vista: " + fxmlPath, e);
        }
    }

    private List<String> getSelectedDays() {
        List<String> selectedDays = new ArrayList<>();
        try {
            if (mondayButton.getStyleClass().contains("selected")) selectedDays.add("Lunes");
            if (tuesdayButton.getStyleClass().contains("selected")) selectedDays.add("Martes");
            if (wednesdayButton.getStyleClass().contains("selected")) selectedDays.add("Miércoles");
            if (thursdayButton.getStyleClass().contains("selected")) selectedDays.add("Jueves");
            if (fridayButton.getStyleClass().contains("selected")) selectedDays.add("Viernes");
            if (saturdayButton.getStyleClass().contains("selected")) selectedDays.add("Sábado");
            if (sundayButton.getStyleClass().contains("selected")) selectedDays.add("Domingo");
        } catch (Exception e) {
            throw new RoutineException("Error al obtener los días seleccionados.", e);
        }
        return selectedDays;
    }


    private void handleException(RoutineException e) {
        e.printStackTrace(); // Registro de la excepción (puedes cambiar esto a un logger)
        showErrorAlert("Error", "Ha ocurrido un problema", e.getMessage());
    }


    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
