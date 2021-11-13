package com.github.ffrancoc.foca;

import com.github.ffrancoc.foca.db.ApplicationSetting;
import com.github.ffrancoc.foca.model.ConfigurationApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("layout/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(MainApplication.class.getResource("css/light.css").toExternalForm());
        stage.setTitle("Foca");
        stage.setScene(scene);

        // Cargar preferencias de la aplicacion
        if (!ApplicationSetting.existsSettings()){
            ApplicationSetting.initDefaultSettings();
        }else{
            ConfigurationApp appConfig = ApplicationSetting.openSettings();
            if (appConfig != null){
                stage.setWidth(appConfig.getWindowProperty().getWidth());
                stage.setHeight(appConfig.getWindowProperty().getHeight());
                stage.setX(appConfig.getWindowProperty().getX());
                stage.setY(appConfig.getWindowProperty().getY());
            }
        }

        stage.show();

        // Guadar informacion de la ventana de la aplicacion
        stage.getScene().getWindow().setOnCloseRequest(windowEvent ->{
            ConfigurationApp appConfig = ApplicationSetting.openSettings();
            if (appConfig != null){
                appConfig.getWindowProperty().setWidth(stage.getWidth());
                appConfig.getWindowProperty().setHeight(stage.getHeight());
                appConfig.getWindowProperty().setX(stage.getX());
                appConfig.getWindowProperty().setY(stage.getY());

                ApplicationSetting.saveSettings(appConfig, true);
                System.out.println(appConfig.getWindowProperty().toString());
            }

        });

    }

    public static void main(String[] args) {
        launch();
    }
}