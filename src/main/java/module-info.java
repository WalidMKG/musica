module univr.musica {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires jdk.compiler;
    requires java.desktop;


    opens univr.musica to javafx.fxml;
    exports univr.musica;
    exports univr.musica.controller;
    exports univr.musica.model;
    exports univr.musica.view;
    opens univr.musica.controller to javafx.fxml;
}