package com.github.ffrancoc.foca;

import com.github.ffrancoc.foca.dialog.DialogManager;
import com.github.ffrancoc.foca.lib.Conexion;
import com.github.ffrancoc.foca.lib.GlobalMessageItem;
import com.github.ffrancoc.foca.lib.SidebarObject;
import com.github.ffrancoc.foca.lib.SidebarObjectDetail;
import com.github.ffrancoc.foca.model.ConnectionObject;
import com.github.ffrancoc.foca.task.AsyncSidebar;
import com.github.ffrancoc.foca.task.AsyncSqlManager;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
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
    private AnchorPane editorContainer;
    private AnchorPane resultContainer;

    // Toolbar widgets
    @FXML
    private Button btnCloseConn;

    @FXML
    private Button btnOpenConn;

    @FXML
    private Button btnEditorStatus;

    @FXML
    private Button btnTpResultStatus;

    @FXML
    private SplitPane spContainer;

    @FXML
    private SplitPane spRight;

    @FXML
    private VBox sidebar;

    @FXML
    private Button btnLoadDB;

    @FXML
    private VBox sidebarDetail;

    @FXML
    private HBox hbStatusbar;

    @FXML
    private CodeArea queryEditor;

    @FXML
    private TabPane tpResult;

    @FXML
    private Tab tabMessages;

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
            Tab tmpTab = tabMessages;
            tpResult.getTabs().clear();
            tpResult.getTabs().add(tmpTab);



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
    private void onActionEditorStatus(ActionEvent event) {
        int spRightSize = spRight.getItems().size();
        if (spRightSize == 2) {
            editorContainer=  (AnchorPane) spRight.getItems().get(0);
            spRight.getItems().remove(0);
        } else if (spRightSize == 1 && ((AnchorPane) spRight.getItems().get(0)).getAccessibleText().equals("editorContainer")) {
            editorContainer =  (AnchorPane) spRight.getItems().get(0);
            spRight.getItems().remove(0);
        }
    }


    @FXML
    private void onActionTpResultStatus(ActionEvent event) {
        int spRightSize = spRight.getItems().size();
        if (spRightSize == 2) {
            resultContainer =  (AnchorPane) spRight.getItems().get(1);
            spRight.getItems().remove(1);
        } else if (spRightSize == 1 && ((AnchorPane) spRight.getItems().get(0)).getAccessibleText().equals("resultContainer")) {
            resultContainer =  (AnchorPane) spRight.getItems().get(0);
            spRight.getItems().remove(0);
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

        queryEditor.setParagraphGraphicFactory(LineNumberFactory.get(queryEditor));

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

        tabMessages.setGraphic(icon("bi-info-circle-fill", Color.DODGERBLUE));
        tpResult.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            Label resultInfo = (Label) hbStatusbar.getChildren().get(3);
            if (newTab != null) {
                if (!newTab.getText().equals("Messages")) {
                    TableView tv = (TableView) newTab.getContent();
                    if (tv != null) {
                        resultInfo.setText("col: " + tv.getColumns().size() + " | row: " + tv.getItems().size());
                    }
                } else {
                    resultInfo.setText("");
                }
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

            // Add ListView ContextMenu
            listView.getItems().forEach(object -> {
                SidebarObject sidebarObj = (SidebarObject) object;
                ContextMenu contextMenu = new ContextMenu();
                MenuItem showHundredRow = new MenuItem("Show 100 rows");
                showHundredRow.setOnAction(action -> {
                    String sqlQuery = "SELECT * FROM `" +sidebarObj.getName()+"` LIMIT 100;";

                    Tab tab = new Tab(sidebarObj.getName());
                    tab.setGraphic(icon("bi-table", Color.DODGERBLUE));
                    tpResult.getTabs().add(tab);
                    tpResult.getSelectionModel().select(tab);

                    Label resultInfo = (Label) hbStatusbar.getChildren().get(3);
                    AsyncSqlManager sqlManager = new AsyncSqlManager(connObj.getConn(), sqlQuery, tab, resultInfo);

                    sqlManager.setOnRunning(running -> {
                        tab.setClosable(false);
                    });

                    sqlManager.setOnSucceeded(succeded2 -> {
                        tab.setClosable(true);
                        appendGlobalMessage("SELECT 100 ROWS FROM `" +sidebarObj.getName()+"`", "bi-code-square", Color.DODGERBLUE);

                    });


                    ExecutorService service = Executors.newFixedThreadPool(1);
                    service.execute(sqlManager);
                    service.shutdown();
                });


                MenuItem showAlldRow = new MenuItem("Show All rows");
                showAlldRow.setOnAction(action -> {
                    String sqlQuery = "SELECT * FROM `" +sidebarObj.getName()+"`;";

                    Tab tab = new Tab(sidebarObj.getName());
                    tab.setGraphic(icon("bi-table", Color.DODGERBLUE));
                    tpResult.getTabs().add(tab);
                    tpResult.getSelectionModel().select(tab);

                    Label resultInfo = (Label) hbStatusbar.getChildren().get(3);
                    AsyncSqlManager sqlManager = new AsyncSqlManager(connObj.getConn(), sqlQuery, tab, resultInfo);

                    sqlManager.setOnRunning(running -> {
                        tab.setClosable(false);
                    });

                    sqlManager.setOnSucceeded(succeded2 -> {
                        tab.setClosable(true);
                        appendGlobalMessage("SELECT ALL ROWS FROM `" +sidebarObj.getName()+"`", "bi-code-square", Color.DODGERBLUE);

                    });


                    ExecutorService service = Executors.newFixedThreadPool(1);
                    service.execute(sqlManager);
                    service.shutdown();
                });


                contextMenu.getItems().addAll(showHundredRow, showAlldRow);
                sidebarObj.getTitle().setContextMenu(contextMenu);
            });

            appendGlobalMessage("Database load succesfully", "bi-info-circle", Color.DODGERBLUE);
        });


        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(asyncSidebar);
        service.shutdown();
    }

    private void appendGlobalMessage(String message, String iconName, Color iconColor) {
        GlobalMessageItem globalMsgItem = new GlobalMessageItem(message, iconName, iconColor);
        globalMsgList.getItems().add(0, globalMsgItem);
    }

    private ProgressIndicator progressIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        indicator.setPrefWidth(15);
        indicator.setPrefHeight(15);
        return indicator;
    }

    private FontIcon icon(String iconName, Color iconColor) {
        FontIcon icon = new FontIcon(iconName);
        icon.setIconColor(iconColor);
        return icon;
    }

    private void hideNode(Node node, boolean status) {
        node.setVisible(!status);
        node.setManaged(!status);
    }
}