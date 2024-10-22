package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.models.User;
import com.example.gearfit.repositories.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

public class UserSettingsController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField heightField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();
    private User currentUser; // Esto debería contener el usuario actualmente autenticado

    @FXML
    public void initialize() {
        // Inicializa currentUser desde la sesión o el contexto
        currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Usuario autenticado: " + currentUser.getUsername()); // Depuración
            System.out.println("Peso: " + currentUser.getWeight()); // Depuración
            System.out.println("Altura: " + currentUser.getHeight()); // Depuración
            // Rellena los campos con la información del usuario
            usernameField.setText(currentUser.getUsername());
            weightField.setText(String.valueOf(currentUser.getWeight()));
            heightField.setText(String.valueOf(currentUser.getHeight()));
        } else {
            // Maneja el caso en que no hay un usuario autenticado
            showAlert("Error", "No se encontró el usuario autenticado.");
        }
    }
    @FXML
    public void handleSaveChanges(ActionEvent event) {
        String username = usernameField.getText().trim();
        String weightStr = weightField.getText().trim();
        String heightStr = heightField.getText().trim();
        String password = passwordField.getText().trim(); // Obtener la nueva contraseña si es que se desea cambiar


        double weight;
        double height;

        try {
            weight = Double.parseDouble(weightStr);
            height = Double.parseDouble(heightStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Por favor, introduce un peso y altura válidos.");
            return;
        }

        // Actualizar el usuario en la base de datos
        try {
            currentUser.setUsername(username);
            currentUser.setWeight(weight);
            currentUser.setHeight(height);

            // Si hay una nueva contraseña, hashearla y actualizar el usuario
            String hashedPassword = null;
            if (!password.isEmpty()) {
                hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            }

            userDAO.updateUser(currentUser,hashedPassword);
            showAlert("Éxito", "Cambios guardados exitosamente.");

            // Actualizar directamente los campos de la interfaz
            usernameField.setText(currentUser.getUsername());
            weightField.setText(String.valueOf(currentUser.getWeight()));
            heightField.setText(String.valueOf(currentUser.getHeight()));
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error al guardar los cambios: " + e.getMessage());
        }
    }

    // Método para mostrar una alerta
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}