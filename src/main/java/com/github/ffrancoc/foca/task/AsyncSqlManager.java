package com.github.ffrancoc.foca.task;

import com.github.ffrancoc.foca.db.Conexion;
import com.github.ffrancoc.foca.model.QueryData;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.*;

import java.sql.Connection;
import java.util.ArrayList;

public class AsyncSqlManager extends Task<Void> {
    private Connection conn;
    private String sqlQuery;
    private Tab tab;
    private Label resultInfo;

    public AsyncSqlManager(Connection conn, String sqlQuery, Tab tab, Label resultInfo) {
        this.conn = conn;
        this.sqlQuery = sqlQuery;
        this.tab = tab;
        this.resultInfo = resultInfo;
    }

    // Funcion para cargar informacion de la consulta ejecutada a una tabla
    @Override
    protected Void call() throws Exception {
        QueryData queryData = Conexion.executeQuery(conn, sqlQuery);
        initTable(queryData.getColumns(), queryData.getRows());

        return null;
    }

    // Actualizar informacion de la tabla
    private void updateData(TableView tableView) {
        Platform.runLater(() -> {
            tab.setContent(tableView);
            resultInfo.setText("col: " + tableView.getColumns().size() + " | row: " + tableView.getItems().size());
        });
    }

    // Creacion de la tabla
    private void initTable(ArrayList<String> columns, ArrayList<ArrayList<String>> rows) {
        TableView tableView = new TableView();
        
        for (int c = 0; c < columns.size(); c++) {
            TableColumn<ArrayList<String>, String> tc = new TableColumn(columns.get(c));
            tc.setReorderable(false);
            int finalC = c;
            tc.setCellValueFactory(data -> {
                return new SimpleStringProperty(data.getValue().get(finalC));
            });
            tableView.getColumns().add(tc);
        }

        rows.forEach(row -> {
            tableView.getItems().add(row);
        });

        updateData(tableView);
    }
}
