package com.github.ffrancoc.foca;

import com.github.ffrancoc.foca.db.ApplicationSetting;
import com.github.ffrancoc.foca.db.Conexion;
import com.github.ffrancoc.foca.lib.SaveConnItem;
import com.github.ffrancoc.foca.model.ConfigurationApp;
import com.github.ffrancoc.foca.model.ConnProperty;
import com.github.ffrancoc.foca.model.ConnectionObject;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ConnController implements Initializable {
    private Stage mainStage;
    private boolean editActive = false;
    private String tempConnName;
    private String tempConnHost;
    private String tempConnPort;
    private String tempConnUser;
    private String tempConnPassword;
    private String tempConnDB;

    @FXML
    private TextField txtConnName;

    @FXML
    private TextField txtConnHost;

    @FXML
    private TextField txtConnPort;

    @FXML
    private TextField txtConnUser;

    @FXML
    private PasswordField txtConnPassword;

    @FXML
    private TextField txtConnDB;

    @FXML
    private Button btnCancelEdit;

    @FXML
    private Button btnSaveEditConn;

    @FXML
    private ListView saveConnList;

    @FXML
    private HBox hbLvSaveConnContainer;

    @FXML
    private Button btnRemoveConn;

    @FXML
    private Button btnEditConn;

    @FXML
    private Button btnConnectSaveConn;

    @FXML
    private TextArea txtConnMsgArea;

    @FXML
    private Button btnTestConn;

    @FXML
    private Button btnSaveConn;

    @FXML
    private Button btnConnectConn;

    @FXML
    private VBox msgContainer;

    @FXML
    private void onActionTestConn(ActionEvent event) {
        String host = "localhost";
        if (!txtConnHost.getText().isEmpty()){
            host = txtConnHost.getText();
        }

        String port = "3306";
        if (!txtConnPort.getText().isEmpty()) {
            port = txtConnPort.getText();
        }


        String user = txtConnUser.getText();
        String pass = txtConnPassword.getText();
        String db = txtConnDB.getText();

        String DB_URL = "jdbc:mariadb://"+host+":"+port+"/"+db;
        System.out.println(DB_URL);


        ConnectionObject connObj = Conexion.connect(DB_URL, user, pass);

        if (connObj.getConn() != null){
            hideNode(msgContainer, false);
            txtConnMsgArea.setText("Connection Successfully");
            txtConnMsgArea.getStyleClass().add("conn-msg-info");
            Conexion.close(connObj.getConn());
        }else {
            hideNode(msgContainer, false);
            txtConnMsgArea.setText("Connection failed, "+connObj.getMessage());
            txtConnMsgArea.getStyleClass().add("conn-msg-info");
        }
    }

    @FXML
    private void onActionSaveConn(ActionEvent event) {

        String connName = txtConnName.getText();

        String host = "localhost";
        if (!txtConnHost.getText().isEmpty()){
            host = txtConnHost.getText();
        }

        String port = "3306";
        if (!txtConnPort.getText().isEmpty()) {
            port = txtConnPort.getText();
        }

        String user = txtConnUser.getText();
        String pass = txtConnPassword.getText();
        String db = txtConnDB.getText();

        boolean connExist = false;
        for(Object object : saveConnList.getItems()){
            SaveConnItem saveConnItem = (SaveConnItem) object;
            if (saveConnItem.getConnName().getText().toLowerCase().equals(connName.toLowerCase())) {
                connExist = true;
                break;
            }
        }


        if (!connExist) {
            SaveConnItem saveConnItem = new SaveConnItem(connName, host, port, user, pass, db);
            saveConnList.getItems().add(saveConnItem);
            cleanFields();
            saveLvConnList();
        }else {
            hideNode(msgContainer, false);
            txtConnMsgArea.setText("The connection cannot be saved because it already exists.");
            txtConnMsgArea.getStyleClass().add("conn-msg-info");
        }



    }

    @FXML
    private void onActionConnectConn(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        String host = "localhost";
        if (!txtConnHost.getText().isEmpty()){
            host = txtConnHost.getText();
        }

        String port = "3306";
        if (!txtConnPort.getText().isEmpty()) {
            port = txtConnPort.getText();
        }

        String user = txtConnUser.getText();
        String pass = txtConnPassword.getText();
        String db = txtConnDB.getText();

        String DB_URL = "jdbc:mariadb://"+host+":"+port+"/"+db;
        ConnectionObject connObj = Conexion.connect(DB_URL, user, pass);

        if (connObj.getConn() != null){
            hideNode(msgContainer, true);
            connObj.setUrl(user+"@"+host+":"+port+"/"+db);
            mainStage.setUserData(connObj);
            stage.close();
        }else {
            hideNode(msgContainer, false);
            txtConnMsgArea.setText("Connection failed, "+connObj.getMessage());
            txtConnMsgArea.getStyleClass().add("conn-msg-info");
        }
    }


    @FXML
    private void onActionRemoveConn(ActionEvent event) {
        int index = saveConnList.getSelectionModel().getSelectedIndex();
        if (index >= 0 ){
            saveConnList.getItems().remove(index);
        }

        saveLvConnList();
    }


    @FXML
    private void onActionEditConn(ActionEvent event) {
        int index = saveConnList.getSelectionModel().getSelectedIndex();
        if(index >= 0) {
            editActive = true;
            btnCancelEdit.setVisible(true);
            btnCancelEdit.setDisable(false);

            btnSaveEditConn.setVisible(true);
            btnSaveEditConn.setDisable(false);

            disableButtons(true);

            SaveConnItem saveConnItem = (SaveConnItem) saveConnList.getItems().get(index);
            tempConnName = saveConnItem.getConnName().getText();
            tempConnHost = saveConnItem.getHost();
            tempConnPort = saveConnItem.getPort();
            tempConnUser = saveConnItem.getUser();
            tempConnPassword = saveConnItem.getPassword();
            tempConnDB = saveConnItem.getDatabase();

            txtConnName.setText(tempConnName);
            if (!tempConnHost.equals("localhost")){
                txtConnHost.setText(tempConnHost);
            }

            if (!tempConnPort.equals("3306")) {
                txtConnPort.setText(tempConnPort);
            }

            txtConnUser.setText(tempConnUser);
            txtConnPassword.setText(tempConnPassword);
            txtConnDB.setText(tempConnDB);

            hbLvSaveConnContainer.setDisable(true);
        }
    }

    @FXML
    private void onActionSaveEditConn(ActionEvent event) {
        String connName;
        if (txtConnName.getText().isEmpty()) {
            connName = tempConnName;
        }else {
            connName = txtConnName.getText();
        }

        String host;
        if (txtConnHost.getText().isEmpty()){
            host = tempConnHost;
        }else {
            host = txtConnHost.getText();
        }

        String port;
        if (txtConnPort.getText().isEmpty()) {
            port = tempConnPort;
        }else {
            port = txtConnPort.getText();
        }

        String user;
        if (txtConnUser.getText().isEmpty()) {
            user = tempConnUser;
        }else {
            user = txtConnUser.getText();
        }

        String pass;
        if (txtConnPassword.getText().isEmpty()) {
            pass = tempConnPassword;
        }else {
            pass = txtConnPassword.getText();
        }

        String db;
        if (txtConnDB.getText().isEmpty()) {
            db = tempConnDB;
        }else {
            db = txtConnDB.getText();
        }

        // Si el nombre de la conexion es diferente del que tenia entonces validar de que no exista
        if(!connName.equals(tempConnName)) {
            boolean connExist = false;
            for(Object object : saveConnList.getItems()){
                SaveConnItem saveConnItem = (SaveConnItem) object;
                if (saveConnItem.getConnName().getText().toLowerCase().equals(connName.toLowerCase())) {
                    connExist = true;
                    break;
                }
            }

            if (!connExist) {
                int index = saveConnList.getSelectionModel().getSelectedIndex();
                SaveConnItem saveConnItem = (SaveConnItem) saveConnList.getItems().get(index);

                saveConnItem.setConnNameStr(connName);
                saveConnItem.setHost(host);
                saveConnItem.setPort(port);
                saveConnItem.setUser(user);
                saveConnItem.setPassword(pass);
                saveConnItem.setDatabase(db);

                btnCancelEdit.setVisible(false);
                btnCancelEdit.setDisable(true);

                btnSaveEditConn.setVisible(false);
                btnSaveEditConn.setDisable(true);

                cleanFields();
                saveLvConnList();

                if (saveConnList.getItems().size() > 0) {
                    hbLvSaveConnContainer.setDisable(false);
                }else{
                    hbLvSaveConnContainer.setDisable(true);
                }

            }else {
                hideNode(msgContainer, false);
                txtConnMsgArea.setText("The connection cannot be saved because it already exists.");
                txtConnMsgArea.getStyleClass().add("conn-msg-info");
            }
        }else {
            int index = saveConnList.getSelectionModel().getSelectedIndex();
            SaveConnItem saveConnItem = (SaveConnItem) saveConnList.getItems().get(index);

            saveConnItem.setConnNameStr(connName);
            saveConnItem.setHost(host);
            saveConnItem.setPort(port);
            saveConnItem.setUser(user);
            saveConnItem.setPassword(pass);
            saveConnItem.setDatabase(db);

            btnCancelEdit.setVisible(false);
            btnCancelEdit.setDisable(true);

            btnSaveEditConn.setVisible(false);
            btnSaveEditConn.setDisable(true);

            cleanFields();
            saveLvConnList();

            if (saveConnList.getItems().size() > 0) {
                hbLvSaveConnContainer.setDisable(false);
            }else{
                hbLvSaveConnContainer.setDisable(true);
            }
        }

    }


    @FXML
    private void onActionConnectSaveConn(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        int index = saveConnList.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            SaveConnItem saveConnItem = (SaveConnItem) saveConnList.getItems().get(index);

            String host = saveConnItem.getHost();
            String port = saveConnItem.getPort();
            String user = saveConnItem.getUser();
            String pass = saveConnItem.getPassword();
            String db = saveConnItem.getDatabase();

            String DB_URL = "jdbc:mariadb://"+host+":"+port+"/"+db;
            ConnectionObject connObj = Conexion.connect(DB_URL, user, pass);

            if (connObj.getConn() != null){
                hideNode(msgContainer, true);
                connObj.setUrl(user+"@"+host+":"+port+"/"+db);
                mainStage.setUserData(connObj);
                stage.close();
            }else {
                hideNode(msgContainer, false);
                txtConnMsgArea.setText("Connection failed, "+connObj.getMessage());
                txtConnMsgArea.getStyleClass().add("conn-msg-info");
            }
        }
    }

    @FXML
    private void onActionCancelEditConn(ActionEvent event) {
        editActive = false;
        btnCancelEdit.setVisible(false);
        btnCancelEdit.setDisable(true);

        btnSaveEditConn.setVisible(false);
        btnSaveEditConn.setDisable(true);

        disableButtons(false);
        hbLvSaveConnContainer.setDisable(false);
        cleanFields();
    }



    @FXML
    private void onActionCloseMsgContainer(ActionEvent event) {
        hideNode(msgContainer, true);
    }

    // Funcion para guardar el stage principal
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hideNode(msgContainer, true);
        btnCancelEdit.setVisible(false);
        btnSaveEditConn.setVisible(false);

        ConfigurationApp appConfig = ApplicationSetting.openSettings();
        if (appConfig != null){
            ArrayList<ConnProperty> saveConnections = appConfig.getSaveConnection();
            saveConnections.forEach(connProperty -> {
                SaveConnItem saveConnItem = new SaveConnItem(connProperty.getName(), connProperty.getHost(), connProperty.getPort(), connProperty.getUser(), connProperty.getPassword(), connProperty.getDatabase());
                saveConnList.getItems().add(saveConnItem);
            });

            if (saveConnList.getItems().size() > 0) {
                hbLvSaveConnContainer.setDisable(false);
            }else{
                hbLvSaveConnContainer.setDisable(true);
            }

        }


        saveConnList.getItems().addListener((ListChangeListener) change -> {
            if (saveConnList.getItems().size() > 0) {
                hbLvSaveConnContainer.setDisable(false);
            }else{
                hbLvSaveConnContainer.setDisable(true);
            }
        });


        saveConnList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(oldValue != null) {
                HBox hbox = (HBox) oldValue;
                Label oldConnName;

                oldConnName = (Label) hbox.getChildren().get(0);
                oldConnName.getStyleClass().remove("sidebar-list-item");
            }

            if (newValue != null) {
                HBox hbox = (HBox) newValue;
                Label newConnName;

                newConnName = (Label) hbox.getChildren().get(0);
                newConnName.getStyleClass().add("sidebar-list-item");
            }
        });


        // Evento de validacion para validar numero entero
        txtConnPort.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtConnPort.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Evento de validacion
        txtConnName.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
        txtConnUser.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
        txtConnPassword.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
        txtConnDB.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
    }

    // Funcion de validacion para informacion de los campos de conexion
    private void validationEvent(KeyEvent event) {
        if (!txtConnUser.getText().isEmpty() && !txtConnPassword.getText().isEmpty() && !txtConnDB.getText().isEmpty() && !txtConnName.getText().isEmpty() && !editActive) {
            disableButtons(false);
        }else if (!txtConnUser.getText().isEmpty() && !txtConnPassword.getText().isEmpty() && !txtConnDB.getText().isEmpty() && txtConnName.getText().isEmpty() && !editActive) {
            disableButtons(true);
            btnTestConn.setDisable(false);
            btnConnectConn.setDisable(false);
        }else {
            disableButtons(true);
        }
    }


    private void saveLvConnList() {
        ArrayList<ConnProperty> saveConnections = new ArrayList<>();
        saveConnList.getItems().forEach(object -> {
            SaveConnItem item = (SaveConnItem) object;
            saveConnections.add(new ConnProperty(item.getConnName().getText(), item.getHost(), item.getPort(), item.getUser(), item.getPassword(), item.getDatabase()));
        });


        ConfigurationApp appConfig = ApplicationSetting.openSettings();
        if (appConfig != null){
            appConfig.setSaveConnection(saveConnections);
            ApplicationSetting.saveSettings(appConfig, true);
        }
    }

    // Funcion para limpiar campos
    private void cleanFields() {
        txtConnName.clear();
        txtConnHost.clear();
        txtConnPort.clear();
        txtConnUser.clear();
        txtConnPassword.clear();
        txtConnDB.clear();

        disableButtons(true);
    }

    // Funcion para deshabilitar los botones
    private void disableButtons(boolean status) {
        btnTestConn.setDisable(status);
        btnSaveConn.setDisable(status);
        btnConnectConn.setDisable(status);
    }

    // Funcion para ocultar un widget
    private void hideNode(Node node, boolean status) {
        node.setVisible(!status);
        node.setManaged(!status);
    }

}
