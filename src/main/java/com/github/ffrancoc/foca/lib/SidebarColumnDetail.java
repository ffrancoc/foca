package com.github.ffrancoc.foca.lib;

import com.github.ffrancoc.foca.model.ColumnInfo;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class SidebarColumnDetail extends VBox {
    private ColumnInfo columnInfo;
    private String iconName;
    private Color color;

    public SidebarColumnDetail(ColumnInfo columnInfo, String iconName, Color color){
        super();
        this.columnInfo = columnInfo;
        this.iconName = iconName;
        this.color = color;
        init();
    }

    private void init() {
        setSpacing(5);

        Label lblColumnName = new Label(columnInfo.getName());
        lblColumnName.setGraphic(IconHelper.icon(iconName, color));
        getChildren().add(lblColumnName);

        if (!columnInfo.getFk().getColumnName().isEmpty()){
            Label lblColumnFKName = new Label(columnInfo.getFk().getTableParent()+"("+columnInfo.getFk().getColumnName()+")");
            lblColumnFKName.setFont(Font.font("Verdara", FontWeight.NORMAL, FontPosture.ITALIC, 11));

            getChildren().add(lblColumnFKName);
        }

        Label lblColumnTypeData = new Label(columnInfo.getType());
        lblColumnTypeData.setFont(Font.font("Verdara", FontWeight.NORMAL, FontPosture.ITALIC, 11));
        lblColumnTypeData.getStyleClass().add("data-pill");

        getChildren().add(lblColumnTypeData);
    }
}
