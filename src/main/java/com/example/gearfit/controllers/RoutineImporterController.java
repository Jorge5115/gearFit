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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
                HBox routineBox = new HBox(10);
                routineBox.getStyleClass().add("routine-import-box");

                Label routineLabel = new Label(routine.getName());
                routineLabel.getStyleClass().add("routine-label");
                routineLabel.setMaxWidth(700);

                Button importButton = new Button("Importar");
                importButton.getStyleClass().add("importer-button");
                importButton.setPrefWidth(100);

                // Asignar acción al botón de importar
                importButton.setOnAction(event -> {
                    importRoutineToUser (routine);
                    //System.out.println(routine.getId());
                });

                routineBox.getChildren().addAll(routineLabel, importButton);
                availableRoutinesList.getChildren().add(routineBox);
            }
        } else {
            System.out.println("No se encontró un usuario autenticado en la sesión.");
        }
    }


    private void importRoutineToUser(Routine routine) {
        var currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            // Llamar al método que importa la rutina completa
            int newRoutineId = routineDAO.importRoutineForUser(routine.getId(), currentUser.getId());

            if (newRoutineId != -1) {
                loadRoutineDetails(newRoutineId); // Cargar los detalles de la nueva rutina si es necesario
            } else {
                System.out.println("Error al importar la rutina.");
            }
        } else {
            System.out.println("No se encontró un usuario autenticado en la sesión.");
        }
    }

    private void loadRoutineDetails(int routineId) {
        // Consultar los días de la rutina desde la base de datos
        List<String> days = routineDAO.getDaysByRoutineId(routineId);

        if (days.isEmpty()) {
            System.out.println("No se encontraron días para la rutina.");
        } else {
            System.out.println("Días de la rutina cargados correctamente.");
            for (String day : days) {
                System.out.println("Día: " + day);

                // Consultar los ejercicios asociados a este día
                List<Exercise> exercises = routineDAO.getExercisesWithSetsByRoutineDay(routineId, day);
                for (Exercise exercise : exercises) {
                    System.out.println("Ejercicio: " + exercise.getName());

                    // Consultar y mostrar los sets asociados al ejercicio
                    for (ExerciseSet set : exercise.getSets()) {
                        System.out.println("Set: " + set.getSetNumber() + ", Reps: " + set.getRepetitions() + ", Peso: " + set.getWeight());
                    }
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