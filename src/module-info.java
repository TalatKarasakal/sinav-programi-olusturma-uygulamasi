module scheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;
    requires com.github.librepdf.openpdf;

    // PropertyValueFactory yansıma erişimi için
    opens scheduler.model to javafx.base, javafx.controls;
    opens scheduler.ui to javafx.fxml;
    exports scheduler.ui;
}
