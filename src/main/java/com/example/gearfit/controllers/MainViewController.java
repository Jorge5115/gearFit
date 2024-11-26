package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.exceptions.ViewLoadException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    private Pane mainVBoxWindows;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadView("/com/example/gearfit/WelcomeGuide.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    public void logOutSession(ActionEvent event) {
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("/com/example/gearfit/UserAuth.fxml"));

            // Crear la escena y establecer el fondo como transparente
            Scene mainScene = new Scene(mainView);
            mainScene.setFill(Color.TRANSPARENT); // Establecer fondo transparente

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();

            SessionManager.logOut(); // Limpia la sesión del usuario
        } catch (IOException e) {
            handleViewLoadException(new ViewLoadException("Error al cargar la vista de autenticación", e));
        }
    }

    @FXML
    private void handleUserSettings(ActionEvent event) {
        try {
            loadView("/com/example/gearfit/UserSettings.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    @FXML
    private void handleWelcomeGuide(ActionEvent event) {
        try {
            loadView("/com/example/gearfit/WelcomeGuide.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    @FXML
    private void handleRoutineSelector(ActionEvent event) {
        try {
            loadView("/com/example/gearfit/RoutineSelector.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    /**
     * Carga una vista y la añade al panel principal.
     */
    private void loadView(String resourcePath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(resourcePath));
            mainVBoxWindows.getChildren().clear();
            mainVBoxWindows.getChildren().add(view);
        } catch (IOException e) {
            throw new ViewLoadException("Error al cargar la vista: " + resourcePath, e);
        }
    }

    /**
     * Maneja las excepciones relacionadas con la carga de vistas.
     */
    private void handleViewLoadException(ViewLoadException e) {
        e.printStackTrace();
        showErrorAlert("Error", "No se pudo cargar la vista.", e.getMessage());
    }

    /**
     * Muestra un mensaje de error en una alerta.
     */
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}