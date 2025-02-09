package aed.firematch.ui;

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

    }


    public GridPane getLoginRoot() {
        return loginRoot;
    }
}
