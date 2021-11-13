package com.github.ffrancoc.foca;

import com.github.ffrancoc.foca.dialog.DialogManager;
import com.github.ffrancoc.foca.lib.Conexion;
import com.github.ffrancoc.foca.lib.GlobalMessageItem;
import com.github.ffrancoc.foca.lib.SidebarObject;
import com.github.ffrancoc.foca.lib.SidebarObjectDetail;
import com.github.ffrancoc.foca.model.ConnectionObject;
import com.github.ffrancoc.foca.task.AsyncSidebar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {
    private ConnectionObject connObj;
    private AnchorPane sbContainer;

    // Toolbar widgets
    @FXML
    private Button btnCloseConn;

    @FXML
    private Button btnOpenConn;

    @FXML
    private SplitPane spContainer;

    @FXML
    private VBox sidebar;

    @FXML
    private Button btnLoadDB;

    @FXML
    private VBox sidebarDetail;

    @FXML
    private HBox hbStatusbar;



    @FXML
    private ListView globalMsgList;


    @FXML
    private void onActionCloseConn(ActionEvent event) {
        if (connObj.getConn() != null) {
            Conexion.close(connObj.getConn());
            ListView listView = (ListView) sidebar.getChildren().get(1);
            listView.getItems().clear();

            Label status = (Label) ((HBox) sidebar.getChildren().get(0)).getChildren().get(0);
            status.setText("Objects(0)");

            btnLoadDB.setDisable(true);

            ListView listViewDetail = (ListView) sidebarDetail.getChildren().get(2);
            listViewDetail.getItems().clear();
            hideNode(sidebarDetail, true);

            Label connInfo = (Label) hbStatusbar.getChildren().get(1);
            connInfo.setText("Not connection");

            hideNode(btnCloseConn, true);
            hideNode(btnOpenConn, false);


            globalMsgList.getItems().clear();


        }
    }

    @FXML
    private void onActionOpenConn(ActionEvent event) {
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        DialogManager.showConnDialog(mainStage);

        if(mainStage.getUserData() != null) {
            connObj = (ConnectionObject) mainStage.getUserData();

            hideNode(btnOpenConn, true);
            hideNode(btnCloseConn, false);

            Label connInfo = (Label) hbStatusbar.getChildren().get(1);
            connInfo.setText(connObj.getUrl());

            loadData();
        }
    }


    @FXML
    private void onActionLoadDB(ActionEvent event) {
        loadData();
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
        hideNode(btnCloseConn, true);
        hideNode(sidebarDetail, true);
        btnLoadDB.setDisable(true);


        /*
        conn = Conexion.connect("sis_inventario", "@dmin21", "@dmin21");
        Label connInfo = (Label) hbStatusbar.getChildren().get(1);
        connInfo.setText("@dmin21@localhost:3306/sis_inventario");
        */

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

    private void loadData() {
        ListView listView = (ListView) sidebar.getChildren().get(1);
        listView.getItems().clear();

        Label status = (Label) ((HBox) sidebar.getChildren().get(0)).getChildren().get(0);
        //Label connInfo = (Label) hbStatusbar.getChildren().get(1);
        status.setText("Objects(0)");

        ListView listViewDetail = (ListView) sidebarDetail.getChildren().get(2);
        listViewDetail.getItems().clear();
        hideNode(sidebarDetail, true);

        //long startTime = System.currentTimeMillis();
        //System.out.println("Start:"+startTime);

        AsyncSidebar asyncSidebar = new AsyncSidebar(connObj.getConn(), sidebar);
        asyncSidebar.setOnRunning(running -> {
            btnCloseConn.setDisable(true);
            btnLoadDB.setDisable(true);

            status.setGraphic(progressIndicator());
        });

        asyncSidebar.setOnSucceeded(succeded -> {
            btnCloseConn.setDisable(false);
            btnLoadDB.setDisable(false);
            status.setGraphic(null);

            /*
            long stopTime = System.currentTimeMillis();
            System.out.println("Stop:"+stopTime);
            long elapsedTime = stopTime - startTime;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
            System.out.println("Seconds:"+ TimeUnit.MILLISECONDS.toSeconds(elapsedTime));
            System.out.println("Minutes:"+minutes);

             */

            GlobalMessageItem globalMsgItem = new GlobalMessageItem("Database load succesfully", "bi-info-circle-fill", Color.DODGERBLUE);
            globalMsgList.getItems().add(0, globalMsgItem);
        });


        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(asyncSidebar);
        service.shutdown();
    }

    private ProgressIndicator progressIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        indicator.setPrefWidth(15);
        indicator.setPrefHeight(15);
        return indicator;
    }

    private void hideNode(Node node, boolean status) {
        node.setVisible(!status);
        node.setManaged(!status);
    }
}