package aed.firematch.ui.modelos;

import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class Usuario {

    // Atributos
    private StringProperty nombre;
    private StringProperty apellidos;
    private StringProperty email;
    private StringProperty password;
    private StringProperty descripcion;
    private Image fotoPerfil;
    private ListProperty<String> caracteristicas;
}
