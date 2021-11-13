package com.github.ffrancoc.foca.model;

import java.util.ArrayList;

// Clase principal para guardar los diferentes modelos de configuracion
public class ConfigurationApp {
    private WindowProperty windowProperty; // Modelo de preferencias de la ventana de la aplicacion
    private ArrayList<ConnProperty> saveConnection;

    public WindowProperty getWindowProperty() {
        return windowProperty;
    }

    public void setWindowProperty(WindowProperty windowProperty) {
        this.windowProperty = windowProperty;
    }

    public ArrayList<ConnProperty> getSaveConnection() {
        return saveConnection;
    }

    public void setSaveConnection(ArrayList<ConnProperty> saveConnection) {
        this.saveConnection = saveConnection;
    }

    public ConfigurationApp (WindowProperty windowProperty, ArrayList<ConnProperty> saveConnection){
        this.windowProperty = windowProperty;
        this.saveConnection = saveConnection;
    }
}
