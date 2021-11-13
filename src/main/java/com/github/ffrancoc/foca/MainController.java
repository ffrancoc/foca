package com.github.ffrancoc.foca;

import com.github.ffrancoc.foca.dialog.DialogManager;
import com.github.ffrancoc.foca.lib.*;
import com.github.ffrancoc.foca.model.ConnectionObject;
import com.github.ffrancoc.foca.task.AsyncColumnInfo;
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
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController implements Initializable {
    // Variable para guardar informacion de la conexión
    private ConnectionObject connObj;

    // Variable para guardar el sidebar cuando esta oculto
    private AnchorPane sbContainer;

    // Toolbar widgets
    @FXML
    private Button btnCloseConn;

    @FXML
    private Button btnOpenConn;

    // Main widgets
    @FXML
    private SplitPane spMainContainer;

    @FXML
    private SplitPane spRight;

    @FXML
    private AnchorPane resultContainer;

    @FXML
    private VBox vbSidebarContainer;

    @FXML
    private Label lblSidebarEntitiesInfo;

    @FXML
    private Button btnSidebarLoadDB;

    @FXML
    private ListView lvEntity;

    @FXML
    private VBox vbSidebarColumnContainer;

    @FXML
    private Label columnInfoLblColumnCount;

    @FXML
    private Label columnInfoLlblTableName;

    @FXML
    private ListView lvColumnInfo;

    @FXML
    private HBox hbStatusbarContainer;

    @FXML
    private Label sbLblConnInfo;

    @FXML
    private CodeArea singleQueryEditor;

    @FXML
    private TabPane tpResult;

    @FXML
    private Tab tabMessage;

    @FXML
    private ListView lvGlobalMsgList;


    // Estatus bar items
    @FXML
    private Label sbLblResultInfo;


    // Evento para cerrar la conexion actual
    @FXML
    private void onActionCloseConn(ActionEvent event) {
        if (connObj.getConn() != null) {
            Conexion.close(connObj.getConn());

            lvEntity.getItems().clear();
            lblSidebarEntitiesInfo.setText("Entities(0)");
            btnSidebarLoadDB.setDisable(true);

            lvColumnInfo.getItems().clear();
            hideNode(vbSidebarColumnContainer, true);

            sbLblConnInfo.setText("Not connection");

            hideNode(btnCloseConn, true);
            hideNode(btnOpenConn, false);

            singleQueryEditor.clear();

            lvGlobalMsgList.getItems().clear();
            Tab tmpTab = tabMessage;
            tpResult.getTabs().clear();
            tpResult.getTabs().add(tmpTab);
        }
    }

    // Evento para mostrar dialog de conexiones
    @FXML
    private void onActionOpenConn(ActionEvent event) {
        Stage mainStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        showDialogConnection(mainStage);
    }

    // Evento para volver a cargar la base de datos
    @FXML
    private void onActionLoadDB(ActionEvent event) {
        loadData();
    }

    @FXML
    private void onMouseClickedEntityList(MouseEvent event) {
        int index = lvEntity.getSelectionModel().getSelectedIndex();
        if (index >= 0 && event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {

            EntityObject entityObject = (EntityObject) lvEntity.getItems().get(index);
            lvColumnInfo.getItems().clear();
            columnInfoLlblTableName.setText(entityObject.getName());


            // Si la informacion de columnas de la tabla es null cargar la informacion y mostrarla
            if (entityObject.getColumns() == null) {
                AsyncColumnInfo asyncColumnInfo = new AsyncColumnInfo(connObj.getConn(), entityObject.getName());
                asyncColumnInfo.setOnSucceeded(succeeded -> {
                    entityObject.setColumns(asyncColumnInfo.getValue());
                    loadColmunDetails(entityObject, lvColumnInfo);
                });

                ExecutorService service = Executors.newFixedThreadPool(1);
                service.execute(asyncColumnInfo);
                service.shutdown();

             // Si la informacion de columnas de la tabla no es null mostrarla
            }else {
                loadColmunDetails(entityObject, lvColumnInfo);
            }
        }
    }

    // Evento para ocultar informacion de las columnas si esta visible
    @FXML
    private void onActionCloseSidebarDetail(ActionEvent event) {
        hideNode(vbSidebarColumnContainer, true);
    }

    // Evento para ocultar o mostrar el sidebar
    @FXML
    private void onActionHideSidebar(ActionEvent event) {
        if (spMainContainer.getItems().size() == 2) {
            // Para ocultar el sidebar, se guarda en una variable temporal para liberar el espacio ocupado
            sbContainer = (AnchorPane) spMainContainer.getItems().get(0);
            spMainContainer.getItems().remove(0);
        }else {
            // Para mostrar el sidebar se vuelve a agregar el mismo de la variable temporal
            spMainContainer.getItems().add(0, sbContainer);
        }
    }


    @FXML
    private void onActionResizeTPEditor(ActionEvent event) {
        ToggleButton btnResizeTPResult = (ToggleButton) event.getSource();

        if (btnResizeTPResult.isSelected()) {
            spRight.setDividerPositions(0.9);
        }else {
            spRight.setDividerPositions(0.5);
        }
    }


    @FXML
    private void onActionResizeTPResult(ActionEvent event) {
        ToggleButton btnResizeTPResult = (ToggleButton) event.getSource();

        if (btnResizeTPResult.isSelected()) {
            spRight.setDividerPositions(0.1);
        }else {
            spRight.setDividerPositions(0.5);
        }
    }

    @FXML
    private void onActionCloseSelectedTab(ActionEvent event) {
        int index = tpResult.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            tpResult.getTabs().remove(index);
        }
    }

    @FXML
    private void onActionCloseAllTab(ActionEvent event) {
        Tab tmpTab = tabMessage;
        tpResult.getTabs().clear();
        tpResult.getTabs().add(tmpTab);
    }

    // Funcion principal del stage
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Aplicacion de configuraciones iniciales a algunos widgets
        hideNode(btnCloseConn, true);
        hideNode(vbSidebarColumnContainer, true);
        btnSidebarLoadDB.setDisable(true);
        singleQueryEditor.setParagraphGraphicFactory(LineNumberFactory.get(singleQueryEditor));

        // Agregar imagen default para listview vacio
        Hyperlink lvEntityHolder = new Hyperlink("No data");
        lvEntityHolder.setGraphic(IconHelper.iconHolder("bi-table", Color.LIGHTGRAY, 50));
        lvEntityHolder.setContentDisplay(ContentDisplay.TOP);
        lvEntity.setPlaceholder(lvEntityHolder);

        // Agregar evento para el hyperlink del place holder de el listview
        lvEntityHolder.setOnAction(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            showDialogConnection(stage);
        });

        // Agregar evento de item seleccionado al listview
        lvEntity.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
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

        // Agregar imagen default para listview de mensajes vacio
        Label lvGlobalMsgListHolder = new Label("No Messages");
        lvGlobalMsgListHolder.setGraphic(IconHelper.iconHolder("bi-info-circle", Color.LIGHTGRAY, 50));
        lvGlobalMsgListHolder.setContentDisplay(ContentDisplay.TOP);
        lvGlobalMsgList.setPlaceholder(lvGlobalMsgListHolder);

        // Agregar imagen al tab que contiene el listview de mensajes
        tabMessage.setGraphic(IconHelper.icon("bi-info-circle-fill", Color.DODGERBLUE));

        // Evento para actualizar informacion de columnas y filas de las consultas cuando se cambia de tab
        tpResult.getSelectionModel().selectedItemProperty().addListener((observableValue, oldTab, newTab) -> {
            Label resultInfo = (Label) hbStatusbarContainer.getChildren().get(3);
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

    // Funcion para cargar informacion de las columnas de una tabla
    private void loadColmunDetails(EntityObject sidebarObject, ListView listViewDetail) {
        sidebarObject.getColumns().forEach(columnInfo -> {
            if (columnInfo.isPk()){
                listViewDetail.getItems().add(new SidebarColumnDetail(columnInfo, "bi-key-fill", Color.BLACK));
            }else if (!columnInfo.isPk() && !columnInfo.getFk().getColumnName().isEmpty()){
                listViewDetail.getItems().add(new SidebarColumnDetail(columnInfo, "bi-key", Color.BLACK));
            }else {
                listViewDetail.getItems().add(new SidebarColumnDetail(columnInfo, "bi-table", Color.BLACK));
            }
        });

        columnInfoLblColumnCount.setText("Columns("+listViewDetail.getItems().size()+")");
        hideNode(vbSidebarColumnContainer, false);
    }

    // Funcion para agregar un mensage al listview de mensajes
    private void appendGlobalMessage(String message, FontIcon iconMessage) {
        GlobalMessageItem globalMsgItem = new GlobalMessageItem(message, iconMessage);
        lvGlobalMsgList.getItems().add(0, globalMsgItem);
    }

    // Funcion para crear animacion de carga
    private ProgressIndicator progressIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        indicator.setPrefWidth(15);
        indicator.setPrefHeight(15);
        return indicator;
    }

    // Funcion para ocultar un widget
    private void hideNode(Node node, boolean status) {
        node.setVisible(!status);
        node.setManaged(!status);
    }

    // Funcion para mostrar el dialog de conexiones
    private void showDialogConnection(Stage mainStage) {
        DialogManager.showConnDialog(mainStage);

        if(mainStage.getUserData() != null) {
            connObj = (ConnectionObject) mainStage.getUserData();

            hideNode(btnOpenConn, true);
            hideNode(btnCloseConn, false);

            sbLblConnInfo.setText(connObj.getUrl());
            loadData();
        }
    }

    // Funcion para cargar la informacion de las tablas
    private void loadData() {
        lvEntity.getItems().clear();
        lblSidebarEntitiesInfo.setText("Entities(0)");

        lvColumnInfo.getItems().clear();
        hideNode(vbSidebarColumnContainer, true);

        //long startTime = System.currentTimeMillis();
        //System.out.println("Start:"+startTime);

        AsyncSidebar asyncSidebar = new AsyncSidebar(connObj.getConn(), vbSidebarContainer);
        asyncSidebar.setOnRunning(running -> {
            btnCloseConn.setDisable(true);
            btnSidebarLoadDB.setDisable(true);

            lblSidebarEntitiesInfo.setGraphic(progressIndicator());
        });

        asyncSidebar.setOnSucceeded(succeded -> {
            btnCloseConn.setDisable(false);
            btnSidebarLoadDB.setDisable(false);
            lblSidebarEntitiesInfo.setGraphic(null);

            /*
            long stopTime = System.currentTimeMillis();
            System.out.println("Stop:"+stopTime);
            long elapsedTime = stopTime - startTime;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
            System.out.println("Seconds:"+ TimeUnit.MILLISECONDS.toSeconds(elapsedTime));
            System.out.println("Minutes:"+minutes);
             */

            // Agregando menu contextual a cada item de la lista
            lvEntity.getItems().forEach(object -> {
                EntityObject sidebarObj = (EntityObject) object;
                ContextMenu contextMenu = new ContextMenu();
                MenuItem showHundredRow = new MenuItem("Show Top 100 rows");
                showHundredRow.setOnAction(action -> {
                    // SQL Query a ejecutar
                    String sqlQuery = "SELECT * FROM `" +sidebarObj.getName()+"` LIMIT 100;";

                    // Agregar nueva tab para mostrar los resultados de la consulta
                    Tab tab = TabManager.addTabResult(tpResult, sidebarObj.getName(), IconHelper.icon("bi-table", Color.DODGERBLUE));

                    // Cargar los datos de la consulta de manera asyncrona en la nueva tab creada
                    AsyncSqlManager asyncSqlManager = new AsyncSqlManager(connObj.getConn(), sqlQuery, tab, sbLblResultInfo);

                    asyncSqlManager.setOnRunning(running -> {
                        // deshabilitar que la tab pueda ser cerrada mientras no se cargen los datos
                        tab.setClosable(false);
                    });

                    asyncSqlManager.setOnSucceeded(succeded2 -> {
                        // permitir que la tab pueda ser cerraaa y agregar mensaje de la consulta realizada a la tab mensajes
                        tab.setClosable(true);
                        appendGlobalMessage("SELECT 100 ROWS FROM `" +sidebarObj.getName()+"`", IconHelper.icon("bi-code-square", Color.DODGERBLUE));

                    });

                    // Agrenado funcion asyncrona
                    ExecutorService service = Executors.newFixedThreadPool(1);
                    service.execute(asyncSqlManager);
                    service.shutdown();
                });


                MenuItem showAlldRow = new MenuItem("Show All rows");
                showAlldRow.setOnAction(action -> {
                    String sqlQuery = "SELECT * FROM `" +sidebarObj.getName()+"`;";

                    Tab tab = TabManager.addTabResult(tpResult, sidebarObj.getName(), IconHelper.icon("bi-table", Color.DODGERBLUE));

                    AsyncSqlManager asyncSqlManager = new AsyncSqlManager(connObj.getConn(), sqlQuery, tab, sbLblResultInfo);

                    asyncSqlManager.setOnRunning(running -> {
                        tab.setClosable(false);
                    });

                    asyncSqlManager.setOnSucceeded(succeded2 -> {
                        tab.setClosable(true);
                        appendGlobalMessage("SELECT ALL ROWS FROM `" +sidebarObj.getName()+"`", IconHelper.icon("bi-code-square", Color.DODGERBLUE));

                    });

                    ExecutorService service = Executors.newFixedThreadPool(1);
                    service.execute(asyncSqlManager);
                    service.shutdown();
                });

                // Agregando todos los menu item al menu contextual
                contextMenu.getItems().addAll(showHundredRow, showAlldRow);
                sidebarObj.getTitle().setContextMenu(contextMenu);
            });

            appendGlobalMessage("Database load succesfully", IconHelper.icon("bi-info-circle", Color.DODGERBLUE));
        });

        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(asyncSidebar);
        service.shutdown();
    }
}