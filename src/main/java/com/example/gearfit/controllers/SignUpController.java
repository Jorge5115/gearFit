package com.example.gearfit.controllers;

import com.example.gearfit.User;
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
import java.sql.SQLException;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    private UserDAO usuarioDAO = new UserDAO();


    @FXML
    private void pressSignUpButton(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // LO COMENTO PORQUE SE HACEN LAS COMPROBACIONES EN LA CLASE USUARIO!!!!!!
        /**if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor, completa todos los campos.");
        } else if (!isValidUsername(username)) {
            showAlert("Error", "La contraseña debe tener al menos 3 caracteres.");
        } else if (!isValidEmail(email)) {
            showAlert("Error", "El formato del correo electrónico no es válido.");
        } else if (!isValidPassword(password)) {
            showAlert("Error", "La contraseña debe tener al menos 8 caracteres.");
        } else {**/
            // Meter la lógica para registrar al usuario (que no se repita en la base de datos, etc)
        if (registerUser(username, email, password)) {
            // showAlert("Éxito", "Registro exitoso.");
            loadMainView(event);
        } else {
            showAlert("Error", "Error al registrar el usuario.");
        }
    }

    // Método para cargar la vista MainView.fxml
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
    private boolean registerUser(String username, String email, String password) {
        try {
            User usuario = new User();
            usuario.setNombre(username);  // Validación automática en setter
            usuario.setEmail(email);      // Validación automática en setter

            usuarioDAO.addUser(usuario, password);
            return true;
        } catch (IllegalArgumentException e) {
            // Captura de las excepciones lanzadas por validaciones en setters, si las has incluido
            showAlert("Error", e.getMessage());
            return false;
        }
    }

    // Función para validar el nombre de usuario
    private boolean isValidUsername(String username) {
        return username.length() >= 3;
    }

    // Función para validar el formato del correo electrónico
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    // Función para validar la contraseña (mínimo 8 caracteres en este caso)
    private boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    // Función para registrar al usuario en la base de datos


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
