package com.github.ffrancoc.foca.task;

import com.github.ffrancoc.foca.lib.Conexion;
import com.github.ffrancoc.foca.model.ColumnInfo;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.util.ArrayList;

public class AsyncColumnInfo extends Task<ArrayList<ColumnInfo>> {
    private Connection conn;
    private String tableName;

    public AsyncColumnInfo(Connection conn, String tableName) {
        this.conn = conn;
        this.tableName = tableName;
    }

    // Funcion que retorna un array con informacion de las columnas
    @Override
    protected ArrayList<ColumnInfo> call() throws Exception {
        ArrayList<ColumnInfo> columns = Conexion.columnData(conn, tableName);
        return columns;
    }
}
