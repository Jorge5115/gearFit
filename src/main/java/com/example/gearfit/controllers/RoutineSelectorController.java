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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

            for (Routine routine : routines) {
                // Crear un HBox para contener el texto y el botón
                HBox routineBox = new HBox();
                routineBox.getStyleClass().add("routine-button");

                // Crear un Label para el nombre de la rutina
                Label routineLabel = new Label(routine.getName());
                routineLabel.getStyleClass().add("routine-label");

                // Crear un botón para borrar la rutina
                Button deleteButton = new Button();
                deleteButton.getStyleClass().add("delete-button");

                // Crear un Label con la "X"
                Label deleteLabel = new Label("Borrar rutina");
                deleteLabel.getStyleClass().add("delete-label");

                // Añadir el Label con la "X" al botón
                deleteButton.setGraphic(deleteLabel);

                routineBox.setOnMouseClicked(event -> {
                    selectRoutine(routine);
                });

                // Asignar evento al botón para borrar la rutina
                deleteButton.setOnAction(event -> {
                    System.out.println("Deleting routine: " + routine.getName());
                    deleteRoutine(routine);
                    loadUserRoutines(); // Recargar las rutinas después de borrar
                });

                // Añadir el Label y el botón al HBox
                routineBox.getChildren().addAll(routineLabel, deleteButton);

                // Añadir el HBox al VBox principal
                RoutinesList.getChildren().add(routineBox);
            }
        } else {
            System.out.println("No se encontró un usuario autenticado en la sesión.");
        }
    }

    // Añadir un método para manejar la selección de una rutina
    private void selectRoutine(Routine routine) {
        // Obtener los días asociados a la rutina
        List<String> days = RoutineDAO.getDaysByRoutineId(routine.getId());

        // Cargar la ventana RoutineDaySelector y pasarle los días
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gearfit/RoutineDaySelector.fxml"));
            Parent newContent = loader.load();

            // Obtener el controlador de RoutineDaySelector
            RoutineDaySelectorController controller = loader.getController();
            controller.setDays(days);

            // Reemplazar el contenido del rootPane con el nuevo contenido
            rootPane.getChildren().setAll(newContent);

            // Anclar el nuevo contenido a los bordes del AnchorPane
            AnchorPane.setTopAnchor(newContent, 0.0);
            AnchorPane.setBottomAnchor(newContent, 0.0);
            AnchorPane.setLeftAnchor(newContent, 0.0);
            AnchorPane.setRightAnchor(newContent, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void deleteRoutine(Routine routine) {
        RoutineDAO.deleteRoutine(routine.getId());
        System.out.println("Routine deleted: " + routine.getName());
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
