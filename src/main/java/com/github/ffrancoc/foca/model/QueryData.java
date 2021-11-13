package com.github.ffrancoc.foca.model;

import java.util.ArrayList;

public class QueryData {
    private ArrayList<String> columns;
    private ArrayList<ArrayList<String>> rows;

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

    public QueryData(ArrayList<String> columns, ArrayList<ArrayList<String>> rows) {
        this.columns = columns;
        this.rows = rows;
    }
}
