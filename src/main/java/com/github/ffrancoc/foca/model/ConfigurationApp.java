package com.github.ffrancoc.foca.model;

// Clase principal para guardar los diferentes modelos de configuracion
public class ConfigurationApp {
    private WindowProperty windowProperty; // Modelo de preferencias de la ventana de la aplicacion

    public WindowProperty getWindowProperty() {
        return windowProperty;
    }

    public void setWindowProperty(WindowProperty windowProperty) {
        this.windowProperty = windowProperty;
    }

    public ConfigurationApp (WindowProperty windowProperty){
        this.windowProperty = windowProperty;
    }
}
