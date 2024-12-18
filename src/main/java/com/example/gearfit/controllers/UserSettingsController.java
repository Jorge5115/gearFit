package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.exceptions.InvalidPasswordException;
import com.example.gearfit.exceptions.UserDeleteException;
import com.example.gearfit.models.User;
import com.example.gearfit.repositories.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class UserSettingsController {

    @FXML
    private Label userIcon; // Este Label será el icono del usuario

    @FXML
    private Label emailLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField heightField;

    @FXML
    public TextField caloriesField;


    private UserDAO userDAO = new UserDAO();

    private User currentUser;

    @FXML
    public void initialize() {
        // Inicializa currentUser desde la sesión o el contexto
        currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            createUserIcon(currentUser.getUsername());
            emailLabel.setText(currentUser.getEmail());
            // Rellena los campos con la información del usuario
            usernameField.setText(currentUser.getUsername());
            heightField.setText(String.valueOf(currentUser.getHeight()));
            weightField.setText(String.valueOf(currentUser.getWeight()));
            caloriesField.setText(String.valueOf(currentUser.getCalories()));

        } else {
            // Maneja el caso en que no hay un usuario autenticado
            showAlert("Error", "No se encontró el usuario autenticado.");
        }
    }

    private void createUserIcon(String username) {
        // Obtiene la inicial para poner en el Label
        String initial = username.substring(0, 1).toUpperCase();
        userIcon.setText(initial);
    }

    @FXML
    public void handleSaveChanges(ActionEvent event) {
        String username = usernameField.getText().trim();
        String weightString = weightField.getText().trim();
        String heightString = heightField.getText().trim();
        String caloriesString = caloriesField.getText().trim();
        String password = passwordField.getText().trim();
        String repeatPassword = repeatPasswordField.getText().trim();

        int height;
        double weight;
        int calories;

        try {
            height = Integer.parseInt(heightString);
            weight = Double.parseDouble(weightString);
            calories = Integer.parseInt(caloriesString);
        } catch (NumberFormatException e) {
            showAlert("Error", "Por favor, introduce un peso y altura válidos.");
            return;
        }

        // Validación de contraseña
        if (!password.isEmpty() || !repeatPassword.isEmpty()) {
            if (password.length() < 8) {
                showAlert("Error", "La contraseña debe tener al menos 8 caracteres.");
                throw new InvalidPasswordException("La contraseña debe tener al menos 8 caracteres.");
            }

            if (!password.equals(repeatPassword)) {
                showAlert("Error", "Las contraseñas no coinciden. Por favor, verifica e intenta nuevamente.");
                throw new InvalidPasswordException("Las contraseñas no coinciden. Por favor verifica e intenta nuevamente.");
            }
        }

        // Actualizar el usuario en la base de datos
        try {
            currentUser.setUsername(username);
            currentUser.setWeight(weight);
            currentUser.setHeight(height);
            currentUser.setCalories(calories);

            // Si hay una nueva contraseña válida, hashearla y actualizar el usuario
            String hashedPassword = null;
            if (!password.isEmpty()) {
                hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            }

            userDAO.updateUser(currentUser, hashedPassword);
            showAlert("Éxito", "Cambios guardados exitosamente.");

            // Actualizar directamente los campos de la interfaz
            usernameField.setText(currentUser.getUsername());
            heightField.setText(String.valueOf(currentUser.getHeight()));
            weightField.setText(String.valueOf(currentUser.getWeight()));
            caloriesField.setText(String.valueOf(currentUser.getCalories()));

            // Limpiar los campos de contraseña después de guardar
            passwordField.clear();
            repeatPasswordField.clear();
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error al guardar los cambios: " + e.getMessage());
        }
    }


    public boolean promptForPassword(ActionEvent event) {
        // Crear el diálogo
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmar Eliminación");
        dialog.setHeaderText("Ingresa tu contraseña para confirmar la eliminación de la cuenta.");

        // Crear un PasswordField
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        // Agregar el PasswordField al diálogo
        VBox vbox = new VBox(passwordField);
        dialog.getDialogPane().setContent(vbox);

        // Agregar botones al diálogo
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Mostrar el diálogo y esperar a que el usuario lo cierre
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String password = passwordField.getText();
                // Aquí puedes verificar la contraseña
                try {
                    boolean isCorrect = userDAO.verifyPassword(SessionManager.getCurrentUser().getEmail(), password);
                    if (isCorrect) {
                        // Lógica para eliminar la cuenta
                        userDAO.deleteUser(currentUser.getId());
                        loadLoginView(event);
                        System.out.println("Cuenta eliminada correctamente.");
                    } else {
                        throw new UserDeleteException("La contraseña es incorrecta.");
                    }
                } catch (UserDeleteException e) {
                    showAlert("Error", e.getMessage());
                } catch (Exception e) {
                    showAlert("Error", "Ocurrió un error inesperado al intentar eliminar la cuenta.");
                }
            }
        });
        return true; // o false dependiendo de la lógica que desees
    }

    @FXML
    public void handleDeleteAccount(ActionEvent event) {
        promptForPassword(event);
    }

    private void loadLoginView(ActionEvent event) {
        try {
            // Cargar la vista de inicio de sesión
            Parent mainView = FXMLLoader.load(getClass().getResource("/com/example/gearfit/UserAuth.fxml"));

            // Obtener la ventana actual desde el evento
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Crear la escena y establecer el fondo como transparente
            Scene mainScene = new Scene(mainView);
            mainScene.setFill(Color.TRANSPARENT); // Establecer fondo transparente

            stage.setScene(mainScene);
            stage.show();
            SessionManager.logOut(); // Limpiar la sesión del usuario
        } catch (IOException e) {
            e.printStackTrace();
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