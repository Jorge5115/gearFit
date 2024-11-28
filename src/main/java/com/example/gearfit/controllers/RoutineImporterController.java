package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.models.Exercise;
import com.example.gearfit.models.ExerciseSet;
import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class RoutineImporterController {

    @FXML
    private VBox availableRoutinesList;

    @FXML
    private AnchorPane rootPane;

    private RoutineDAO routineDAO;

    @FXML
    public void initialize() {
        loadAvailableRoutines();
    }

    private void loadAvailableRoutines() {
        var currentUser  = SessionManager.getCurrentUser ();
        if (currentUser  != null) {
            // Obtener rutinas solo del usuario "admin"
            List<Routine> availableRoutines = routineDAO.getRoutinesByUserId(1);

            // Limpiar la lista antes de añadir las rutinas
            availableRoutinesList.getChildren().clear();

            for (Routine routine : availableRoutines) {
                HBox routineBox = new HBox();
                routineBox.getStyleClass().add("routine-import-box");

                Label routineLabel = new Label(routine.getName());
                routineLabel.getStyleClass().add("routine-label");

                Button importButton = new Button("Importar");
                importButton.getStyleClass().add("import-button");

                // Asignar acción al botón de importar
                importButton.setOnAction(event -> {
                    importRoutineToUser (routine);
                    System.out.println(routine.getId());
                });

                routineBox.getChildren().addAll(routineLabel, importButton);
                availableRoutinesList.getChildren().add(routineBox);
            }
        } else {
            System.out.println("No se encontró un usuario autenticado en la sesión.");
        }
    }

    private void importRoutineToUser (Routine routine) {
        var currentUser  = SessionManager.getCurrentUser ();
        if (currentUser  != null) {
            // Insertar la rutina en la base de datos asociada al usuario
            routineDAO.addRoutineToUser (currentUser .getId(), routine.getName());
            System.out.println("Rutina importada: " + routine.getName());

            // Cargar los detalles de la rutina importada
            loadRoutineDetails(routine.getId());
        } else {
            System.out.println("No se encontró un usuario autenticado en la sesión.");
        }
    }

    private void loadRoutineDetails(int routineId) {
        // Obtener los días de la rutina
        List<String> days = routineDAO.getDaysByRoutineId(routineId);
        for (String day : days) {
            System.out.println("Día: " + day);

            // Obtener los ejercicios para el día
            List<Exercise> exercises = routineDAO.getExercisesWithSetsByRoutineDay(routineId, day);
            for (Exercise exercise : exercises) {
                System.out.println("Ejercicio: " + exercise.getName());
                // Imprimir series del ejercicio
                for (ExerciseSet set : exercise.getSets()) {
                    System.out.println("  Set: " + set.getSetNumber() + ", Repeticiones: " + set.getRepetitions() + ", Peso: " + set.getWeight());
                }
            }
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            // Regresar a la vista principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gearfit/RoutineSelector.fxml"));
            Parent mainContent = loader.load();
            rootPane.getChildren().setAll(mainContent);

            // Anclar a los bordes
            AnchorPane.setTopAnchor(mainContent, 0.0);
            AnchorPane.setBottomAnchor(mainContent, 0.0);
            AnchorPane.setLeftAnchor(mainContent, 0.0);
            AnchorPane.setRightAnchor(mainContent, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}