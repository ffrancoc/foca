package com.github.ffrancoc.foca.model;

public class FKInfo {
    private String columnName;
    private String tableParent;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTableParent() {
        return tableParent;
    }

    public void setTableParent(String tableParent) {
        this.tableParent = tableParent;
    }

    public FKInfo() {
        this.columnName = "";
        this.tableParent = "";
    }

    public FKInfo(String columnName, String tableParent) {
        this.columnName = columnName;
        this.tableParent = tableParent;
    }
}
