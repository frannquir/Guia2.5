package model.services;

import model.entities.enums.EPermiso;
import model.entities.enums.ETipo;
import model.entities.impl.CredencialEntity;
import model.entities.impl.CuentaEntity;
import model.entities.impl.UsuarioEntity;
import model.exceptions.NoAutorizadoException;
import model.repositories.impl.CredencialRepository;
import model.repositories.impl.CuentaRepository;
import model.repositories.impl.UsuarioRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UsuarioService {
    private static UsuarioService instance;
    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;
    private final CredencialRepository credencialRepository;

    private UsuarioService() {
        usuarioRepository = UsuarioRepository.getInstance();
        cuentaRepository = CuentaRepository.getInstance();
        credencialRepository = CredencialRepository.getInstance();
    }

    public static UsuarioService getInstance() {
        if (instance == null) instance = new UsuarioService();
        return instance;
    }

    /**
     * registrarUsuario() Inserta un usuario, una credencial y una cuenta nueva.
     *
     * @param nombre
     * @param apellido
     * @param dni
     * @param email
     * @return Usuario creado
     */
    public UsuarioEntity registrarUsuario(String nombre, String apellido, String dni, String email) {
        var nuevoUsuario = new UsuarioEntity();
        try {
            boolean dniExistente = usuarioRepository.findAll()
                    .stream()
                    .anyMatch(u -> u.getDni().equals(dni));
            if (dniExistente) {
                System.out.println("Ya existe un usuario con este DNI.");
                return nuevoUsuario;
            }
            // Creación de Usuario
            nuevoUsuario = new UsuarioEntity(
                    nombre, apellido, dni, email
            );
            usuarioRepository.save(nuevoUsuario);
            System.out.println("Creando Usuario..");
            // Creación de Credencial
            CredencialEntity nuevaCredencial = new CredencialEntity(
                    nuevoUsuario.getId(),
                    generarUsername(nuevoUsuario.getEmail()),
                    dni, // la contraseña predeterminada es el DNI.
                    EPermiso.CLIENTE
            );
            credencialRepository.save(nuevaCredencial);
            System.out.println("Creando Credencial..");
            nuevoUsuario.setCredencial(nuevaCredencial);
            // Creación de Cuenta
            CuentaEntity nuevaCuenta = new CuentaEntity(
                    0f,
                    ETipo.CAJA_AHORRO,
                    nuevoUsuario.getId()
            );
            cuentaRepository.save(nuevaCuenta);
            System.out.println("Creando Cuenta..");
            nuevoUsuario.setCuentas(List.of(nuevaCuenta));
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        }
        return nuevoUsuario;
    }

    private String generarUsername(String email) {
        return email.split("@")[0]; // devuelve lo q esta antes del @
    }

    /**
     * iniciarSesion() Valida las credenciales y retorna el usuario autenticado.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @return Usuario autenticado si las credenciales son correctas; vacío si no lo son.
     */
    public UsuarioEntity iniciarSesion(String username, String password) {
        var usuario = new UsuarioEntity();
        try {
            // Busco la credencial del usuario
            CredencialEntity credencial = credencialRepository.findAll()
                    .stream()
                    .filter((c) -> c.getUsername().equalsIgnoreCase(username) && c.getPassword().equals(password))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            Integer id = credencial.getUsuarioId();
            // Busco el usuario
            usuario = usuarioRepository.findByID(id)
                    .orElseThrow(NoSuchElementException::new);
            // Asigno su credencial
            usuario.setCredencial(credencial);
            // Busco las cuentas del usuario
            List<CuentaEntity> cuentasUsuario = cuentaRepository.findAll()
                    .stream()
                    .filter(c -> c.getUsuarioId().equals(id))
                    .toList();
            // Asigno sus cuentas
            usuario.setCuentas(cuentasUsuario);

        } catch (SQLException e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
        }
        return usuario;
    }


    public List<UsuarioEntity> listarUsuarios(CredencialEntity credencial) throws NoAutorizadoException {
        if (credencial.getPermiso() == EPermiso.GESTOR || credencial.getPermiso() == EPermiso.ADMINISTRADOR) {
            try {
                return usuarioRepository.findAll();
            } catch (SQLException e) {
                System.out.println("Error al listar usuarios " + e.getMessage());
                return new ArrayList<>();
            }
        } else {
            throw new NoAutorizadoException("Los CLIENTES no pueden ver el listado de Usuarios.");
        }
    }

    public UsuarioEntity buscarPorId(CredencialEntity credencial, Integer id) throws NoAutorizadoException {
        var usuario = new UsuarioEntity();
        if (credencial.getPermiso() == EPermiso.GESTOR || credencial.getPermiso() == EPermiso.ADMINISTRADOR) {
            try {
                Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findByID(id);
                if (usuarioOpt.isPresent()) {
                    usuario = usuarioOpt.get();
                } else {
                    throw new NoSuchElementException("El usuario no fue encontrado.");
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar el usuario por ID");
            }
        } else {
            throw new NoAutorizadoException("Los CLIENTES no pueden buscar por ID.");
        }
        return usuario;
    }

    public UsuarioEntity buscarPorDni(CredencialEntity credencial, String dni) throws NoAutorizadoException {
        var usuario = new UsuarioEntity();
        if (credencial.getPermiso() == EPermiso.GESTOR || credencial.getPermiso() == EPermiso.ADMINISTRADOR) {
            try {
                usuario = usuarioRepository.findAll()
                        .stream()
                        .filter(u -> u.getDni().equals(dni))
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new);
            } catch (SQLException e) {
                System.out.println("Error al buscar usuario por DNI " + e.getMessage());
            }
        } else {
            throw new NoAutorizadoException("Los CLIENTES no pueden buscar por DNI.");
        }
        return usuario;
    }

    private Integer buscarIdPorDni(String dni) throws SQLException, NoSuchElementException {
        return usuarioRepository.findAll()
                .stream()
                .filter(u -> u.getDni().equals(dni))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No existe usuario con DNI: " + dni))
                .getId();
    }

    public UsuarioEntity buscarPorEmail(CredencialEntity credencial, String email) throws NoAutorizadoException {
        var usuario = new UsuarioEntity();
        if (credencial.getPermiso() == EPermiso.GESTOR || credencial.getPermiso() == EPermiso.ADMINISTRADOR) {
            try {
                usuario = usuarioRepository.findAll()
                        .stream()
                        .filter(u -> u.getEmail().equals(email))
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new);
            } catch (SQLException e) {
                System.out.println("Error al buscar usuario por Email " + e.getMessage());
            }
        } else {
            throw new NoAutorizadoException("Los CLIENTES no pueden buscar por Email.");
        }
        return usuario;
    }

    public boolean actualizarUsuario(UsuarioEntity usuario, CredencialEntity credencialSolicitante) throws NoAutorizadoException {
        try {
            switch (credencialSolicitante.getPermiso()) {
                case CLIENTE -> {
                    if (!credencialSolicitante.getUsuarioId().equals(usuario.getId()))
                        throw new NoAutorizadoException("Como CLIENTE, solo podes actualizar tus datos.");
                }
                case GESTOR -> {
                    if (!usuario.getCredencial().getPermiso().equals(EPermiso.CLIENTE))
                        throw new NoAutorizadoException("Como GESTOR, solo podes actualizar CLIENTES.");
                }
                case ADMINISTRADOR -> {
                } // Admin puede actualizar cualquier usuario.

                default -> throw new NoAutorizadoException("Permiso no reconocido");
            }
            // Si llegamos hasta aca, significa que los permisos son correctos.
            usuarioRepository.update(usuario);
            return true;
        } catch (SQLException e) {
            System.out.println("Error al actualizar Usuario " + e.getMessage());
            return false;
        }
    }

    public void eliminarUsuario(CredencialEntity credencial, String dni) throws NoAutorizadoException {
        try {
            Integer id = buscarIdPorDni(dni);
            UsuarioEntity usuarioAEliminar = usuarioRepository.findByID(id)
                    .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con DNI: " + dni));

            switch (credencial.getPermiso()) {
                case CLIENTE -> { // Auto-eliminacion
                    if (!credencial.getUsuarioId().equals(id)) {
                        throw new NoAutorizadoException("Como CLIENTE, solo podés eliminarte a vos mismo.");
                    }
                    eliminarDepedendencias(id);
                }
                case GESTOR -> {
                    if (usuarioAEliminar.getCredencial().getPermiso() == EPermiso.ADMINISTRADOR) {
                        throw new NoAutorizadoException("Si sos GESTOR no podés eliminar ADMINISTRADOR");
                    } else {
                        eliminarDepedendencias(id);
                    }
                }
                case ADMINISTRADOR -> eliminarDepedendencias(id);
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar el usuario: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarDepedendencias(Integer id) throws SQLException {
        usuarioRepository.deleteByID(id);
        credencialRepository.deleteByUsuarioID(id);
        cuentaRepository.deleteByUsuarioID(id);
        System.out.println("Usuario y cuentas eliminado.");
    }

    public List<CuentaEntity> listarCuentasUsuario(CredencialEntity credencial, String dni) throws NoAutorizadoException {
        try {
            Integer id;

            if (credencial.getPermiso() == EPermiso.CLIENTE) {
                // Si es cliente, solo puede ver sus propias cuentas
                id = credencial.getUsuarioId();
                // Verificar que el DNI corresponda al usuario actual
                UsuarioEntity usuario = usuarioRepository.findByID(id)
                        .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
                if (!usuario.getDni().equals(dni)) {
                    throw new NoAutorizadoException("Como CLIENTE, solo puedes ver tus propias cuentas");
                }
            } else {
                // Si es gestor o admin, puede ver cuentas de cualquier usuario
                id = buscarIdPorDni(dni);
            }

            return cuentaRepository.findAll()
                    .stream()
                    .filter(cuenta -> cuenta.getUsuarioId().equals(id))
                    .toList();

        } catch (SQLException e) {
            System.out.println("Error al listar cuentas de usuario: " + e.getMessage());
            return new ArrayList<>();
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }

    public Float obtenerSaldoUsuario(CredencialEntity credencial, String dni) throws NoAutorizadoException {
        Float saldo;
        try {
            Optional<UsuarioEntity> usuarioActualOpt = usuarioRepository.findByDni(dni);
            Optional<UsuarioEntity> usuarioAObtenerOpt = usuarioRepository.findByID(credencial.getId());
            if(usuarioAObtenerOpt.isPresent()) {
                UsuarioEntity usuario = usuarioAObtenerOpt.get();
                if(usuarioActualOpt.equals(usuarioAObtenerOpt)) { // cualquier usuario puede ver su saldo.
                    saldo = usuario.getCuentas().stream().map(CuentaEntity::getSaldo).reduce(0f, Float::sum);
                    return saldo;
                } else {
                    if(credencial.getPermiso() == EPermiso.CLIENTE) { // un cliente pide el saldo de otra cuenta
                        throw new NoAutorizadoException("Los CLIENTES solo pueden ver su saldo.");
                    } else { // admin y gestor pueden ver cualquier saldo
                        saldo = usuario.getCuentas().stream().map(CuentaEntity::getSaldo).reduce(0f, Float::sum);
                        return saldo;
                    }
                }
            } else {
                throw new NoSuchElementException();
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener saldo del usuario");
        } catch (NoSuchElementException e) {
            System.out.println("Usuario no encontrado");
        }
        return 0.0f;
    }
}