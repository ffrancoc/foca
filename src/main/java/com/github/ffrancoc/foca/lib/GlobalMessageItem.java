package com.github.ffrancoc.foca.lib;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GlobalMessageItem{
    private StringProperty message = new SimpleStringProperty("");
    private StringProperty time = new SimpleStringProperty("");

    public String getMessage() {
        return message.get();
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public GlobalMessageItem(String message, String time) {
        setMessage(message);
        setTime(time);
    }
}
