package com.github.ffrancoc.foca.lib;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class TabManager {

    public static Tab addTabResult (TabPane tpResult, String tabText, FontIcon tabIcon) {
        Tab tab = new Tab(tabText);
        tab.setGraphic(tabIcon);
        tpResult.getTabs().add(tab);
        tpResult.getSelectionModel().select(tab);

        return tab;
    }
}
