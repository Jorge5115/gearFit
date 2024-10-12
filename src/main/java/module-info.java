module com.example.gearfit {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires java.sql;
    requires jbcrypt;

    opens com.example.gearfit to javafx.fxml;
    exports com.example.gearfit;
    exports com.example.gearfit.controllers;
    opens com.example.gearfit.controllers to javafx.fxml;
    exports com.example.gearfit.models;
    opens com.example.gearfit.models to javafx.fxml;
    exports com.example.gearfit.repositories;
    opens com.example.gearfit.repositories to javafx.fxml;
    exports com.example.gearfit.connections;
    opens com.example.gearfit.connections to javafx.fxml;
}