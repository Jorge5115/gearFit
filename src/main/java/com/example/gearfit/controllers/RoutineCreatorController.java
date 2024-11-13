package com.example.gearfit.controllers;

import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import com.example.gearfit.connections.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

public class RoutineCreatorController {

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
        System.out.println(userId);
        Routine newRoutine = new Routine(userId, routineName);

        // Guardar la rutina en la base de datos
        boolean isRoutineCreated = RoutineDAO.createRoutine(newRoutine);

        if (isRoutineCreated) {
            System.out.println("Rutina creada con éxito.");
            System.out.println("Nombre de rutina: " + routineName);
            System.out.println("Días seleccionados: " + selectedDays);

            // Aquí meteremos lógica para guardar los días seleccionados (separado en el DAO)

            // Limpiar los campos después de guardar
            routineNameField.clear();
            mondayCheckBox.setSelected(false);
            tuesdayCheckBox.setSelected(false);
            wednesdayCheckBox.setSelected(false);
            thursdayCheckBox.setSelected(false);
            fridayCheckBox.setSelected(false);
            saturdayCheckBox.setSelected(false);
            sundayCheckBox.setSelected(false);
        } else {
            System.out.println("Error al crear la rutina.");
        }
    }

    // Método para cancelar la creación de la rutina
    @FXML
    public void cancelRoutine(ActionEvent event) {
        // Lógica para cancelar (por ejemplo, cerrar la ventana o limpiar campos)
    }
}
