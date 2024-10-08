package com.example.gearfit.controllers;

import com.example.gearfit.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

        // Validar las entradas
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor, completa ambos campos.");
        } else {
            if (authenticate(email, password)) {
                loadMainView(event);
            } else {
                showAlert("Error", "Credenciales incorrectas.");
            }
        }
    }
    private void loadMainView(ActionEvent event) {
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("/com/example/gearfit/MainView.fxml"));
            Scene mainScene = new Scene(mainView);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Función para comprobar el email y la contraseña en la base de datos
    private boolean authenticate(String nombre, String password) {
        return usuarioDAO.verificarContrasena(nombre,password);
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
