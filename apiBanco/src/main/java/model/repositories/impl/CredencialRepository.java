package model.repositories.impl;

import model.entities.enums.EPermiso;
import model.entities.impl.CredencialEntity;
import model.repositories.ConexionSQLite;
import model.repositories.interfaces.IRepository;

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
                rs.getInt("id_credencial"),
                rs.getInt("id_usuario"),
                rs.getString("username"),
                rs.getString("password"),
                EPermiso.valueOf(rs.getString("permiso"))
        ));
    }

    @Override
    public void save(CredencialEntity entity) throws SQLException {
        String sql = "INSERT INTO credenciales (id_usuario, username, password, permiso) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getUsuarioId());
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
        String sql = "SELECT * FROM credenciales WHERE id_credencial = ?";
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
        String sql = "DELETE FROM credenciales WHERE id_credencial = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(CredencialEntity entity) throws SQLException {
        String sql = "UPDATE credenciales SET id_usuario = ?, " +
                "username = ?, password = ?, permiso = ? WHERE id_credencial = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getUsuarioId());
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getPassword());
            ps.setString(4, entity.getPermiso().name());
            ps.setInt(5, entity.getId());
            ps.executeUpdate();
        }
    }
    public void deleteByUsuarioID (Integer id_usuario) throws SQLException {
        String sql = "DELETE FROM credenciales WHERE id_usuario = ?";
        try (Connection connection = ConexionSQLite.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id_usuario);
            ps.executeUpdate();
        }
    }
}
