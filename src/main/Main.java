package main;

import modelos.Bitacora;
import modelos.Boveda;
import modelos.VerificarContrasena;
import modelos.AlmacenamientoBoveda;
import modulos.ModuloBase;
import modulos.ModuloArchivos;
import modulos.ModuloBoveda;

import java.io.*;
import java.io.Console;
import java.util.Scanner;

public class Main {

    private static final Scanner lector = new Scanner(System.in);
    private static final AlmacenamientoBoveda almacenamiento = new AlmacenamientoBoveda();
    private static Boveda boveda;
    private static String contrasenaActual;

    // --- ÚNICO Método Principal ---
    public static void main(String[] args) {
        Bitacora.info("=== INICIO DE SISTEMA ===");
        System.out.println("--- SUITE DE SEGURIDAD (JAVA POO) ---");

        try {
            // Login (Existe bóveda o no)
            if (almacenamiento.existeBoveda()) {
                Bitacora.info("Bóveda existente detectada, procediendo a login");
                iniciarSesion();
            } else {
                Bitacora.info("No hay bóveda existente, creando nueva");
                crearNuevaBoveda();
            }
            
            ModuloBase moduloBoveda = new ModuloBoveda(boveda, almacenamiento, contrasenaActual, lector);
            ModuloBase moduloArchivos = new ModuloArchivos(contrasenaActual, lector);

            boolean enEjecucion = true;
            while (enEjecucion) {
                System.out.println("\n--- Menú Principal ---");
                System.out.println("1. " + moduloBoveda.obtenerNombre());
                System.out.println("2. " + moduloArchivos.obtenerNombre());
                System.out.println("3. Cambiar contraseña maestra");
                System.out.println("4. Ver bitácora");
                System.out.println("5. Salir");
                System.out.print("> ");

                String opcion = lector.nextLine().trim();

                switch (opcion) {
                    case "1":
                        Bitacora.info("Usuario seleccionó: Gestor de Contraseñas");
                        moduloBoveda.ejecutar();
                        break;
                    case "2":
                        Bitacora.info("Usuario seleccionó: Cifrador de Archivos");
                        moduloArchivos.ejecutar();
                        break;
                    case "3":
                        Bitacora.info("Usuario seleccionó: Cambiar contraseña maestra");
                        cambiarContrasenaMaestra();
                        moduloBoveda = new ModuloBoveda(boveda, almacenamiento, contrasenaActual, lector);
                        moduloArchivos = new ModuloArchivos(contrasenaActual, lector);
                        break;
                    case "4":
                        Bitacora.info("Usuario solicitó ver bitácora");
                        mostrarBitacora();
                        break;
                    case "5":
                        Bitacora.info("Usuario solicitó salir del sistema");
                        System.out.println("Cerrando programa... ¡Adiós!");
                        enEjecucion = false;
                        break;
                    default:
                        Bitacora.warn("Opción no válida seleccionada: " + opcion);
                        System.out.println("Opción no válida.");
                }
            }

        } catch (Exception e) {
            Bitacora.error("Error fatal: " + e.getMessage());
            System.err.println("Error fatal: " + e.getMessage());
        } finally {
            Bitacora.info("=== FIN DE SISTEMA ===");
        }
    }
    
    // --- Método para mostrar Logs ---
    private static void mostrarBitacora() {
        System.out.println("\n=== BITÁCORA DE EVENTOS ===");
        System.out.println("Ubicación: " + Bitacora.obtenerRuta());  // ← Muestra dónde está
        
        try (BufferedReader br = new BufferedReader(new FileReader(Bitacora.obtenerRuta()))) {
            String linea;
            int contador = 0;
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
                contador++;
            }
            if (contador == 0) {
                System.out.println("La bitácora está vacía.");
            } else {
                System.out.println("--- Total: " + contador + " eventos ---");
            }
        } catch (IOException e) {
            System.out.println("No hay bitácora o no se pudo leer.");
        }
        System.out.println("=== FIN BITÁCORA ===\n");
    }

    // --- Métodos de Login ---
    private static void crearNuevaBoveda() throws Exception {
        System.out.println("\n--- Configuración Inicial ---");  
        System.out.println("Bienvenido. Crea una contraseña maestra.");
        
        String nuevaContra = leerContrasena("Nueva contraseña: ", true);
        String nuevaContraVerificada = leerContrasena("Confirme su contraseña: ", false);

        if (!nuevaContra.equals(nuevaContraVerificada)) {
            Bitacora.warn("Intento de creación de bóveda: contraseñas no coinciden");
            throw new Exception("Las contraseñas no coinciden.");
        }

        contrasenaActual = nuevaContra;
        boveda = new Boveda();
        almacenamiento.guardarBoveda(boveda, contrasenaActual);
        Bitacora.info("Nueva bóveda creada exitosamente");
        System.out.println("¡Sistema configurado correctamente!");
    }

    private static void iniciarSesion() throws Exception {
        System.out.println("\n--- Inicio de Sesión ---");
        String contraAlmacenada = leerContrasena("Introduce tu contraseña maestra: ", false);

        try {
            boveda = almacenamiento.cargarBoveda(contraAlmacenada);
            contrasenaActual = contraAlmacenada;
            Bitacora.info("Login exitoso");
            System.out.println("¡Acceso concedido!");
        } catch (Exception e) {
            Bitacora.warn("Intento de login fallido: " + e.getMessage());
            throw e;
        }
    }
    
    private static String leerContrasena(String mensaje, boolean validar) {
        Console console = System.console();
        String contrasena = "";
        boolean bandera = true;

        while (bandera) {
            if (console != null) {
                char[] pass = console.readPassword(mensaje);
                contrasena = new String(pass);
            } else {
                System.out.print(mensaje);
                contrasena = lector.nextLine();
            }

            if (validar) {
                bandera = esNueva(contrasena);
            } else {
                bandera = false;
            }
        }
        return contrasena;
    }

    private static void cambiarContrasenaMaestra() {
        System.out.println("\n--- Cambio de contraseña maestra ---");
        try {
            Bitacora.info("Iniciando cambio de contraseña maestra");
            
            String contraActualIngresada = leerContrasena("Introduce tu contraseña actual: ", false);
            almacenamiento.cargarBoveda(contraActualIngresada);

            String nuevaContra = leerContrasena("Introduce la nueva contraseña: ", true);
            String nuevaContraVerificada = leerContrasena("Confirme la nueva contraseña: ", false);

            if (!nuevaContra.equals(nuevaContraVerificada)) {
                Bitacora.warn("Intento de cambio de contraseña: nuevas contraseñas no coinciden");
                System.out.println("ERROR: Las contraseñas no coinciden");
                return;
            }

            almacenamiento.guardarBoveda(boveda, nuevaContra);
            contrasenaActual = nuevaContra;
            Bitacora.info("Contraseña maestra cambiada con éxito");
            System.out.println("Contraseña maestra cambiada con éxito");
        } catch (Exception e) {
            Bitacora.error("Error al cambiar la contraseña: " + e.getMessage());
            System.err.println("Error al cambiar la contraseña: " + e.getMessage());
        }
    }

    private static boolean esNueva(String contrasena) {
        if (VerificarContrasena.esValida(contrasena)) {
            Bitacora.info("Contraseña validada exitosamente");
            return false;
        } else {
            Bitacora.warn("Validación de contraseña fallida");
            return true;
        }
    }
}
