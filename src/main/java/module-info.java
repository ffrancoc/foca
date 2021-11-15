module com.github.ffrancoc.foca {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.fxmisc.richtext;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.bootstrapicons;

    requires java.sql;
    requires org.mariadb.jdbc;
    requires com.google.gson;
    requires reactfx;

    opens com.github.ffrancoc.foca to javafx.fxml;
    opens com.github.ffrancoc.foca.model;
    exports com.github.ffrancoc.foca;
    exports com.github.ffrancoc.foca.model;
}