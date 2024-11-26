package com.example.gearfit.controllers;

import com.example.gearfit.models.Exercise;
import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class RoutineDaySelectorController {

    private Routine routine;

    public AnchorPane rootPane;

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
                selectDayRoutine(day);
            });

            // Añadir el botón al HBox
            RoutineDaysList.getChildren().add(dayButton);
        }
    }

    public void setRoutine(Routine routine) {
        this.routine = routine; // Almacenar la rutina
    }

    private void selectDayRoutine(String routineDay) {
        // Obtener los ejercicios y sus series asociados a la rutina y el día seleccionado
        List<Exercise> exercises = RoutineDAO.getExercisesWithSetsByRoutineDay(routine.getId(), routineDay);

        // Cargar la ventana RoutineDayTable y pasarle los ejercicios
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gearfit/RoutineDayTable.fxml"));
            Parent newContent = loader.load();

            // Obtener el controlador de RoutineDayTable
            RoutineDayTableController controller = loader.getController();
            controller.setRoutineId(routine.getId());
            controller.setExercises(exercises);
            controller.setRoutineDay(routineDay);

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
}