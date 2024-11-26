package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.exceptions.UserAuthenticationException;
import com.example.gearfit.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WelcomeGuideController {

    @FXML
    private Label usernameLabel;

    private User currentUser; // Esto debería contener el usuario actualmente autenticado

    private static final Logger logger = Logger.getLogger(WelcomeGuideController.class.getName());

    @FXML
    public void initialize() {
        try {
            // Inicializa currentUser desde la sesión o el contexto
            currentUser = SessionManager.getCurrentUser();

            if (currentUser != null) {
                usernameLabel.setText("¡Hola " + currentUser.getUsername() + "!");
            } else {
                logger.log(Level.WARNING, "No se encontró el usuario autenticado.");
                throw new UserAuthenticationException("No se encontró el usuario autenticado.");
            }
        } catch (UserAuthenticationException e) {
            showAlert("Error", "No se encontró el usuario autenticado. Por favor, inicia sesión.");
            logger.log(Level.SEVERE, "Error de autenticación: " + e.getMessage(), e);
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
