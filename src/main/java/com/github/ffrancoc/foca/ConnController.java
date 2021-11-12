package com.github.ffrancoc.foca;

import com.github.ffrancoc.foca.lib.Conexion;
import com.github.ffrancoc.foca.model.ConnectionObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class ConnController implements Initializable {
    private Stage mainStage;

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
    private ListView saveConnList;

    @FXML
    private TextArea txtPingMsg;

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
            txtPingMsg.setText("Connection Successfully");
            txtPingMsg.getStyleClass().add("conn-msg-info");
            Conexion.close(connObj.getConn());
        }else {
            hideNode(msgContainer, false);
            txtPingMsg.setText("Connection failed, "+connObj.getMessage());
            txtPingMsg.getStyleClass().add("conn-msg-info");
        }
    }

    @FXML
    private void onActionSaveConn(ActionEvent event) {

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
        System.out.println(DB_URL);


        ConnectionObject connObj = Conexion.connect(DB_URL, user, pass);

        if (connObj.getConn() != null){
            hideNode(msgContainer, true);
            connObj.setUrl(user+"@"+host+":"+port+"/"+db);
            mainStage.setUserData(connObj);
            stage.close();
        }else {
            hideNode(msgContainer, false);
            txtPingMsg.setText("Connection failed, "+connObj.getMessage());
            txtPingMsg.getStyleClass().add("conn-msg-info");
        }
    }

    @FXML
    private void onActionCloseMsgContainer(ActionEvent event) {
        hideNode(msgContainer, true);
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hideNode(msgContainer, true);

        // Validation events
        txtConnPort.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtConnPort.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtConnName.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
        txtConnUser.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
        txtConnPassword.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
        txtConnDB.addEventHandler(KeyEvent.KEY_TYPED, this::validationEvent);
    }

    private void validationEvent(KeyEvent event) {
        if (!txtConnUser.getText().isEmpty() && !txtConnPassword.getText().isEmpty() && !txtConnDB.getText().isEmpty() && !txtConnName.getText().isEmpty()) {
            disableButtons(false);
        }else if (!txtConnUser.getText().isEmpty() && !txtConnPassword.getText().isEmpty() && !txtConnDB.getText().isEmpty() && txtConnName.getText().isEmpty()) {
            disableButtons(true);
            btnTestConn.setDisable(false);
            btnConnectConn.setDisable(false);
        }else {
            disableButtons(true);
        }
    }

    private void disableButtons(boolean status) {
        btnTestConn.setDisable(status);
        btnSaveConn.setDisable(status);
        btnConnectConn.setDisable(status);
    }

    private void hideNode(Node node, boolean status) {
        node.setVisible(!status);
        node.setManaged(!status);
    }
}
