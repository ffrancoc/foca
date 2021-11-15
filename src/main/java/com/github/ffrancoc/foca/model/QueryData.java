package com.github.ffrancoc.foca.model;

import java.util.ArrayList;

public class QueryData {
    private ArrayList<String> columns;
    private ArrayList<ArrayList<String>> rows;
    private String tableName;
    private String message;

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
    }

    public ArrayList<ArrayList<String>> getRows() {
        return rows;
    }

    public void setRows(ArrayList<ArrayList<String>> rows) {
        this.rows = rows;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public QueryData(ArrayList<String> columns, ArrayList<ArrayList<String>> rows, String tableName, String message) {
        this.columns = columns;
        this.rows = rows;
        this.tableName = tableName;
        this.message = message;
    }
}
