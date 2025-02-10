package aed.firematch.ui.controllers;

import aed.firematch.firebase.DBManager;
import aed.firematch.ui.modelos.Genero;
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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController implements Initializable {


    // Model
    private ObjectProperty<Usuario> usuarioLoged = new SimpleObjectProperty<>();
    private Stage ajustesStage;
    private DBManager dbManager = new DBManager();
    private ListProperty<Usuario> usuarios = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<Usuario> usuariosLike = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ObjectProperty<Usuario> usuarioActual = new SimpleObjectProperty<>();
    private int usuariosIndex = 0;
    private AjustesController ajustesController = new AjustesController();

    // View

    @FXML
    private Button matchButton;

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
    void onMatchAction(ActionEvent event) {
        if (usuariosLike.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Matches");
            alert.setHeaderText(null);
            alert.setContentText("Aún no has dado me gusta a nadie.");
            alert.showAndWait();
        } else {
            String nombres = usuariosLike.stream()
                    .map(u -> u.getNombre() + " " + u.getApellidos() + " (\"" + u.getNickname() + "\")")
                    .collect(Collectors.joining("\n"));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Matches");
            alert.setHeaderText("Personas a las que diste me gusta:");
            alert.setContentText(nombres);
            alert.showAndWait();
        }
    }

    @FXML
    void onAjustesAction(ActionEvent event) {
        if (ajustesStage == null) {
            try {
                ajustesStage = new Stage();
                ajustesStage.setTitle("Ajustes");
                ajustesStage.setScene(new Scene(ajustesController.getRoot()));
                ajustesStage.initOwner(mainRoot.getScene().getWindow()); // Opcional, para que dependa de la ventana principal
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ajustesStage.show(); // Hacer la ventana visible
    }


    /**
     * Método para actualizar la lista de usuarios según los nuevos filtros.
     */
    public void actualizarUsuarioLogeado(int edadMin, int edadMax, boolean masculino, boolean femenino, boolean noBinario, boolean otro) {
        if (usuarioLoged != null) {

            if (masculino) usuarioLoged.get().getGustos().add(Genero.MASCULINO);
            if (femenino) usuarioLoged.get().getGustos().add(Genero.FEMENINO);
            if (noBinario) usuarioLoged.get().getGustos().add(Genero.NO_BINARIO);
            if (otro) usuarioLoged.get().getGustos().add(Genero.OTRO);
            usuarios.clear();
            usuarios.set(FXCollections.observableArrayList(dbManager.GetUsuarios(usuarioLoged.get(), edadMin, edadMax)));
            usuarioActual.set(usuarios.getFirst());
        }
    }


    @FXML
    void onMenuAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PerfilView.fxml"));
            PerfilController perfilController = new PerfilController();
            loader.setController(perfilController);
            GridPane perfilRoot = loader.load();

            Stage perfilStage = new Stage();
            perfilController.setUsuario(usuarioLoged.get(), perfilStage);
            perfilStage.setTitle("Perfil");
            perfilStage.setScene(new Scene(perfilRoot));
            perfilStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            mostrarFinUsuarios();
            return;
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

    private void mostrarFinUsuarios() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No quedan personas");
        alert.setHeaderText(null);
        alert.setContentText("Se han acabado las personas disponibles. Vuelve más tarde.");
        alert.showAndWait();

        // Oculta la imagen y muestra el mensaje
        ligueImageView.setVisible(false);

        Label noPersonasLabel = new Label("No quedan personas");
        noPersonasLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        mainRoot.setCenter(noPersonasLabel);
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
        this.usuarioLoged.set(usuarioLoged);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usuarioActual.addListener(this::onUsuarioActualChanged);
        ajustesController.setMainController(this);

        ajustesController.usuarioActualProperty().bindBidirectional(usuarioLoged);
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
