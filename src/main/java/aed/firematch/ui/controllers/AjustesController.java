package aed.firematch.ui.controllers;

import aed.firematch.ui.modelos.Genero;
import aed.firematch.ui.modelos.Usuario;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class AjustesController implements Initializable {

    private ObjectProperty<Usuario> usuarioActual = new SimpleObjectProperty<>();
    private MainController mainController; // Referencia al MainController

    @FXML
    private GridPane root;

    @FXML
    private Button aplicarButton;

    @FXML
    private TextField edadMaxTextField;

    @FXML
    private TextField edadMinTextField;

    @FXML
    private CheckBox femeninoCheckBox;

    @FXML
    private CheckBox masculinoCheckBox;

    @FXML
    private CheckBox noBinarioCheckBox;

    @FXML
    private CheckBox otroCheckBox;

    @FXML
    void onAplicarAction(ActionEvent event) {
        try {
            int edadMin = Integer.parseInt(edadMinTextField.getText());
            int edadMax = Integer.parseInt(edadMaxTextField.getText());
            boolean masculino = masculinoCheckBox.isSelected();
            boolean femenino = femeninoCheckBox.isSelected();
            boolean noBinario = noBinarioCheckBox.isSelected();
            boolean otro = otroCheckBox.isSelected();

            mainController.actualizarUsuarioLogeado(edadMin, edadMax, masculino, femenino, noBinario, otro);

            Stage stage = (Stage) aplicarButton.getScene().getWindow();
            stage.hide();
        } catch (NumberFormatException e) {
            System.out.println("Error al parsear la edad");
        }
    }

    public AjustesController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AjustesView.fxml"));
            loader.setController(this);
            loader.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Vincular CheckBoxes con los gustos cuando cambie el usuario actual
        usuarioActual.addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                bindCheckBoxes(newUser);
            }
        });
    }

    private void bindCheckBoxes(Usuario usuario) {
        // Establecer valores iniciales en los CheckBox
        masculinoCheckBox.setSelected(usuario.getGustos().contains(Genero.MASCULINO));
        femeninoCheckBox.setSelected(usuario.getGustos().contains(Genero.FEMENINO));
        noBinarioCheckBox.setSelected(usuario.getGustos().contains(Genero.NO_BINARIO));
        otroCheckBox.setSelected(usuario.getGustos().contains(Genero.OTRO));

        // Bind bidireccional para sincronizar los gustos del usuario con las CheckBox
        masculinoCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            actualizarGustos(usuario, Genero.MASCULINO, isSelected);
        });

        femeninoCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            actualizarGustos(usuario, Genero.FEMENINO, isSelected);
        });

        noBinarioCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            actualizarGustos(usuario, Genero.NO_BINARIO, isSelected);
        });

        otroCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            actualizarGustos(usuario, Genero.OTRO, isSelected);
        });
    }

    private void actualizarGustos(Usuario usuario, Genero genero, boolean isSelected) {
        ObservableList<Genero> gustos = usuario.getGustos();

        if (isSelected) {
            if (!gustos.contains(genero)) {
                gustos.add(genero);
                System.out.println("Gusto añadido: " + genero);
            }
        } else {
            gustos.remove(genero);
            System.out.println("Gusto eliminado: " + genero);
        }

        usuario.setGustos(FXCollections.observableArrayList(gustos)); // Notificar cambios
    }


    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual.set(usuario);
    }

    public Usuario getUsuarioActual() {
        return usuarioActual.get();
    }

    public ObjectProperty<Usuario> usuarioActualProperty() {
        return usuarioActual;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public GridPane getRoot() {
        return root;
    }
}
