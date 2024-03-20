module com.example.arrow {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.google.gson;


    exports com.example.arrow.server;
    exports com.example.arrow.client;
    opens com.example.arrow.client to javafx.fxml;
    opens com.example.arrow to com.google.gson;
}