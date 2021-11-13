package com.github.ffrancoc.foca.lib;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.Timestamp;
import java.util.Date;

public class GlobalMessageItem extends Label {

    public GlobalMessageItem(String message, FontIcon iconMessage) {
        super();
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        setText(message + " "+timestamp.toString());
        iconMessage.setIconSize(16);
        setGraphic(iconMessage);

        getStyleClass().add("sidebar-list-item");
    }
}
