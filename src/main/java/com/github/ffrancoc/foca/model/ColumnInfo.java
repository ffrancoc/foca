package com.github.ffrancoc.foca.model;

public class ColumnInfo {
    private String name;
    private String type;
    private boolean pk;
    private FKInfo fk;

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

    public FKInfo getFk() {
        return fk;
    }

    public void setFk(FKInfo fk) {
        this.fk = fk;
    }

    public ColumnInfo(){

    }

    public ColumnInfo(String name, String type, boolean pk, FKInfo fkInfo) {
        this.name = name;
        this.type = type;
        this.pk = pk;
        this.fk = fkInfo;
    }
}
