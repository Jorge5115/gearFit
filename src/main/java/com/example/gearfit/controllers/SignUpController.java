package com.example.gearfit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void pressSignUpButton(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validar las entradas
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor, completa todos los campos.");
        } else if (!isValidUsername(username)) {
            showAlert("Error", "La contraseña debe tener al menos 3 caracteres.");
        } else if (!isValidEmail(email)) {
            showAlert("Error", "El formato del correo electrónico no es válido.");
        } else if (!isValidPassword(password)) {
            showAlert("Error", "La contraseña debe tener al menos 8 caracteres.");
        } else {
            // Meter la lógica para registrar al usuario (que no se repita en la base de datos, etc)
            if (registerUser(username, email, password)) {
                showAlert("Éxito", "Registro exitoso.");
                // Redirigir a la página de inicio de sesión o directamente meter al usuario en el MainView
            } else {
                showAlert("Error", "Error al registrar el usuario.");
            }
        }
    }

    // Función para validar el nombre de usuario
    private boolean isValidUsername(String username) {
        return username.length() >= 3;
    }

    // Función para validar el formato del correo electrónico
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Función para validar la contraseña (mínimo 8 caracteres en este caso)
    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    // Función para registrar al usuario en la base de datos
    private boolean registerUser(String username, String email, String password) {
        // Lógica para registrar al usuario (conectar a base de datos, API, etc.)
        // Actualmente, es solo un ejemplo que siempre retorna verdadero
        return true;
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
