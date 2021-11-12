package com.github.ffrancoc.foca.lib;

import com.github.ffrancoc.foca.model.ColumnInfo;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;

public class SidebarObject extends HBox {
    private String name;
    private String iconName;
    private Label colsNumber;
    private int count;
    private Color color;
    private ArrayList<ColumnInfo> columns;

    public String getName() {
        return name;
    }

    public ArrayList<ColumnInfo> getColumns() {
        return columns;
    }

    public SidebarObject(String name, String iconName, int count, Color color, ArrayList<ColumnInfo> columns) {
        super();
        this.name = name;
        this.iconName = iconName;
        this.count = count;
        this.color = color;
        this.columns = columns;
        init();
    }

    private void init() {
        setSpacing(5);

        Label title = new Label(name);
        FontIcon fontIcon = new FontIcon(iconName);
        fontIcon.setIconColor(color);
        title.setGraphic(fontIcon);

        colsNumber = new Label(""+count);
        colsNumber.getStyleClass().add("data-pill");

        //Label colsNumber = new Label(""+count);
        //colsNumber.getStyleClass().add("data-pill");


        Pane separator = new Pane();

        getChildren().addAll(title, separator, colsNumber);
        HBox.setHgrow(separator, Priority.ALWAYS);
    }
}
