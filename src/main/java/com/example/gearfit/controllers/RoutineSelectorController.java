package com.example.gearfit.controllers;

import com.example.gearfit.repositories.RoutineDAO;
import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.models.Routine;
import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RoutineSelectorController {


    @FXML
    private VBox RoutinesList;

    @FXML
    private AnchorPane rootPane;

    @FXML
    public void initialize() {
        loadUserRoutines();
    }

    private void loadUserRoutines() {
        // Obtener el usuario autenticado
        var currentUser = SessionManager.getCurrentUser();

        if (currentUser != null) {
            // Obtener las rutinas del usuario desde la base de datos
            List<Routine> routines = RoutineDAO.getRoutinesByUserId(currentUser.getId());

            // Limpiar la lista en el VBox antes de añadir los elementos
            RoutinesList.getChildren().clear();

            // Para cada rutina, crea un Label y agrégalo al VBox
            for (Routine routine : routines) {
                Button routineButton = new Button(routine.getName());
                routineButton.getStyleClass().add("routine-button");

                // Evento de clic para mostrar detalles de la rutina
                routineButton.setOnMouseClicked(event -> {
                    System.out.println("Clicked on routine: " + routine.getName());
                    // Aquí puedes cargar más detalles o iniciar una acción específica
                });

                RoutinesList.getChildren().add(routineButton);  // Añade el Label al VBox
            }
        } else {
            System.out.println("No se encontró un usuario autenticado en la sesión.");
        }
    }

    @FXML
    public void addNewRoutine(ActionEvent event) {
        replaceContent("/com/example/gearfit/RoutineCreator.fxml");
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
