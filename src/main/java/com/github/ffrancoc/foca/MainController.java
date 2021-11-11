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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.IkonResolver;

import java.net.URL;
import java.sql.Connection;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {
    private Connection conn;

    @FXML
    private VBox sidebar;

    @FXML
    private VBox sidebarDetail;

    @FXML
    private void onLoadTest(ActionEvent event) {
        ListView listView = (ListView) sidebar.getChildren().get(1);
        listView.getItems().clear();

        ListView listViewDetail = (ListView) sidebarDetail.getChildren().get(1);
        listViewDetail.getItems().clear();
        hideNode(sidebarDetail, true);


        AsyncSidebar asyncSidebar = new AsyncSidebar(conn, sidebar);
        asyncSidebar.setOnRunning(running -> {

        });

        asyncSidebar.setOnSucceeded(succeded -> {

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
            ListView listViewDetail = (ListView) sidebarDetail.getChildren().get(1);
            listViewDetail.getItems().clear();

            Label tableName = (Label) (sidebarDetail.getChildren().get(0));
            tableName.setText(sidebarObject.getName());

            sidebarObject.getColumns().forEach(columnInfo -> {
                if (columnInfo.isPk()){
                    listViewDetail.getItems().add(new SidebarObjectDetail(columnInfo, "bi-key-fill", Color.BLACK));
                }else {
                    listViewDetail.getItems().add(new SidebarObjectDetail(columnInfo, "bi-table", Color.BLACK));
                }
            });

            Label status = (Label) ((HBox) sidebarDetail.getChildren().get(2)).getChildren().get(0);
            status.setText("Columns("+listViewDetail.getItems().size()+")");

            hideNode(sidebarDetail, false);
        }
    }

    @FXML
    private void onActionCloseSidebarDetail(ActionEvent event) {
        hideNode(sidebarDetail, true);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hideNode(sidebarDetail, true);

        conn = Conexion.connect("sis_inventario", "@dmin21", "@dmin21");

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