package com.example.gearfit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void pressSignInButton(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validar las entradas
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor, completa ambos campos.");
        } else {
            if (authenticate(email, password)) {
                showAlert("Éxito", "Inicio de sesión exitoso.");

            } else {
                showAlert("Error", "Credenciales incorrectas.");
            }
        }
    }

    // Función para comprobar el email y la contraseña en la base de datos
    private boolean authenticate(String email, String password) {
        // Aquí irá la lógica para autenticar al usuario (verificar las credenciales con una base de datos)

        return email.equals("usuario@gmail.com") && password.equals("123");
    }

    // Función para mostrar una alerta con un mensaje personalizado
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
