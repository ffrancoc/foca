package com.github.ffrancoc.foca.task;

import com.github.ffrancoc.foca.db.Conexion;
import com.github.ffrancoc.foca.lib.EntityObject;
import com.github.ffrancoc.foca.model.TableInfo;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.util.ArrayList;

public class AsyncSidebar extends Task<Void> {
    private Connection conn;
    private VBox sidebar;

    public AsyncSidebar(Connection conn, VBox sidebar) {
        this.conn = conn;
        this.sidebar = sidebar;
    }

    // Funcion para cargar informacion de entiaddes al listview del sidebar
    @Override
    protected Void call() throws Exception {
        ArrayList<TableInfo> tables = Conexion.getTableNames(conn);
        tables.forEach(table -> {
                load(new EntityObject(table.getName(), "bi-table", table.getCount(), Color.DODGERBLUE));
        });


        ArrayList<TableInfo> views = Conexion.getViewNames(conn);
        views.forEach(view -> {
            load(new EntityObject(view.getName(), "bi-table", view.getCount(), Color.web("#0c5ba0")));
        });

        return null;
    }

    // Funcion para actualiza informacion del sidebar
    private void load(HBox row) {
        Platform.runLater(() -> {
            ListView listView = (ListView) sidebar.getChildren().get(1);
            listView.getItems().add(row);

            Label status = (Label) ((HBox) sidebar.getChildren().get(0)).getChildren().get(0);
            status.setText("Entities("+listView.getItems().size()+")");
        });
    }
}
