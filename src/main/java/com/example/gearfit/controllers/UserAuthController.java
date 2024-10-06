package com.example.gearfit.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserAuthController implements Initializable {

    @FXML
    private VBox vbox;

    private Parent fxml;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fxml = FXMLLoader.load(getClass().getResource("/com/example/gearfit/SignUp.fxml"));
            vbox.getChildren().setAll(fxml);
        } catch (IOException ex) {
            ex.printStackTrace();
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

            }
        });
    }

}
