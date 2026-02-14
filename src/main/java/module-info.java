module univr.musica {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires jdk.compiler;
    requires java.desktop;
    requires javafx.base;
    requires javafx.media;


    opens univr.musica to javafx.fxml;
    exports univr.musica;
    exports univr.musica.model;
    exports univr.musica.view;
    exports univr.musica.controller.Admin;
    opens univr.musica.controller.Admin to javafx.fxml;
    exports univr.musica.controller.User;
    opens univr.musica.controller.User to javafx.fxml;
    exports univr.musica.controller;
    opens univr.musica.controller to javafx.fxml;
}