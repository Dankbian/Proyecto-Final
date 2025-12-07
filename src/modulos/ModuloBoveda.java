package modulos;

import modelos.Boveda;
import modelos.AlmacenamientoBoveda;

import java.util.Scanner;

public class ModuloBoveda extends ModuloBase {

    private final Boveda boveda;
    private final AlmacenamientoBoveda almacenamiento;
    private final String contrasena;

    public ModuloBoveda(Boveda boveda, AlmacenamientoBoveda almacenamiento, String contrasena, Scanner lector) {
        super(lector); // Inicia el lector del padre
        this.boveda = boveda;
        this.almacenamiento = almacenamiento;
        this.contrasena = contrasena;
    }

    @Override
    public String obtenerNombre() {
        return "Gestor de Contraseñas";
    }

    @Override
    public void ejecutar() throws Exception {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- " + obtenerNombre() + " ---");

            boolean estaVacia = boveda.listarNombresSecretos().isEmpty();

            // Menú dinámico
            System.out.println("1. Agregar secreto");
            if (estaVacia) {
                System.out.println("5. Guardar y Regresar");
            } else {
                System.out.println("2. Listar secretos");
                System.out.println("3. Actualizar secreto");
                System.out.println("4. Ver secreto");
		System.out.println("5. Eliminar secreto");
                System.out.println("6. Guardar y Regresar");
            }
            System.out.print("> ");

            int opcion = leerOpcion();

            // Ajuste de lógica si está vacía (para que el menú coincida)
            if (estaVacia) {
                if (opcion == 5) opcion = 6;
                else if (opcion != 1) opcion = -1;
            }

            switch (opcion) {
                case 1:
                    System.out.print("Nombre del secreto: ");
                    String nombre = lector.nextLine();
                    System.out.print("Valor del secreto: ");
                    String valor = lector.nextLine();
                    boveda.agregarSecreto(nombre, valor);
                    break;
                case 2:
		    System.out.println("--- Lista de secretos ---");
		    boveda.listarNombresSecretos().forEach(System.out::println);
		    break;
		    
                case 3:
                    System.out.print("Nombre del secreto a actualizar: ");
                    String nombreActual = lector.nextLine();
                
                    if (!boveda.listarNombresSecretos().contains(nombreActual)) {
                        System.out.println("ERROR: El secreto no existe.");
                        break;
                    }
                
                    System.out.print("Nuevo nombre del secreto: ");
                    String nuevoNombre = lector.nextLine();
                
                    System.out.print("Nuevo valor del secreto: ");
                    String nuevoValor = lector.nextLine();
                
                    boolean actualizado = boveda.actualizarSecreto(nombreActual, nuevoNombre, nuevoValor);
                
                    if (actualizado) {
                        System.out.println("Secreto actualizado correctamente.");
                    } else {
                        System.out.println("No se pudo actualizar el secreto.");
                    }
                    break;
                    
                case 4:
                        System.out.print("Ingrese palabra clave para buscar: ");
                        String buscar = lector.nextLine().toLowerCase();  // Hacemos la búsqueda insensible a mayúsculas/minúsculas
                        System.out.println("--- Resultados de búsqueda ---");
                    
                        // Búsqueda inteligente osea buscar coincidencias parciales
                        boolean encontrado = false;
                        for (String nombreSecreto : boveda.listarNombresSecretos()) {
                            if (nombreSecreto.toLowerCase().contains(buscar)) {  // Compara el nombre del secreto con el término de búsqueda
                                System.out.println("Secreto encontrado: " + nombreSecreto);
                                encontrado = true;
                            }
                        }
                        
                        if (!encontrado) {
                            System.out.println("No se encontraron secretos con esa palabra clave.");
                        }
                        break;
                    
                case 5:
                    System.out.print("Nombre a eliminar: ");
		    String eliminar = lector.nextLine();
		    boveda.eliminarSecreto(eliminar);
                    break;
	        case 6: 
		    System.out.println("Guardando ...");
		    almacenamiento.guardarBoveda(boveda, contrasena);
		    System.out.println("Guardado exitosamente");
		    continuar = false;
		    break;
		default:
                    System.out.println("Opción inválida.");
            }
        }
    }
}
