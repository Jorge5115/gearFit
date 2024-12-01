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
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    private Button selectedButton;

    @FXML
    private Pane mainVBoxWindows;

    @FXML
    private Button userSettingsButton;

    @FXML
    private Button welcomeGuideButton;

    @FXML
    private Button routineSelectorButton;

    @FXML
    private Button nutritionMainButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadView("/com/example/gearfit/WelcomeGuide.fxml");
            // Seleccionar el botón de bienvenida por defecto
            selectedButton = welcomeGuideButton;
            selectedButton.getStyleClass().add("selected-button");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    public void logOutSession(ActionEvent event) {
        try {
            Parent mainView = FXMLLoader.load(getClass().getResource("/com/example/gearfit/UserAuth.fxml"));
            Scene mainScene = new Scene(mainView);
            mainScene.setFill(Color.TRANSPARENT);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(mainScene);
            stage.show();

            SessionManager.logOut();
        } catch (IOException e) {
            handleViewLoadException(new ViewLoadException("Error al cargar la vista de autenticación", e));
        }
    }

    @FXML
    private void handleUserSettings(ActionEvent event) {
        selectButton(userSettingsButton);
        try {
            loadView("/com/example/gearfit/UserSettings.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    @FXML
    private void handleWelcomeGuide(ActionEvent event) {
        selectButton(welcomeGuideButton);
        try {
            loadView("/com/example/gearfit/WelcomeGuide.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    @FXML
    private void handleRoutineSelector(ActionEvent event) {
        selectButton(routineSelectorButton);
        try {
            loadView("/com/example/gearfit/RoutineSelector.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    @FXML
    private void handleNutritionMain(ActionEvent event) {
        selectButton(nutritionMainButton);
        try {
            loadView("/com/example/gearfit/NutritionMain.fxml");
        } catch (ViewLoadException e) {
            handleViewLoadException(e);
        }
    }

    private void selectButton(Button button) {
        // Deseleccionar el botón previamente seleccionado
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("selected-button");
        }
        // Seleccionar el nuevo botón
        selectedButton = button;
        selectedButton.getStyleClass().add("selected-button");
    }

    private void loadView(String resourcePath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(resourcePath));
            mainVBoxWindows.getChildren().clear();
            mainVBoxWindows.getChildren().add(view);
        } catch (IOException e) {
            throw new ViewLoadException("Error al cargar la vista: " + resourcePath, e);
        }
    }

    private void handleViewLoadException(ViewLoadException e) {
        e.printStackTrace();
        showErrorAlert("Error", "No se pudo cargar la vista.", e.getMessage());
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}