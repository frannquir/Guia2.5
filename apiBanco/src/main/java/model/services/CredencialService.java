package model.services;

import model.entities.enums.EPermiso;
import model.entities.impl.CredencialEntity;
import model.repositories.impl.CredencialRepository;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CredencialService {
    private static CredencialService instance;
    private final CredencialRepository credencialRepository;

    private CredencialService() {
        credencialRepository = CredencialRepository.getInstance();
    }

    public static CredencialService getInstance() {
        if (instance == null) instance = new CredencialService();
        return instance;
    }

    /**
     * Cambia la contraseña de un usuario.
     * @param usuarioId ID del usuario
     * @param viejaPassword Contraseña actual
     * @param nuevaPassword Nueva contraseña
     * @return true si fue exitoso, false si falló
     */
    public boolean cambiarPassword(Integer usuarioId, String viejaPassword, String nuevaPassword) {
        try {
            CredencialEntity credencial = buscarPorUsuarioId(usuarioId);
            if (credencial.getPassword().equals(viejaPassword)) {
                credencial.setPassword(nuevaPassword);
                credencialRepository.update(credencial);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el permiso de un usuario (solo para administradores).
     * @param usuarioId ID del usuario a modificar
     * @param nuevoPermiso Nuevo permiso (CLIENTE, GESTOR, ADMINISTRADOR)
     */
    public boolean actualizarPermiso(Integer usuarioId, EPermiso nuevoPermiso) {
        try {
            CredencialEntity credencial = buscarPorUsuarioId(usuarioId);
            if(credencial.getPermiso() == EPermiso.ADMINISTRADOR) {
                credencial.setPermiso(nuevoPermiso);
                credencialRepository.update(credencial);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al actualizar permiso: " + e.getMessage());
            return false;
        }
    }

    public CredencialEntity buscarPorUsuarioId(Integer usuarioId)  {
        var credencial = new CredencialEntity();
        try {
            credencial = credencialRepository.findAll().stream()
                    .filter(c -> c.getUsuarioId().equals(usuarioId))
                    .findFirst().orElseThrow(NoSuchElementException::new);
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por id " + e.getMessage());
        }
        return credencial;
    }
}
