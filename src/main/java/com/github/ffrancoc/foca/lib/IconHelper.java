package com.github.ffrancoc.foca.lib;

import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

public class IconHelper {


    public static FontIcon iconHolder(String iconName, Color iconColor, int iconSize) {
        FontIcon icon = new FontIcon(iconName);
        icon.setIconColor(iconColor);
        icon.setIconSize(iconSize);
        return icon;
    }

    public static FontIcon icon(String iconName, Color iconColor) {
        FontIcon icon = new FontIcon(iconName);
        icon.setIconColor(iconColor);
        return icon;
    }

    public static FontIcon icon(String iconName, Color iconColor, int iconSize) {
        FontIcon icon = new FontIcon(iconName);
        icon.setIconColor(iconColor);
        icon.setIconSize(iconSize);
        return icon;
    }
}
