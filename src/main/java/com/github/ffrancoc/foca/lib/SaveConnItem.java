package com.github.ffrancoc.foca.lib;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SaveConnItem extends HBox {
    private Label connName;
    private String host;
    private String port;
    private String user;
    private String password;
    private String database;

    public Label getConnName() {
        return connName;
    }

    public void setConnName(Label connName) {
        this.connName = connName;
    }

    public void setConnNameStr(String connName) {
        this.connName.setText(connName);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public SaveConnItem(String connName, String host, String port, String user, String password, String database) {
        super();
        this.connName = new Label(connName);
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        init();
    }

    private void init() {
        connName.setGraphic(IconHelper.icon("bi-hdd-stack-fill", Color.DODGERBLUE, 20));
        getChildren().add(connName);
    }

    @Override
    public String toString() {
        return "Connection: "+connName.getText()+" Host: "+host+" Port: "+port+" User: "+user+" Password: "+password+" Database: "+database;
    }
}
