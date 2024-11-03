package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML
    private Pane mainVBoxWindows; // Asegúrate de que esto esté vinculado al Pane correspondiente en tu FXML


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
            SessionManager.logOut(); // limpia la sesion del usuario
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUserSettings(ActionEvent event) {
        try {
            // Cargar la vista de ajustes de usuario
            Parent userSettingsView = FXMLLoader.load(getClass().getResource("/com/example/gearfit/UserSettings.fxml"));
            mainVBoxWindows.getChildren().clear(); // Limpiar el contenido anterior
            mainVBoxWindows.getChildren().add(userSettingsView); // Añadir la nueva vista
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleWelcomeGuide(ActionEvent event) {
        try {
            // Cargar la vista de información de bienvenida de usuario
            Parent welcomeGuideView = FXMLLoader.load(getClass().getResource("/com/example/gearfit/WelcomeGuide.fxml"));
            mainVBoxWindows.getChildren().clear(); // Limpiar el contenido anterior
            mainVBoxWindows.getChildren().add(welcomeGuideView); // Añadir la nueva vista
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
