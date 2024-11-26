package com.example.gearfit.controllers;

import com.example.gearfit.exceptions.RoutineDaySelectorException;
import com.example.gearfit.models.Exercise;
import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.List;

public class RoutineDaySelectorController {

    private Routine routine;

    public AnchorPane rootPane;

    public Label routineNameLabel;

    @FXML
    private HBox RoutineDaysList;

    public void setDays(List<String> days) {
        try{
            routineNameLabel.setText(routine.getName());

            // Limpiar el HBox antes de añadir nuevos días
            RoutineDaysList.getChildren().clear();

            for (String day : days) {
                Button dayButton = new Button(day);
                dayButton.getStyleClass().add("day-button"); // Añadir clase CSS si es necesario


                // Aquí puedes añadir un evento al botón si lo deseas
                dayButton.setOnAction(event -> {
                    try {
                        // System.out.println("Día seleccionado: " + day);
                        selectDayRoutine(day); // Usar el texto del botón como el día
                    } catch (Exception e) {
                        handleException(new RoutineDaySelectorException("Error al seleccionar el día: " + day, e));
                    }
                });

                // Añadir el botón al HBox
                RoutineDaysList.getChildren().add(dayButton);
            }
        } catch (Exception e){
            throw new RoutineDaySelectorException("Error al establecer los días de la rutina.", e);
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
            handleException(new RoutineDaySelectorException("Error al cargar la vista para introducir los días.", e));
        }
    }

    private void handleException(RoutineDaySelectorException e) {
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
