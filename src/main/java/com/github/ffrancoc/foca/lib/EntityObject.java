package com.github.ffrancoc.foca.lib;

import com.github.ffrancoc.foca.model.ColumnInfo;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;

public class EntityObject extends HBox {
    private Label title;
    private String name;
    private String iconName;
    private Label colsNumber;
    private int count;
    private Color color;
    private ArrayList<ColumnInfo> columns;

    public Label getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<ColumnInfo> columns) {
        this.columns = columns;
    }

    public EntityObject(String name, String iconName, int count, Color color) {
        super();
        this.name = name;
        this.iconName = iconName;
        this.count = count;
        this.color = color;
        init();
    }

    private void init() {
        setSpacing(5);

        title = new Label(name);
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
