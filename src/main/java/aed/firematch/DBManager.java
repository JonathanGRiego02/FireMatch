package aed.firematch;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DBManager {

    private Firestore db;

    public DBManager() {
        try {
            // Cargar el archivo JSON desde la carpeta resources
            InputStream serviceAccount = DBManager.class.getClassLoader()
                    .getResourceAsStream("firematch-d3817-firebase-adminsdk.json");

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
    public void crearUsuario(String idUsuario, String nombre, String apellidos, String email, String password, String descripcion) {
        CollectionReference usuariosRef = db.collection("FireMatch").document("Usuarios").collection("ListaUsuarios");
        DocumentReference usuarioDoc = usuariosRef.document(idUsuario);

        // Datos del usuario
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("nombre", nombre);
        usuarioData.put("apellidos", apellidos);
        usuarioData.put("email", email);
        usuarioData.put("password", password);
        usuarioData.put("descripcion", descripcion);
        usuarioData.put("fotoPerfil", null);
        usuarioData.put("caracteristicas", null);

        // Verificar si el usuario ya existe de forma síncrona
        try {
            if (!usuarioDoc.get().get().exists()) {
                usuarioDoc.set(usuarioData)
                        .get(); // Espera la ejecución
                System.out.println("Usuario creado con éxito: " + idUsuario);
            } else {
                System.out.println("El usuario ya existe: " + idUsuario);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error al verificar/crear usuario: " + e.getMessage());
        }
    }

}
