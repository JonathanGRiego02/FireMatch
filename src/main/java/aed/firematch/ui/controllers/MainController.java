package aed.firematch.ui.controllers;

import aed.firematch.firebase.DBManager;
import aed.firematch.ui.modelos.Usuario;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Model
    private DBManager dbManager = new DBManager();
    private ListProperty<Usuario> usuarios = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Usuario> usuariosLike = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ObjectProperty<Usuario> usuarioActual = new SimpleObjectProperty<>();
    private int usuariosIndex = 0;

    // View
    @FXML
    private Button ajustesButton;

    @FXML
    private Label descripcionLabel;

    @FXML
    private Label edadLabel;

    @FXML
    private ImageView ligueImageView;

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
    private Button perfilButton;

    @FXML
    void onAjustesAction(ActionEvent event) {

    }


    @FXML
    void onMenuAction(ActionEvent event) {

    }

    @FXML
    void onPerfilAction(ActionEvent event) {
        if (usuarioActual.get() != null) {
            StringBuilder caracteristicas = new StringBuilder("Más sobre mí:\n\n");
            for (String caracteristica : usuarioActual.get().getCaracteristicas()) {
                caracteristicas.append("- ").append(caracteristica).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Perfil de " + usuarioActual.get().getNombre());
            alert.setHeaderText(null);
            alert.setContentText(caracteristicas.toString());
            alert.showAndWait();
        }
    }

    @FXML
    void onLikeAction(ActionEvent event) {
        usuariosLike.add(usuarioActual.get());
        usuarios.remove(usuarioActual.get());
        usuariosIndex++;
        if (usuarios.isEmpty()) {

        }
        if (usuariosIndex >= usuarios.size()) {
            usuariosIndex = 0;
        }
        usuarioActual.set(usuarios.get(usuariosIndex));
        System.out.println("nombre: " + usuarioActual.get().getNombre());
    }

    @FXML
    void onPassAction(ActionEvent event) {
        usuariosIndex++;
        if (usuariosIndex >= usuarios.size()) {
            usuariosIndex = 0;
        }
        usuarioActual.set(usuarios.get(usuariosIndex));
    }


    public MainController(Usuario usuarioLoged) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        usuarios.set(FXCollections.observableArrayList(dbManager.GetUsuarios(usuarioLoged, 0, 1000)));
        usuarioActual.set(usuarios.getFirst());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuarioActual.addListener(this::onUsuarioActualChanged);
    }


    private void onUsuarioActualChanged(ObservableValue<? extends Usuario> o, Usuario oldValue, Usuario newValue) {
        nombreLabel.setText(newValue.getNombre());
        descripcionLabel.setText(newValue.getDescripcion());
        edadLabel.setText(String.valueOf(newValue.getEdad()));

        // Obtener la ruta del recurso basado en el ID del usuario
        String imagePath = "/images/usuario" + newValue.getId() + ".jpg"; // Ajusta la extensión si es diferente

        // Obtener la URL del recurso
        URL imageUrl = getClass().getResource(imagePath);

        if (imageUrl != null) {
            ligueImageView.setImage(new Image(imageUrl.toExternalForm()));
        } else {
        }
    }
    public BorderPane getRoot() {
        return mainRoot;
    }


    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

}
