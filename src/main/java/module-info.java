module univr.musica {
    requires javafx.controls;
    requires javafx.fxml;


    opens univr.musica to javafx.fxml;
    exports univr.musica;
    exports univr.musica.controller;
    opens univr.musica.controller to javafx.fxml;
}