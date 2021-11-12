package com.github.ffrancoc.foca.dialog;

import com.github.ffrancoc.foca.ConnController;
import com.github.ffrancoc.foca.MainApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DialogManager {
    public static void showConnDialog(Stage mainStage){
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("layout/conn-view.fxml"));
            Parent parent = loader.load();

            ConnController controller = loader.<ConnController>getController();
            controller.setMainStage(mainStage);
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(MainApplication.class.getResource("css/light.css").toExternalForm());

            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initOwner(mainStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Error to load xml view, "+e.getMessage());
        }
    }

}
