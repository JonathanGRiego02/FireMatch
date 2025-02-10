package aed.firematch.firebase;

import aed.firematch.ui.modelos.Genero;
import aed.firematch.ui.modelos.Usuario;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class DBManager {

    private Firestore db;


    public DBManager() {
        try {
            // Cargar el archivo JSON desde la carpeta resources
            InputStream serviceAccount = DBManager.class.getClassLoader()
                    .getResourceAsStream("firematch-firebase-adminsdk.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Archivo de credenciales no encontrado en resources");
            }

            // Configura Firebase
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://firematch.firebaseio.com") // Asegúrate de que el URL es correcto
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // Evita inicializar múltiples veces
                FirebaseApp.initializeApp(options);
            }

            // Obtener una instancia de Firestore
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea un usuario en Firestore dentro de la colección "FireMatch/Usuarios"
     */
    public void crearUsuario(Usuario usuario) {
        if (usuario == null) {
            System.err.println("Error: El usuario no puede ser nulo.");
            return;
        }

        String idUsuario = String.valueOf(usuario.getId());

        // Referencia a la colección de usuarios
        CollectionReference usuariosRef = db.collection("FireMatch").document("Usuarios").collection("ListaUsuarios");
        DocumentReference usuarioDoc = usuariosRef.document(idUsuario);

        // Datos del usuario en un mapa (sin características, porque será una subcolección)
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("nombre", usuario.getNombre());
        usuarioData.put("apellidos", usuario.getApellidos());
        usuarioData.put("nickname", usuario.getNickname());
        usuarioData.put("email", usuario.getEmail());
        usuarioData.put("password", usuario.getPassword());
        usuarioData.put("descripcion", usuario.getDescripcion());
        usuarioData.put("edad", usuario.getEdad());
        usuarioData.put("genero", usuario.getGenero().name());
        usuarioData.put("fotoPerfil", null); // Se manejará en Firebase Storage

        try {
            // Verificar si el usuario ya existe
            if (!usuarioDoc.get().get().exists()) {
                usuarioDoc.set(usuarioData).get(); // Guardar los datos básicos
                System.out.println("✅ Usuario creado con éxito: " + idUsuario);

                // Crear la subcolección "Caracteristicas" con IDs numerados
                if (usuario.getCaracteristicas() != null && !usuario.getCaracteristicas().isEmpty()) {
                    CollectionReference caracteristicasRef = usuarioDoc.collection("Caracteristicas");

                    int index = 1;
                    for (String caracteristica : usuario.getCaracteristicas()) {
                        String idCaracteristica = "caracteristica" + index; // ID fijo numerado

                        Map<String, Object> data = new HashMap<>();
                        data.put("valor", caracteristica);
                        caracteristicasRef.document(idCaracteristica).set(data).get(); // Guardar con ID fijo

                        index++;
                    }
                    System.out.println("✅ Características agregadas para el usuario: " + idUsuario);
                }

                // Crear la subcolección "Gustos" utilizando el nombre del gusto
                if (usuario.getGustos() != null && !usuario.getGustos().isEmpty()) {
                    CollectionReference gustosRef = usuarioDoc.collection("Gustos");

                    for (Genero gusto : usuario.getGustos()) {
                        String idGusto = gusto.name(); // Utilizar el nombre del gusto como ID

                        Map<String, Object> data = new HashMap<>();
                        data.put("valor", gusto.name());
                        gustosRef.document(idGusto).set(data).get(); // Guardar con el nombre del gusto

                    }
                    System.out.println("✅ Gustos agregados para el usuario: " + idUsuario);
                }
            } else {
                System.out.println("⚠️ El usuario ya existe: " + idUsuario);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error al verificar/crear usuario: " + e.getMessage());
        }
    }




    public Usuario login(String email, String password) {
        CollectionReference usuariosRef = db.collection("FireMatch").document("Usuarios").collection("ListaUsuarios");
        Query query = usuariosRef.whereEqualTo("email", email).whereEqualTo("password", password);

        try {
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                if (document.exists()) {
                    // Extraer datos del documento
                    int id = Integer.parseInt(document.getId()); // El ID es la clave del documento
                    String nombre = document.getString("nombre");
                    String apellidos = document.getString("apellidos");
                    String nickname = document.getString("nickname");
                    String descripcion = document.getString("descripcion");
                    int edad = document.getLong("edad").intValue();
                    Genero genero = Genero.valueOf(document.getString("genero"));

                    // Obtener características
                    CollectionReference caracteristicasRef = document.getReference().collection("Caracteristicas");
                    ApiFuture<QuerySnapshot> caracteristicasSnapshot = caracteristicasRef.get();
                    ObservableList<String> caracteristicasList = FXCollections.observableArrayList();
                    for (DocumentSnapshot charDoc : caracteristicasSnapshot.get().getDocuments()) {
                        caracteristicasList.add(charDoc.getString("valor"));
                    }

                    // Obtener gustos
                    CollectionReference gustosRef = document.getReference().collection("Gustos");
                    ApiFuture<QuerySnapshot> gustosSnapshot = gustosRef.get();
                    ObservableList<Genero> gustosList = FXCollections.observableArrayList();
                    for (DocumentSnapshot gustoDoc : gustosSnapshot.get().getDocuments()) {
                        gustosList.add(Genero.valueOf(gustoDoc.getString("valor")));
                    }

                    // Crear y retornar el objeto Usuario
                    return new Usuario(id, nombre, apellidos, nickname, email, password, descripcion, edad, genero, caracteristicasList, gustosList);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> GetUsuarios(Usuario usuarioLoged, int edadMin, int edadMax) {
        List<Usuario> usuariosList = new ArrayList<>();
        CollectionReference usuariosRef = db.collection("FireMatch").document("Usuarios").collection("ListaUsuarios");

        System.out.println("Usuario logeado: " + usuarioLoged.getId());
        System.out.println("Edades permitidas: " + edadMin + " - " + edadMax);
        System.out.println("Gustos del usuario logeado: " + usuarioLoged.getGustos());

        try {
            ApiFuture<QuerySnapshot> querySnapshot = usuariosRef.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                if (document.exists()) {
                    int id = Integer.parseInt(document.getId());
                    if (id == usuarioLoged.getId()) {
                        continue; // No incluir al usuario logeado en la lista de resultados
                    }

                    String nombre = document.getString("nombre");
                    String apellidos = document.getString("apellidos");
                    String nickname = document.getString("nickname");
                    String email = document.getString("email");
                    String password = document.getString("password");
                    String descripcion = document.getString("descripcion");

                    // Verificar si la edad es válida
                    Long edadLong = document.getLong("edad");
                    if (edadLong == null) {
                        System.out.println("⚠️ Edad no encontrada para usuario " + id);
                        continue; // Saltar este usuario
                    }
                    int edad = edadLong.intValue();

                    if (edad < edadMin || edad > edadMax) {
                        System.out.println("❌ Usuario " + id + " fuera del rango de edad (" + edad + ")");
                        continue; // Saltar si está fuera del rango de edad
                    }

                    Genero genero;
                    try {
                        genero = Genero.valueOf(document.getString("genero"));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        System.out.println("⚠️ Género inválido o nulo para usuario " + id);
                        continue; // Saltar si el género no es válido
                    }

                    // Obtener características
                    CollectionReference caracteristicasRef = document.getReference().collection("Caracteristicas");
                    ApiFuture<QuerySnapshot> caracteristicasSnapshot = caracteristicasRef.get();
                    ObservableList<String> caracteristicasList = FXCollections.observableArrayList();
                    for (DocumentSnapshot charDoc : caracteristicasSnapshot.get().getDocuments()) {
                        caracteristicasList.add(charDoc.getString("valor"));
                    }

                    // Obtener gustos del usuario actual
                    CollectionReference gustosRef = document.getReference().collection("Gustos");
                    ApiFuture<QuerySnapshot> gustosSnapshot = gustosRef.get();
                    ObservableList<Genero> gustosList = FXCollections.observableArrayList();
                    for (DocumentSnapshot gustoDoc : gustosSnapshot.get().getDocuments()) {
                        try {
                            gustosList.add(Genero.valueOf(gustoDoc.getString("valor")));
                        } catch (IllegalArgumentException | NullPointerException e) {
                            System.out.println("⚠️ Gusto inválido encontrado para usuario " + id);
                        }
                    }

                    // Validar que al menos un gusto del usuario logeado coincide con el género del usuario revisado
                    boolean generoCoincide = usuarioLoged.getGustos().stream().anyMatch(g -> g == genero);

                    if (!generoCoincide) {
                        System.out.println("❌ Usuario " + id + " no coincide en gustos de género.");
                        continue;
                    }

                    // Si pasa todos los filtros, agregar a la lista
                    Usuario usuario = new Usuario(id, nombre, apellidos, nickname, email, password, descripcion, edad, genero, caracteristicasList, gustosList);
                    usuariosList.add(usuario);
                    System.out.println("✅ Usuario agregado: " + id + " - " + nombre);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error al obtener usuarios: " + e.getMessage());
        }

        // Mezclar aleatoriamente la lista de usuarios antes de devolverla
        Collections.shuffle(usuariosList);

        return usuariosList;
    }



}
