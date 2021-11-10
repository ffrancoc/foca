module com.github.ffrancoc.foca {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens com.github.ffrancoc.foca to javafx.fxml;
    exports com.github.ffrancoc.foca;
}