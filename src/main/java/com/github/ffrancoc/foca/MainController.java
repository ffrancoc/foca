package com.github.ffrancoc.foca;

import com.github.ffrancoc.foca.lib.Conexion;
import com.github.ffrancoc.foca.lib.SidebarObject;
import com.github.ffrancoc.foca.lib.SidebarObjectDetail;
import com.github.ffrancoc.foca.task.AsyncSidebar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.IkonResolver;

import java.net.URL;
import java.sql.Connection;
import java.sql.Time;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {
    private Connection conn;
    private AnchorPane sbContainer;

    @FXML
    private SplitPane spContainer;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox sidebarDetail;

    @FXML
    private HBox hbStatusbar;

    @FXML
    private void onActionLoadDB(ActionEvent event) {
        Button btnLoadDB = (Button) event.getSource();

        ListView listView = (ListView) sidebar.getChildren().get(1);
        listView.getItems().clear();

        Label status = (Label) ((HBox) sidebar.getChildren().get(0)).getChildren().get(0);
        Label connInfo = (Label) hbStatusbar.getChildren().get(1);
        status.setText("Objects(0)");

        ListView listViewDetail = (ListView) sidebarDetail.getChildren().get(2);
        listViewDetail.getItems().clear();
        hideNode(sidebarDetail, true);

        //long startTime = System.currentTimeMillis();
        //System.out.println("Start:"+startTime);

        AsyncSidebar asyncSidebar = new AsyncSidebar(conn, sidebar);
        asyncSidebar.setOnRunning(running -> {
            btnLoadDB.setDisable(true);

            ProgressIndicator indicator = new ProgressIndicator();
            indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            indicator.setPrefWidth(15);
            indicator.setPrefHeight(15);

            connInfo.setGraphic(indicator);
        });

        asyncSidebar.setOnSucceeded(succeded -> {
            btnLoadDB.setDisable(false);
            connInfo.setGraphic(null);

            /*
            long stopTime = System.currentTimeMillis();
            System.out.println("Stop:"+stopTime);
            long elapsedTime = stopTime - startTime;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
            System.out.println("Seconds:"+ TimeUnit.MILLISECONDS.toSeconds(elapsedTime));
            System.out.println("Minutes:"+minutes);

             */
        });


        ExecutorService service = Executors.newFixedThreadPool(2);
        service.execute(asyncSidebar);
        service.shutdown();

    }

    @FXML
    private void onMouseClickedSidebarList(MouseEvent event) {
        ListView listView = (ListView) sidebar.getChildren().get(1);
        int index = listView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
            SidebarObject sidebarObject = (SidebarObject) listView.getItems().get(index);
            ListView listViewDetail = (ListView) sidebarDetail.getChildren().get(2);
            listViewDetail.getItems().clear();

            Label tableName = (Label) (sidebarDetail.getChildren().get(1));
            tableName.setText(sidebarObject.getName());

            sidebarObject.getColumns().forEach(columnInfo -> {
                if (columnInfo.isPk()){
                    listViewDetail.getItems().add(new SidebarObjectDetail(columnInfo, "bi-key-fill", Color.BLACK));
                }else if (!columnInfo.isPk() && !columnInfo.getFk().getColumnName().isEmpty()){
                    listViewDetail.getItems().add(new SidebarObjectDetail(columnInfo, "bi-key", Color.BLACK));
                }else {
                    listViewDetail.getItems().add(new SidebarObjectDetail(columnInfo, "bi-table", Color.BLACK));
                }
            });

            Label status = (Label) ((HBox) sidebarDetail.getChildren().get(0)).getChildren().get(0);
            status.setText("Columns("+listViewDetail.getItems().size()+")");

            hideNode(sidebarDetail, false);
        }
    }

    @FXML
    private void onActionCloseSidebarDetail(ActionEvent event) {
        hideNode(sidebarDetail, true);
    }

    @FXML
    private void onActionHideSidebar(ActionEvent event) {
        if (spContainer.getItems().size() == 2) {
            sbContainer = (AnchorPane) spContainer.getItems().get(0);
            spContainer.getItems().remove(0);
        }else {
            spContainer.getItems().add(0, sbContainer);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hideNode(sidebarDetail, true);


        conn = Conexion.connect("sis_inventario", "@dmin21", "@dmin21");
        Label connInfo = (Label) hbStatusbar.getChildren().get(1);
        connInfo.setText("@dmin21@localhost:3306/sis_inventario");


        ListView listView = (ListView) sidebar.getChildren().get(1);
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(oldValue != null) {
                Label oldTitle =(Label) ((HBox) oldValue).getChildren().get(0);
                Label oldPill = (Label) ((HBox) oldValue).getChildren().get(2);

                oldTitle.getStyleClass().remove("sidebar-list-item");
                oldPill.getStyleClass().remove("sidebar-list-item-pill");

            }

            if (newValue != null) {
                Label newTitle = (Label) ((HBox) newValue).getChildren().get(0);
                Label newPill = (Label) ((HBox) newValue).getChildren().get(2);

                newTitle.getStyleClass().add("sidebar-list-item");
                newPill.getStyleClass().add("sidebar-list-item-pill");
            }
        });
    }

    private void hideNode(Node node, boolean status) {
        node.setVisible(!status);
        node.setManaged(!status);
    }
}