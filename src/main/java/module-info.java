module com.example.gearfit {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;

    opens com.example.gearfit to javafx.fxml;
    exports com.example.gearfit;
    exports com.example.gearfit.controllers;
    opens com.example.gearfit.controllers to javafx.fxml;
}