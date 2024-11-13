package com.example.gearfit.controllers;

import com.example.gearfit.connections.EmailService;
import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.exceptions.UserRegistrationException;
import com.example.gearfit.models.User;
import com.example.gearfit.repositories.UserDAO;
import javafx.animation.FadeTransition;
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
import javafx.util.Duration;


import java.io.IOException;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private UserDAO usernameDAO = new UserDAO();

    private EmailService emailService = new EmailService();;


    @FXML
    private void pressSignUpButton(ActionEvent event){
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        User registeredUser  = registerUser(username,email,password);
        if (registeredUser != null) {
            emailService.sendWelcomeEmail(email);
            SessionManager.setCurrentUser(registeredUser);
            loadMainView(event);
        } else {
            System.out.println("Excepcion!!!!!!!!!!!!");
            //throw new UserRegistrationException("El usuario no se ha podido registrar");
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
            e.printStackTrace();
        }
    }

    private User registerUser(String username, String email, String password) {
        try {
            if (usernameDAO.usernameExists(username)) {
                showAlert("Error", "El nombre de usuario ya está en uso.");
                return null;
            }

            if (usernameDAO.emailExists(email)) {
                showAlert("Error", "El correo electrónico ya está en uso.");
                return null;
            }

            if (password.length() < 8) {
                showAlert("Error", "La contraseña debe tener al menos 8 caracteres.");
                return null;
            }

            // Si el nombre de usuario y el correo electrónico son únicos, se procede a crear el usuario.
            User user = new User(username, email);
            usernameDAO.addUser(user, password);
            return user;

        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
            return null;
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
