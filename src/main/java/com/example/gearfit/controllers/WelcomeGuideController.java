package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WelcomeGuideController {

    @FXML
    private Label usernameLabel;

    private User currentUser; // Esto debería contener el usuario actualmente autenticado

    @FXML
    public void initialize() {
        // Inicializa currentUser desde la sesión o el contexto
        currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            usernameLabel.setText("¡Hola " + currentUser.getUsername() + "!");
        } else {
            // Maneja el caso en que no hay un usuario autenticado
            showAlert("Error", "No se encontró el usuario autenticado.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
