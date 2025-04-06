package model.services;

import model.entities.enums.EPermiso;
import model.entities.enums.ETipo;
import model.entities.impl.CredencialEntity;
import model.entities.impl.CuentaEntity;
import model.entities.impl.UsuarioEntity;
import model.repositories.impl.CredencialRepository;
import model.repositories.impl.CuentaRepository;
import model.repositories.impl.UsuarioRepository;

import java.sql.SQLException;
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
            // Creación de Usuario
            nuevoUsuario = new UsuarioEntity(
                    nombre, apellido, dni, email
            );
            usuarioRepository.save(nuevoUsuario);

            // Creación de Credencial
            CredencialEntity nuevaCredencial = new CredencialEntity(
                    nuevoUsuario.getId(),
                    generarUsername(nuevoUsuario.getEmail()),
                    dni, // la contraseña predeterminada es el DNI.
                    EPermiso.CLIENTE
            );
            credencialRepository.save(nuevaCredencial);

            // Creación de Cuenta
            CuentaEntity nuevaCuenta = new CuentaEntity(
                    0f,
                    ETipo.CAJA_AHORRO,
                    nuevoUsuario.getId()
            );
            cuentaRepository.save(nuevaCuenta);

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
                    .filter((c) -> c.getUsername().equals(username) && c.getPassword().equals(password))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            usuario = usuarioRepository.findByID(credencial.getUsuarioId())
                    .orElseThrow(NoSuchElementException::new);
        } catch (SQLException e) {
            System.err.println("Error al iniciar sesión: " + e.getMessage());
        }
        return usuario;
    }
}

