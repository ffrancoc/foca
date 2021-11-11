package com.github.ffrancoc.foca.lib;

import com.github.ffrancoc.foca.model.ColumnInfo;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;

public class SidebarObjectDetail extends VBox {
    private ColumnInfo columnInfo;
    private String iconName;
    private Color color;

    public SidebarObjectDetail(ColumnInfo columnInfo, String iconName, Color color){
        super();
        this.columnInfo = columnInfo;
        this.iconName = iconName;
        this.color = color;
        init();
    }

    private void init() {
        setSpacing(5);

        Label name = new Label(columnInfo.getName());
        FontIcon fontIcon = new FontIcon(iconName);
        fontIcon.setIconColor(color);
        name.setGraphic(fontIcon);

        Label type = new Label(columnInfo.getType());
        type.setFont(Font.font("Verdara", FontWeight.NORMAL, FontPosture.ITALIC, 11));
        type.getStyleClass().add("data-pill");

        getChildren().addAll(name, type);
    }
}
