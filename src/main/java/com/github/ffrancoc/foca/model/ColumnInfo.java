package com.github.ffrancoc.foca.model;

public class ColumnInfo {
    private String name;
    private String type;
    private boolean pk;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }

    public ColumnInfo(){

    }

    public ColumnInfo(String name, String type, boolean pk) {
        this.name = name;
        this.type = type;
        this.pk = pk;
    }
}
