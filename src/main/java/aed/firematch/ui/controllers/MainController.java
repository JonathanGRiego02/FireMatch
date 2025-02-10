package aed.firematch.ui.controllers;

import aed.firematch.firebase.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Model
    private DBManager dbManager = new DBManager();

    // View
    @FXML
    private Button ajustesButton;

    @FXML
    private Label descripcionLabel;

    @FXML
    private Label edadLabel;

    @FXML
    private Button likeButton;

    @FXML
    private BorderPane mainRoot;

    @FXML
    private Button menuButton;

    @FXML
    private Label nombreLabel;

    @FXML
    private Button passButton;

    @FXML
    void onAjustesAction(ActionEvent event) {

    }

    @FXML
    void onLikeAction(ActionEvent event) {

    }

    @FXML
    void onMenuAction(ActionEvent event) {

    }

    @FXML
    void onPassAction(ActionEvent event) {

    }

    public MainController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public BorderPane getRoot() {
        return mainRoot;
    }
}
