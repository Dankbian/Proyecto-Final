package modelos;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bitacora {
    private static final String NOMBRE_ARCHIVO = "audit.log";
    private static File archivoBitacora;
    private static boolean mostrarEnConsola = false; // Control para mostrar/ocultar en consola
    
    static {
        // Inicializar la ruta del archivo de bitácora al cargar la clase
        determinarRutaArchivo();
    }
    
    private static void determinarRutaArchivo() {
        try {
            // Usar la misma lógica que AlmacenamientoBoveda para encontrar la raíz
            File raizProyecto = encontrarRaizProyecto();
            archivoBitacora = new File(raizProyecto, NOMBRE_ARCHIVO);
            
            // SOLO mostrar esto en consola la primera vez (opcional)
            // System.out.println("✓ Bitácora ubicada en: " + archivoBitacora.getAbsolutePath());
            
        } catch (Exception e) {
            // Fallback: usar directorio actual
            archivoBitacora = new File(NOMBRE_ARCHIVO);
            // Si hay error, sí mostrarlo
            System.err.println("Advertencia: No se pudo determinar ruta de bitácora: " + e.getMessage());
        }
    }
    
    private static File encontrarRaizProyecto() {
        File directorioActual = new File(System.getProperty("user.dir"));
        
        // Subir hasta encontrar marcadores del proyecto
        File raiz = directorioActual;
        while (raiz != null) {
            // Marcadores de que ESTA es la raíz del proyecto
            File srcDir = new File(raiz, "src");
            File ideaDir = new File(raiz, ".idea");
            File imlFile = new File(raiz, "Proyecto-Final.iml");
            File gitignore = new File(raiz, ".gitignore");
            
            if (srcDir.exists() || ideaDir.exists() || 
                imlFile.exists() || gitignore.exists()) {
                return raiz; // ¡Esta es la raíz!
            }
            
            if (raiz.getParentFile() == null) {
                break; // Llegamos al directorio raíz
            }
            
            raiz = raiz.getParentFile(); // Subir un nivel
        }
        
        // Si no encontramos marcadores, usar directorio actual
        return directorioActual;
    }
    
    public static void log(String nivel, String mensaje) {
        try {
            // Crear marca de tiempo
            String tiempo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            
            // Crear línea del log
            String linea = "[" + tiempo + "] [" + nivel + "] " + mensaje + "\n";
            
            // Escribir en archivo (siempre en raíz del proyecto)
            try (FileWriter fw = new FileWriter(archivoBitacora, true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(linea);
            }
            
            // Solo mostrar en consola si la bandera está activada
            if (mostrarEnConsola) {
                System.out.println("[" + nivel + "] " + mensaje);
            }
            
        } catch (IOException e) {
            // En este caso SÍ mostrar error en consola
            System.err.println("Error escribiendo en bitácora: " + e.getMessage());
        }
    }
    
    // Métodos rápidos
    public static void info(String mensaje) {
        log("INFO", mensaje);
    }
    
    public static void warn(String mensaje) {
        log("WARN", mensaje);
    }
    
    public static void error(String mensaje) {
        log("ERROR", mensaje);
    }
    
    /**
     * Obtiene la ruta actual del archivo de bitácora
     */
    public static String obtenerRuta() {
        return archivoBitacora.getAbsolutePath();
    }
    
    /**
     * Activa/desactiva la visualización de logs en consola
     */
    public static void setMostrarEnConsola(boolean mostrar) {
        mostrarEnConsola = mostrar;
    }
}
