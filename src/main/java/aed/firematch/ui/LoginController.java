package aed.firematch.ui;

import aed.firematch.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // Model
    private DBManager dbManager = new DBManager();

    //  View
    @FXML
    private Button cerrarButton;

    @FXML
    private GridPane loginRoot;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwdTextField;

    @FXML
    private TextField usuarioTextField;

    @FXML
    void onCerrarButton(ActionEvent event) {

    }

    @FXML
    void onLoginAction(ActionEvent event) {

    }

    public LoginController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager.crearUsuario("user123", "Jonathan", "Gutiérrez", "jonathan@gmail.com", "jonathan123", "Amo la programación");
    }


    public GridPane getLoginRoot() {
        return loginRoot;
    }
}
