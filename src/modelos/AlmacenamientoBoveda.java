package modelos;

import java.io.*;
import javax.crypto.SecretKey;

public class AlmacenamientoBoveda {

    private static final String NOMBRE_ARCHIVO = "boveda.dat";
    private File archivoBoveda;

    public AlmacenamientoBoveda() {
        // Buscar la raíz REAL del proyecto
        File raizProyecto = encontrarRaizProyecto();
        archivoBoveda = new File(raizProyecto, NOMBRE_ARCHIVO);

        // Asegurar que el directorio padre exista
        File directorioPadre = archivoBoveda.getParentFile();
        if (directorioPadre != null && !directorioPadre.exists()) {
            if (!directorioPadre.mkdirs()) {
                System.err.println("Advertencia: No se pudo crear el directorio para la bóveda");
            }
        }

        System.out.println("Directorio actual: " + System.getProperty("user.dir"));
        System.out.println("Raíz del proyecto: " + raizProyecto.getAbsolutePath());
        System.out.println("✓ Bóveda en raíz: " + archivoBoveda.getAbsolutePath());
    }

    private File encontrarRaizProyecto() {
        File directorioActual = new File(System.getProperty("user.dir"));
        System.out.println("DEBUG: Buscando raíz desde: " + directorioActual.getAbsolutePath());

        // Subir hasta encontrar marcadores del proyecto
        File raiz = directorioActual;
        while (raiz != null) {
            // Marcadores de que ESTA es la raíz
            boolean tieneSrc = new File(raiz, "src").exists();
            boolean tieneIdea = new File(raiz, ".idea").exists();
            boolean tieneIml = new File(raiz, "ProyectoFinal.iml").exists();
            boolean tieneGitignore = new File(raiz, ".gitignore").exists();

            if (tieneSrc || tieneIdea || tieneIml || tieneGitignore) {
                System.out.println("DEBUG: Marcador encontrado en: " + raiz.getAbsolutePath());
                return raiz;
            }

            // Si estamos en una carpeta de clases compiladas (main, modelos, modulos)
            // también necesitamos subir
            String nombreActual = raiz.getName();
            if (nombreActual.equals("main") || nombreActual.equals("modelos") || nombreActual.equals("modulos")) {
                System.out.println("DEBUG: Estamos en carpeta de clases: " + nombreActual);
                // Continuar subiendo
            }

            if (raiz.getParentFile() == null) break;
            raiz = raiz.getParentFile();
        }

        System.out.println("DEBUG: No se encontró raíz, usando directorio actual");
        return directorioActual; // Fallback
    }

    public boolean existeBoveda() {
        return archivoBoveda.exists();
    }

    public Boveda cargarBoveda(String contrasena) throws Exception {
        try (FileInputStream archivoEntrada = new FileInputStream(archivoBoveda)) {

            byte[] datosCifrados = archivoEntrada.readAllBytes();
            SecretKey clave = UtilidadesCifrado.obtenerClaveDesdeContrasena(contrasena);
            byte[] datosDescifrados = UtilidadesCifrado.descifrar(datosCifrados, clave);
            return Boveda.crearDesdeBytes(datosDescifrados);

        } catch (javax.crypto.BadPaddingException e) {
            throw new Exception("Contraseña incorrecta o archivo dañado.");
        } catch (FileNotFoundException e) {
            throw new Exception("No se encontró el archivo de bóveda en: " + archivoBoveda.getAbsolutePath());
        }
    }

    public void guardarBoveda(Boveda boveda, String contrasena) throws Exception {

        SecretKey clave = UtilidadesCifrado.obtenerClaveDesdeContrasena(contrasena);
        byte[] datosOriginales = boveda.convertirABytes();
        byte[] datosCifrados = UtilidadesCifrado.cifrar(datosOriginales, clave);

        try (FileOutputStream archivoSalida = new FileOutputStream(archivoBoveda)) {
            archivoSalida.write(datosCifrados);
            System.out.println("✓ Bóveda guardada en: " + archivoBoveda.getAbsolutePath());
        }
    }
}
