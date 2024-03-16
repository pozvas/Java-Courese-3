module com.example.arrow {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.arrow to javafx.fxml;
    exports com.example.arrow;
}