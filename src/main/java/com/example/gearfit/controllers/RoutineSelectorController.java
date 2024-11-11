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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RoutineSelectorController {

    @FXML
    private VBox RoutinesList;  // El VBox donde se mostrarán las rutinas

    @FXML
    public void initialize() {
        loadUserRoutines();  // Llama a este método al inicializar el controlador para cargar las rutinas
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
                Label routineLabel = new Label(routine.getName());
                routineLabel.getStyleClass().add("routine-label");

                // Evento de clic para mostrar detalles de la rutina
                routineLabel.setOnMouseClicked(event -> {
                    System.out.println("Clicked on routine: " + routine.getName());
                    // Aquí puedes cargar más detalles o iniciar una acción específica
                });

                RoutinesList.getChildren().add(routineLabel);  // Añade el Label al VBox
            }
        } else {
            System.out.println("No se encontró un usuario autenticado en la sesión.");
        }
    }

    @FXML
    public void addNewRoutine(ActionEvent event) {
        try {
            // Cargar la vista del formulario para crear una nueva rutina
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gearfit/RoutineCreator.fxml"));
            Parent parent = loader.load();

            // Crear una nueva ventana (stage) para el formulario
            Stage stage = new Stage();
            stage.setTitle("Crear Nueva Rutina");
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);  // Bloquea la ventana principal mientras se abre esta
            stage.showAndWait();

            // Actualizar la lista de rutinas después de agregar la nueva
            loadUserRoutines();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al abrir el formulario de nueva rutina: " + e.getMessage());
        }
    }
}
