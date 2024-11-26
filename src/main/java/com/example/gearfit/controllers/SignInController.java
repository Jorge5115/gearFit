package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.exceptions.UserAuthenticationException;
import com.example.gearfit.exceptions.UserRegistrationException;
import com.example.gearfit.models.User;
import com.example.gearfit.repositories.UserDAO;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.sql.SQLException;
import java.io.IOException;


public class SignInController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private UserDAO usuarioDAO = new UserDAO();

    @FXML
    private void pressSignInButton(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor, completa ambos campos.");
        } else {
            try {
                User authenticatedUser = authenticate(email, password);
                if (authenticatedUser != null) {
                    // Establecer el usuario en la sesión
                    SessionManager.setCurrentUser(authenticatedUser);
                    loadMainView(event);
                }
            } catch (UserAuthenticationException e) {
                // Mostrar el mensaje de error personalizado
                showAlert("Error", e.getMessage());
            }
        }
    }

    private void loadMainView(ActionEvent event) {
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("/com/example/gearfit/MainView.fxml"));

            // Crear la escena
            Scene mainScene = new Scene(mainView);
            mainScene.setFill(Color.TRANSPARENT); // Establecer fondo transparente

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);

            // Crear la animación de desvanecimiento
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), mainView);
            fadeTransition.setFromValue(0);  // Comienza desde transparente
            fadeTransition.setToValue(1);    // Hasta opaco (completamente visible)
            fadeTransition.play();           // Reproducir la animación

            stage.show();  // Mostrar la ventana

        } catch (IOException e) {
            showAlert("Error", e.getMessage());
        }
    }


    // Función para comprobar el email y la contraseña en la base de datos
    private User authenticate(String email, String password) {
        try {
            return usuarioDAO.getUserByEmailAndPassword(email, password);
        } catch (Exception e) {
            throw new UserAuthenticationException("Error de base de datos al autenticar al usuario.", e);
        }
    }


    // Función para mostrar una alerta con un mensaje personalizado
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closeApplication(ActionEvent event) {
        // Obtiene el Stage actual y lo cierra
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
