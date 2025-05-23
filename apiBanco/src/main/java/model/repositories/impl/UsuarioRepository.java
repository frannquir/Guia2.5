package model.repositories.impl;

import model.entities.impl.UsuarioEntity;
import model.repositories.ConexionSQLite;
import model.repositories.interfaces.IRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository implements IRepository<UsuarioEntity> {
    private static UsuarioRepository instance;

    private UsuarioRepository() {
    }

    public static UsuarioRepository getInstance() {
        if (instance == null) instance = new UsuarioRepository();
        return instance;
    }

    private Optional<UsuarioEntity> resultToUsuario(ResultSet rs) throws SQLException {
        return Optional.of(new UsuarioEntity(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("dni"),
                rs.getString("email"),
                rs.getTimestamp("fecha_creacion").toLocalDateTime()
        ));
    }

    @Override
    public void save(UsuarioEntity entity) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, apellido, dni, " +
                "email) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement
                     (sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getApellido());
            ps.setString(3, entity.getDni());
            ps.setString(4, entity.getEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    entity.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<UsuarioEntity> findAll() throws SQLException {
        String sql = "SELECT * FROM usuarios";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            var usuarios = new ArrayList<UsuarioEntity>();
            while (rs.next()) {
                Optional<UsuarioEntity> usuario = resultToUsuario(rs);

                usuario.ifPresent(usuarios::add);
            }
            return usuarios;
        }
    }


    @Override
    public Optional<UsuarioEntity> findByID(Integer id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultToUsuario(rs);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public Optional<UsuarioEntity> findByDni(String dni) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE dni = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultToUsuario(rs);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public void deleteByID(Integer id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(UsuarioEntity entity) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?," +
                " dni = ?, email = ?, fecha_creacion = ? WHERE id_usuario = ?";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getNombre());
            ps.setString(2, entity.getApellido());
            ps.setString(3, entity.getDni());
            ps.setString(4, entity.getEmail());
            ps.setTimestamp(5, Timestamp.valueOf(entity.getFecha_creacion()));
            ps.setInt(6, entity.getId());
            ps.executeUpdate();
        }
    }
}