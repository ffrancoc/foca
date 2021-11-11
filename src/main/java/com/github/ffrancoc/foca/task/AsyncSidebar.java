package com.github.ffrancoc.foca.task;

import com.github.ffrancoc.foca.lib.Conexion;
import com.github.ffrancoc.foca.model.TableInfo;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Connection;
import java.util.ArrayList;

public class AsyncSidebar extends Task<Void> {
    private Connection conn;
    private VBox sidebar;

    public AsyncSidebar(Connection conn, VBox sidebar) {
        this.conn = conn;
        this.sidebar = sidebar;
    }

    @Override
    protected Void call() throws Exception {
        ArrayList<TableInfo> tables = Conexion.getTableNames(conn);
        tables.forEach(table -> {
                load(itemData(table.getName(), "bi-table", table.getCount(), Color.DODGERBLUE));
        });


        ArrayList<TableInfo> views = Conexion.getViewNames(conn);
        views.forEach(view -> {
            load(itemData(view.getName(), "bi-table", view.getCount(), Color.web("#0c5ba0")));
        });

        return null;
    }

    private void load(HBox row) {
        Platform.runLater(() -> {
            ListView listView = (ListView) sidebar.getChildren().get(1);
            listView.getItems().add(row);

            Label status = (Label) sidebar.getChildren().get(2);
            status.setText("Objects("+listView.getItems().size()+")");
        });
    }

    private HBox itemData(String name, String iconName, int count, Color color) {
        HBox hbItem = new HBox();
        hbItem.setSpacing(5);

        Label title = new Label(name);
        FontIcon fontIcon = new FontIcon(iconName);
        fontIcon.setIconColor(color);
        title.setGraphic(fontIcon);

        Label colsNumber = new Label(""+count);
        colsNumber.getStyleClass().add("data-pill");


        Pane separator = new Pane();

        hbItem.getChildren().addAll(title, separator, colsNumber);
        HBox.setHgrow(separator, Priority.ALWAYS);

        return hbItem;
    }
}
