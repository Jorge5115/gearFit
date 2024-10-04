package com.example.gearfit;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserAuthController implements Initializable {

    @FXML
    private VBox vbox;

    @FXML
    private TextField usernameField; // Campo para ingresar el nombre de usuario
    @FXML
    private PasswordField passwordField; // Campo para ingresar la contraseña

    private Parent fxml;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cargar la vista de SignUp al inicio
        if (vbox != null) {
            loadView("SignUp.fxml", 0); // Cargar la vista inicial
        } else {
            System.out.println("VBox no está inicializado en initialize"); //No entiendo porque no se inicializa
        }
    }
    // Método para cargar una vista
    public void loadView(String fxmlFile, double translateX) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(fxmlFile));

            if (vbox != null) {
                // Crear la transición
                TranslateTransition transition = new TranslateTransition(Duration.millis(300), vbox);
                transition.setFromX(0);
                transition.setToX(translateX);
                transition.setOnFinished(event -> {
                    vbox.getChildren().clear(); // Limpiar el contenido anterior
                    vbox.getChildren().add(fxml); // Añadir la nueva vista
                });

                transition.play(); // Reproducir la transición
            } else {
                System.out.println("VBox no está inicializado"); // Mensaje de depuración
            }
        } catch (IOException ex) {
            ex.printStackTrace(); // Manejo de errores
        }
    }

    @FXML
    public void open_signIn(ActionEvent event) {
        // Cargar la vista de SignIn y mover el VBox a la derecha
        loadView("SignIn.fxml", 480); // Aquí puedes poner 0 para no mover, pero si deseas mover, usa un valor negativo
    }

    @FXML
    public void open_signUp(ActionEvent event) {
        // Cargar la vista de SignUp y mover el VBox a la izquierda
        loadView("SignUp.fxml", -30); // Ajusta este valor según el ancho que desees mover
    }


    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText(); // Obtener el nombre de usuario
        String password = passwordField.getText(); // Obtener la contraseña

        // Aquí puedes agregar la lógica para validar los datos de inicio de sesión
        if (validateCredentials(username, password)) { // Validar credenciales
            loadView("MainView.fxml",0); // Cambiar a la vista del dashboard
        } else {
            // Manejo de error: mostrar mensaje de que las credenciales son incorrectas
            System.out.println("Credenciales incorrectas"); // Puedes mostrar un mensaje en la UI
        }
    }

    private boolean validateCredentials(String username, String password) {
        // Aquí puedes implementar tu lógica de validación
        // Este es solo un ejemplo sencillo
        return username.equals("usuario") && password.equals("contraseña");
    }
}
