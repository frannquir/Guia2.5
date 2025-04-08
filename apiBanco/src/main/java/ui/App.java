package ui;

import model.entities.enums.EPermiso;
import model.entities.impl.CredencialEntity;
import model.entities.impl.CuentaEntity;
import model.entities.impl.UsuarioEntity;
import model.exceptions.NoAutorizadoException;
import model.services.CredencialService;
import model.services.CuentaService;
import model.services.UsuarioService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class App {
    private final CredencialService credencialService;
    private final CuentaService cuentaService;
    private final UsuarioService usuarioService;
    private final Scanner scanner;
    private UsuarioEntity usuarioActual;
    private CredencialEntity credencialActual;

    public App() {
        credencialService = CredencialService.getInstance();
        cuentaService = CuentaService.getInstance();
        usuarioService = UsuarioService.getInstance();
        scanner = new Scanner(System.in);
        usuarioActual = null;
        credencialActual = null;
    }

    public void run() {
        int opcion;
        boolean salir = false;
        boolean sesionIniciada = false;
        do {
            if (!sesionIniciada) {
                // Mostrar menú inicial (registro/login)
                menuLogin();
                opcion = leerOpcion(scanner);

                switch (opcion) {
                    case 1 -> registrarUsuario(scanner);
                    case 2 -> sesionIniciada = iniciarSesion(scanner);
                    case 0 -> salir = true;
                    default -> System.out.println("Opción no válida. Intente nuevamente.");
                }
            } else {
                mostrarMenu();
                opcion = leerOpcion(scanner);

                cuentaService.depositar(14, 500.0f);
                switch (opcion) {
                    case 1 -> listarUsuarios();
                    case 2 -> buscarUsuario(scanner);
                    case 3 -> modificarUsuario(scanner);
                    case 4 -> salir = eliminarUsuario(scanner); // Si el usuario se auto-elimino, lo deslogueo.
                    case 5 -> listarCuentasUsuario(scanner);
                    case 6 -> obtenerSaldoUsuario(scanner);
//                    case 7 -> realizarDeposito(scanner);
//                    case 8 -> realizarTransferencia(scanner);
//                    case 9 -> visualizarUsuariosPorPermiso();
//                    case 10 -> cantidadCuentasPorTipo();
//                    case 11 -> usuarioConMayorSaldo();
//                    case 12 -> listarUsuariosPorSaldo();
//                    case 13 -> crearCuentaCorriente();
//                    case 14 -> cambiarContrasena(scanner);
                    case 0 -> {
                        usuarioActual = null;
                        credencialActual = null;
                        sesionIniciada = false;
                        System.out.println("Cerraste sesión. ¡Nos vemos!");
                    }
                    default -> System.out.println("Opción no válida. Intentalo de nuevo.");
                }
            }
        } while (!salir);
    }

    private void menuLogin() {
        System.out.println("==== SISTEMA BANCARIO ====");
        System.out.println("1. Registrarse");
        System.out.println("2. Iniciar Sesión");
        System.out.println("0. Salir");
        System.out.print("Ingresá una opción: ");
    }

    private int leerOpcion(Scanner scanner) {
        // verifico la opcion
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // opcion invalida
        }
    }

    private void mostrarMenu() {
        System.out.println("==== SISTEMA BANCARIO ====");
        System.out.println("Usuario: " + usuarioActual.getNombre() + " " + usuarioActual.getApellido());
        System.out.println("1. Listar Usuarios");
        System.out.println("2. Buscar Usuario");
        System.out.println("3. Modificar Usuario");
        System.out.println("4. Eliminar Usuario");
        System.out.println("5. Listar las Cuentas de un Usuario");
        System.out.println("6. Obtener el saldo de un usuario");
        System.out.println("7. Realizar un depósito");
        System.out.println("8. Realizar una transferencia");
        System.out.println("9. Visualizar Usuarios por Permiso");
        System.out.println("10. Cantidad de Cuentas por Tipo");
        System.out.println("11. Usuario con Mayor Saldo");
        System.out.println("12. Listar Usuarios por Saldo");
        System.out.println("13. Crear nueva cuenta corriente");
        System.out.println("14. Cambiar contraseña");
        System.out.println("0. Cerrar Sesión");
        System.out.println("Ingresá una opción: ");
    }

    private void registrarUsuario(Scanner scanner) {
        System.out.println("\n==== REGISTRO DE USUARIO ====");
        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.println("DNI: ");
        String dni = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();

        UsuarioEntity usuario = usuarioService.registrarUsuario(nombre, apellido, dni, email);
        if (usuario.getId() != null) { // Verifico que se haya registrado
            credencialActual = usuario.getCredencial();
            System.out.println("¡Usuario registrado con éxito!");
            System.out.println("Tu nombre de usuario es: " + credencialActual.getUsername()); // Muestro user generado
            System.out.println("Tu contraseña inicial es tu DNI: " + credencialActual.getPassword());
        } else {
            System.out.println("No se pudo registrar el usuario. Intentalo de nuevo.");
        }
    }

    private boolean iniciarSesion(Scanner scanner) {
        System.out.println("==== INICIO DE SESIÓN ====");
        System.out.println("Usuario: ");
        String username = scanner.nextLine();
        System.out.println("Contraseña: ");
        String password = scanner.nextLine();
        try {
            usuarioActual = usuarioService.iniciarSesion(username, password);
            if (usuarioActual.getId() != null) {
                credencialActual = usuarioActual.getCredencial();
                System.out.println("Bienvenido, " + credencialActual.getUsername() + "!");
                return true;
            }
        } catch (NoSuchElementException e) {
            System.out.println("Credenciales incorrectas.");
        }
        return false;
    }

    private void listarUsuarios() {
        try {
            System.out.println("\n==== LISTA DE USUARIOS ====");
            List<UsuarioEntity> usuarios = usuarioService.listarUsuarios(credencialActual);
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
                return;
            }

            System.out.println("ID | Nombre | Apellido | DNI | Email");
            for (UsuarioEntity usuario : usuarios) {
                System.out.println(usuario.getId() + " | " +
                        usuario.getNombre() + " | " +
                        usuario.getApellido() + " | " +
                        usuario.getDni() + " | " +
                        usuario.getEmail());
            }
        } catch (NoAutorizadoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarUsuario(Scanner scanner) {
        System.out.println("\n==== BUSCAR USUARIO ====");
        System.out.println("1. Buscar por DNI");
        System.out.println("2. Buscar por Email");
        System.out.println("3. Buscar por ID");
        System.out.print("Seleccione una opción: ");
        int opcion = leerOpcion(scanner);

        try {
            UsuarioEntity usuario = null;

            switch (opcion) {
                case 1 -> {
                    System.out.println("Ingrese el DNI: ");
                    String dni = scanner.nextLine();
                    usuario = usuarioService.buscarPorDni(credencialActual, dni);
                }
                case 2 -> {
                    System.out.println("Ingrese el Email: ");
                    String email = scanner.nextLine();
                    usuario = usuarioService.buscarPorEmail(credencialActual, email);
                }
                case 3 -> {
                    System.out.println("Ingrese la ID: ");
                    Integer id = leerOpcion(scanner);
                    usuario = usuarioService.buscarPorId(credencialActual, id);
                }
                default -> {
                    System.out.println("Opción no válida.");
                    return;
                }
            }

            if (usuario != null && usuario.getId() != null) {
                System.out.println("\nUsuario encontrado:");
                System.out.println("ID: " + usuario.getId());
                System.out.println("Nombre: " + usuario.getNombre());
                System.out.println("Apellido: " + usuario.getApellido());
                System.out.println("DNI: " + usuario.getDni());
                System.out.println("Email: " + usuario.getEmail());
            }
        } catch (NoAutorizadoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Usuario no encontrado.");
        }
    }

    private void modificarUsuario(Scanner scanner) {
        System.out.println("\n==== MODIFICAR USUARIO ====");
        UsuarioEntity usuarioAModificar = null;

        try {
            if (credencialActual.getPermiso() == EPermiso.CLIENTE) {
                System.out.println("Como CLIENTE, solo podes modificar tus datos.");
                usuarioAModificar = usuarioActual; // Si el usuario actual es CLIENTE, solo puede modificar sus datos.
            } else {
                System.out.println("Ingrese el DNI del usuario a modificar: ");
                String dni = scanner.nextLine();
                usuarioAModificar = usuarioService.buscarPorDni(credencialActual, dni);
            }
            System.out.println("\nDatos actuales del usuario:");
            System.out.println("Nombre: " + usuarioAModificar.getNombre());
            System.out.println("Apellido: " + usuarioAModificar.getApellido());
            System.out.println("Email: " + usuarioAModificar.getEmail());

            System.out.println("\nIngrese ENTER si desea no modificar el campo.");

            System.out.println("Nuevo nombre: ");
            String nuevoNombre = scanner.nextLine();
            if (!nuevoNombre.isBlank())  // a diferencia de isEmpty, isBlank retorna 1 si tiene espacios
                usuarioAModificar.setNombre(nuevoNombre);

            System.out.print("Nuevo apellido: ");
            String nuevoApellido = scanner.nextLine();
            if (!nuevoApellido.isBlank())
                usuarioAModificar.setApellido(nuevoApellido);


            System.out.print("Nuevo email: ");
            String nuevoEmail = scanner.nextLine();
            if (!nuevoEmail.isBlank())
                usuarioAModificar.setEmail(nuevoEmail);

            boolean actualizado = usuarioService.actualizarUsuario(usuarioAModificar, credencialActual);

            if(actualizado) {
                System.out.println("Usuario actualizado correctamente.");
                if (usuarioAModificar.getId().equals(usuarioActual.getId())) { // Actualizo al usuario actual, en caso de ser una auto-modificacion
                    usuarioActual = usuarioAModificar;
                }
            } else {
                System.out.println("No se pudo actualizar el usuario");
            }

        } catch (NoAutorizadoException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("Usuario no encontrado");
        }
    }

    private boolean eliminarUsuario(Scanner scanner) {
        System.out.println("\n==== ELIMINAR USUARIO ====");
        try {
            System.out.println("Ingrese el DNI del Usuario a eliminar: ");
            String dni = scanner.nextLine();
            System.out.println("Está seguro que desea eliminar al usuario con DNI " + dni + "? s/n");
            String opcion = scanner.nextLine();
            if(opcion.toLowerCase().startsWith("s")) {
                usuarioService.eliminarUsuario(credencialActual, dni);
                // Verificar si el usuario eliminado es el actual
                if(usuarioActual.getDni().equals(dni)) {
                    return true; // Indica que hay que cerrar sesión
                }
            }
        } catch (NoAutorizadoException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("No se encontró el usuario.");
        }
        return false;
    }

    private void listarCuentasUsuario(Scanner scanner) {
        System.out.println("\n==== LISTAR CUENTAS DE USUARIO ====");
        try {
            String dni;
            if (credencialActual.getPermiso() == EPermiso.CLIENTE) {
                // Si es cliente, solo muestra sus propias cuentas
                dni = usuarioActual.getDni();
                System.out.println("Mostrando tus cuentas:");
            } else {
                // Si es gestor o admin, puede ver cuentas de cualquier usuario
                System.out.println("Ingrese el DNI del usuario cuyas cuentas desea listar: ");
                dni = scanner.nextLine();
            }

            List<CuentaEntity> cuentas = usuarioService.listarCuentasUsuario(credencialActual, dni);

            if (cuentas.isEmpty()) {
                System.out.println("El usuario no tiene cuentas registradas.");
                return;
            }

            System.out.println("ID | Tipo | Saldo | Fecha Creación");
            for (CuentaEntity cuenta : cuentas) {
                System.out.println(cuenta.getId() + " | " +
                        cuenta.getTipo().getTipo() + " | " +
                        cuenta.getSaldo() + " | " +
                        cuenta.getFecha_creacion());
            }
        } catch (NoAutorizadoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private void obtenerSaldoUsuario(Scanner scanner){
        System.out.println("\n==== OBTENER SALDO USUARIO ====");
        try{
            System.out.println("Ingrese el DNI del Usuario: ");
            String dni = scanner.nextLine();
            if(usuarioActual.getDni().equals(dni)) {
                System.out.println("Saldo Total: $" + usuarioActual.getSaldoTotal());
            } else {
                System.out.println("Saldo Total: $" + usuarioService.obtenerSaldoUsuario(credencialActual, dni));
            }
        } catch (NoAutorizadoException e) {
            System.out.println(e.getMessage());
        }
    }
}