module com.example.tradingtool {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;


    opens com.example.tradingtool to javafx.fxml;
    exports com.example.tradingtool;
}