package com.github.ffrancoc.foca.model;

public class TableInfo {
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

    private String name;
    private int count;


    public TableInfo(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
