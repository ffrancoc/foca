package com.github.ffrancoc.foca.model;

import java.util.ArrayList;

public class TableInfo {
    private String name;
    private int count;
    private ArrayList<ColumnInfo> columns;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<ColumnInfo> columns) {
        this.columns = columns;
    }

    public TableInfo(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
