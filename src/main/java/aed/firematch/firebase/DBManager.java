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
        try {
            ApiFuture<QuerySnapshot> querySnapshot = usuariosRef.get();
            for (DocumentSnapshot document : querySnapshot.get().getDocuments()) {
                if (document.exists()) {
                    int id = Integer.parseInt(document.getId());
                    if (id == usuarioLoged.getId()) {
                        continue;
                    }

                    String nombre = document.getString("nombre");
                    String apellidos = document.getString("apellidos");
                    String nickname = document.getString("nickname");
                    String email = document.getString("email");
                    String password = document.getString("password");
                    String descripcion = document.getString("descripcion");
                    int edad = document.getLong("edad").intValue();
                    Genero genero = Genero.valueOf(document.getString("genero"));

                    CollectionReference caracteristicasRef = document.getReference().collection("Caracteristicas");
                    ApiFuture<QuerySnapshot> caracteristicasSnapshot = caracteristicasRef.get();
                    ObservableList<String> caracteristicasList = FXCollections.observableArrayList();
                    for (DocumentSnapshot charDoc : caracteristicasSnapshot.get().getDocuments()) {
                        caracteristicasList.add(charDoc.getString("valor"));
                    }

                    CollectionReference gustosRef = document.getReference().collection("Gustos");
                    ApiFuture<QuerySnapshot> gustosSnapshot = gustosRef.get();
                    ObservableList<Genero> gustosList = FXCollections.observableArrayList();
                    for (DocumentSnapshot gustoDoc : gustosSnapshot.get().getDocuments()) {
                        gustosList.add(Genero.valueOf(gustoDoc.getString("valor")));
                    }

                    if (usuarioLoged.getGustos().contains(genero) && edad >= edadMin && edad <= edadMax) {
                        Usuario usuario = new Usuario(id, nombre, apellidos, nickname, email, password, descripcion, edad, genero, caracteristicasList, gustosList);
                        usuariosList.add(usuario);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error al obtener usuarios: " + e.getMessage());
        }

        Collections.shuffle(usuariosList);

        return usuariosList;
    }


}
