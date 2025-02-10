package aed.firematch.ui.controllers;

import aed.firematch.ui.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PerfilController implements Initializable {

    @FXML
    private TextArea caracteristicasTextArea;

    @FXML
    private Button cerrarButton;

    @FXML
    private Label descripcionLabel;

    @FXML
    private Label edadLabel;

    @FXML
    private Label nombreLabel;

    @FXML
    private ImageView perfilImageView;

    private Stage perfilStage;
    private Usuario usuario;

    @FXML
    void onCerrarButton(ActionEvent event) {
        perfilStage.close();
    }

    public PerfilController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PerfilView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setUsuario(Usuario usuario, Stage stage) {
        this.usuario = usuario;
        this.perfilStage = stage;
        actualizarPerfil();
    }

    private void actualizarPerfil() {
        if (usuario != null) {
            nombreLabel.setText("• " + usuario.getNombre());
            descripcionLabel.setText(usuario.getDescripcion());
            edadLabel.setText("• " + usuario.getEdad());

            String imagePath = "/images/usuario" + usuario.getId() + ".jpg";
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                perfilImageView.setImage(new Image(imageUrl.toExternalForm()));
            }

            StringBuilder caracteristicas = new StringBuilder();
            for (String caracteristica : usuario.getCaracteristicas()) {
                caracteristicas.append("- ").append(caracteristica).append("\n");
            }
            caracteristicasTextArea.setText(caracteristicas.toString());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
