package com.example.gearfit.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAuthController implements Initializable {

    @FXML
    private VBox vbox;

    private Parent fxml;

    private static final Logger LOGGER = Logger.getLogger(UserAuthController.class.getName());


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fxml = FXMLLoader.load(getClass().getResource("/com/example/gearfit/SignUp.fxml"));
            vbox.getChildren().setAll(fxml);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error al cargar el archivo FXML: /com/example/gearfit/SignUp.fxml", ex);
            showAlert("Error", "Hubo un problema al cargar la vista de registro. Intenta nuevamente.");
        }
    }

    public void open_signIn(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), vbox);
        transition.setToX(vbox.getLayoutX() * 21.75);
        transition.play();

        transition.setOnFinished((e) -> {
            try {
                fxml = FXMLLoader.load(getClass().getResource("/com/example/gearfit/SignIn.fxml"));
                vbox.getChildren().removeAll();
                vbox.getChildren().setAll(fxml);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error al cargar el archivo FXML: /com/example/gearfit/SignIn.fxml", ex);
                showAlert("Error", "Hubo un problema al cargar la vista de inicio de sesión. Intenta nuevamente.");
            }
        });
    }

    public void open_signUp(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), vbox);
        transition.setToX(0);
        transition.play();

        transition.setOnFinished((e) -> {
            try {
                fxml = FXMLLoader.load(getClass().getResource("/com/example/gearfit/SignUp.fxml"));
                vbox.getChildren().removeAll();
                vbox.getChildren().setAll(fxml);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error al cargar el archivo FXML: /com/example/gearfit/SignUp.fxml", ex);
                showAlert("Error", "Hubo un problema al cargar la vista de registro. Intenta nuevamente.");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
