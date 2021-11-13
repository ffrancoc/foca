package com.github.ffrancoc.foca.model;


// Modelo de las preferencias de la ventana de la aplicacion
public class WindowProperty {
    private double width;
    private double height;
    private double x;
    private double y;

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public WindowProperty(double width, double height, int x, int y){
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

    }

    @Override
    public String toString(){
        return "Window (width="+width+", height="+height+", x="+x+", y="+y+")";
    }
}
