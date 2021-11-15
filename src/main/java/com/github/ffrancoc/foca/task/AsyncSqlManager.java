package com.github.ffrancoc.foca.task;

import com.github.ffrancoc.foca.db.Conexion;
import com.github.ffrancoc.foca.lib.GlobalMessageItem;
import com.github.ffrancoc.foca.lib.IconHelper;
import com.github.ffrancoc.foca.lib.TabManager;
import com.github.ffrancoc.foca.model.QueryData;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class AsyncSqlManager extends Task<Void> {
    private Connection conn;
    private String sqlQuery;
    private TabPane tpResult;
    private Label resultInfo;
    private TableView tvGlobalMsgList;

    public AsyncSqlManager(Connection conn, String sqlQuery, TabPane tbResult, Label resultInfo, TableView tvGlobalMsgList) {
        this.conn = conn;
        this.sqlQuery = sqlQuery;
        this.tpResult = tbResult;
        this.resultInfo = resultInfo;
        this.tvGlobalMsgList = tvGlobalMsgList;
    }


    // Funcion para cargar informacion de la consulta ejecutada a una tabla
    @Override
    protected Void call() throws Exception {
        QueryData queryData = Conexion.executeQuery(conn, sqlQuery);
        if (queryData.getMessage().isEmpty()) {
            initTable(queryData.getColumns(), queryData.getRows(), queryData.getTableName());
        }else {
            updateMessage(tvGlobalMsgList, queryData.getMessage());
        }


        return null;
    }

    // Actualizar informacion de la tabla
    private void updateMessage(TableView tvGlobalMsgList, String message) {
        Platform.runLater(() -> {
            tpResult.getSelectionModel().select(0);
            tvGlobalMsgList.getItems().add(0, new GlobalMessageItem(message, new Timestamp(new Date().getTime()).toString()));
            //GlobalMessageItem globalMsgItem = new GlobalMessageItem(msg, IconHelper.icon( "bi-exclamation-octagon-fill", Color.RED));
            //lvGlobalMsgList.getItems().add(0, globalMsgItem);
        });
    }


    private void updateData(TableView tableView, String tableName) {
        Platform.runLater(() -> {
            Tab tab = TabManager.addTabResult(tpResult, tableName, IconHelper.icon("bi-table", Color.DODGERBLUE));
            tab.setContent(tableView);
            tab.setClosable(true);
            resultInfo.setText("col: " + tableView.getColumns().size() + " | row: " + tableView.getItems().size());
        });
    }

    // Creacion de la tabla
    private void initTable(ArrayList<String> columns, ArrayList<ArrayList<String>> rows, String tableName) {
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

        if (rows.size() == 0) {
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }

        updateData(tableView, tableName);
    }
}
