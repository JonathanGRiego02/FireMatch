package aed.firematch.ui.controllers;

import aed.firematch.firebase.DBManager;
import aed.firematch.ui.modelos.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    // Model
    private DBManager dbManager = new DBManager();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();

    private MainController mainController = new MainController();


    //  View
    @FXML
    private Button cerrarButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private Button loginButton;

    @FXML
    private GridPane loginRoot;

    @FXML
    private PasswordField passwdTextField;

    @FXML
    void onCerrarButton(ActionEvent event) {
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onLoginAction(ActionEvent event) {
        if (dbManager.login(email.getValue(), password.getValue())) {
            // Cerrar la ventana de login
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();

            // Abrir la ventana principal
            Stage mainStage = new Stage();
            Scene scene = new Scene(mainController.getRoot());
            mainStage.setScene(scene);
            mainStage.setTitle("FireMatch");
            mainStage.show();
        } else {
            // Mostrar diálogo de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText("Credenciales incorrectas");
            alert.setContentText("El usuario o la contraseña son incorrectos. Por favor, inténtelo de nuevo.");
            alert.showAndWait();
        }
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
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setEmail("jonathan@gmail.com");
        usuario.setNombre("Jonathan");
        usuario.setApellidos("Gutiérrez");
        usuario.setPassword("jonathan123");
        usuario.setDescripcion("Amo la programación");
        usuario.setNickname("jonathan_gr");
        usuario.getCaracteristicas().add("Me gusta el furbo");
        dbManager.crearUsuario(usuario);
        email.bindBidirectional(emailTextField.textProperty());
        password.bindBidirectional(passwdTextField.textProperty());
    }


    public GridPane getLoginRoot() {
        return loginRoot;
    }
}
