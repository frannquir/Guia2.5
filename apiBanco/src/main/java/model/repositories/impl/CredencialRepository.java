package model.repositories.impl;

import model.entities.enums.EPermiso;
import model.entities.impl.CredencialEntity;
import model.entities.impl.CuentaEntity;
import model.entities.impl.UsuarioEntity;
import model.repositories.ConexionSQLite;
import model.repositories.interfaces.IRepository;

import javax.security.auth.login.CredentialNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CredencialRepository implements IRepository<CredencialEntity> {
    private static CredencialRepository instance;

    private CredencialRepository() {
    }

    public static CredencialRepository getInstance() {
        if (instance == null) instance = new CredencialRepository();
        return instance;
    }

    private Optional<CredencialEntity> resultToCredencial(ResultSet rs) throws SQLException {
        return Optional.of(new CredencialEntity(
                rs.getInt("id"),
                rs.getInt("alumnoId"),
                rs.getString("username"),
                rs.getString("password"),
                EPermiso.valueOf(rs.getString("permiso"))
        ));
    }

    @Override
    public void save(CredencialEntity entity) throws SQLException {
        String sql = "INSERT INTO credenciales (alumnoId, username, password, permiso) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getAlumnoId());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getPermiso().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    entity.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<CredencialEntity> findAll() throws SQLException {
        String sql = "SELECT * FROM credenciales";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<CredencialEntity> credenciales = new ArrayList<>();
            while (rs.next()) {
                Optional<CredencialEntity> credencial = resultToCredencial(rs);

                credencial.ifPresent(credenciales::add);
            }
            return credenciales;
        }
    }

    @Override
    public Optional<CredencialEntity> findByID(Integer id) throws SQLException {
        String sql = "SELECT * FROM credenciales WHERE id = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultToCredencial(rs);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public void deleteByID(Integer id) throws SQLException {
        String sql = "DELETE FROM credenciales WHERE id = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(CredencialEntity entity) throws SQLException {
        String sql = "UPDATE credenciales SET alumnoId = ?, " +
                "username = ?, password = ?, permiso = ? WHERE id = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getAlumnoId());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getPermiso().name());
            ps.setInt(5, entity.getId());
            ps.executeUpdate();
        }
    }
}
