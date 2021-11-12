package com.github.ffrancoc.foca.model;

import java.sql.Connection;

public class ConnectionObject {
    private Connection conn;
    private String message;
    private String url;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ConnectionObject() {

    }

    public ConnectionObject(Connection conn, String message) {
        this.conn = conn;
        this.message = message;
    }
}
