package aed.firematch.ui.modelos;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class  Usuario {

    // Atributos
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nombre = new SimpleStringProperty();
    private StringProperty apellidos = new SimpleStringProperty();
    private IntegerProperty edad = new SimpleIntegerProperty();
    private StringProperty nickname = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty descripcion = new SimpleStringProperty();
    private ObjectProperty<Genero> genero = new SimpleObjectProperty<>();
    private Image fotoPerfil;
    private ListProperty<Genero> gustos = new SimpleListProperty<>(FXCollections.observableArrayList());
    private ListProperty<String> caracteristicas = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Usuario() {
    }

    public Usuario(int id, String nombre, String apellidos, String nickname, String email, String password,
                   String descripcion, int edad, Genero genero, ObservableList<String> caracteristicas,
                   ObservableList<Genero> gustos) {
        this.id.set(id);
        this.nombre.set(nombre);
        this.apellidos.set(apellidos);
        this.nickname.set(nickname);
        this.email.set(email);
        this.password.set(password);
        this.descripcion.set(descripcion);
        this.edad.set(edad);
        this.genero.set(genero);
        this.caracteristicas.set(FXCollections.observableArrayList(caracteristicas));
        this.gustos.set(FXCollections.observableArrayList(gustos));
    }

    public int getEdad() {
        return edad.get();
    }

    public IntegerProperty edadProperty() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad.set(edad);
    }

    public Genero getGenero() {
        return genero.get();
    }

    public ObjectProperty<Genero> generoProperty() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero.set(genero);
    }

    public ObservableList<Genero> getGustos() {
        return gustos.get();
    }

    public ListProperty<Genero> gustosProperty() {
        return gustos;
    }

    public void setGustos(ObservableList<Genero> gustos) {
        this.gustos.set(gustos);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos.get();
    }

    public StringProperty apellidosProperty() {
        return apellidos;
    }

    public String getNickname() {
        return nickname.get();
    }

    public StringProperty nicknameProperty() {
        return nickname;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public Image getFotoPerfil() {
        return fotoPerfil;
    }

    public ObservableList<String> getCaracteristicas() {
        return caracteristicas.get();
    }

    public ListProperty<String> caracteristicasProperty() {
        return caracteristicas;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public void setApellidos(String apellidos) {
        this.apellidos.set(apellidos);
    }

    public void setNickname(String nickname) {
        this.nickname.set(nickname);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public void setFotoPerfil(Image fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public void setCaracteristicas(ObservableList<String> caracteristicas) {
        this.caracteristicas.set(caracteristicas);
    }
}
