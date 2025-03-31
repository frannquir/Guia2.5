package model.repositories.impl;

import model.entities.enums.EPermiso;
import model.entities.enums.ETipo;
import model.entities.impl.CredencialEntity;
import model.entities.impl.CuentaEntity;
import model.repositories.ConexionSQLite;
import model.repositories.interfaces.IRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CuentaRepository implements IRepository<CuentaEntity> {
    private static CuentaRepository instance;

    public CuentaRepository() {
    }

    public static CuentaRepository getInstance() {
        if (instance == null) instance = new CuentaRepository();
        return instance;
    }

    private Optional<CuentaEntity> resultToCuenta(ResultSet rs) throws SQLException {
        return Optional.of(new CuentaEntity(
                rs.getInt("id"),
                rs.getInt("usuarioId"),
                ETipo.valueOf(rs.getString("tipo")),
                rs.getFloat("saldo"),
                rs.getTimestamp("fecha_creacion").toLocalDateTime()
        ));
    }

    @Override
    public void save(CuentaEntity entity) throws SQLException {
        if (entity.getTipo() == ETipo.CAJA_AHORRO) {
            String checkSQL = "SELECT COUNT(*) FROM cuentas WHERE usuarioId = ? AND tipo 'CAJA_AHORRO'";
            try (Connection connection = ConexionSQLite.getConnection();
                 PreparedStatement ps = connection.prepareStatement(checkSQL)) {
                ps.setInt(1, entity.getUsuarioId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.getInt(1) >= 1)
                        throw new SQLException("El usuario ya tiene una caja de ahorro.");
                }
            }
        }
        String sql = "INSERT INTO cuentas (usuarioId, tipo, saldo, fecha_creacion) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getUsuarioId());
            ps.setString(2, entity.getTipo().name());
            ps.setFloat(3, entity.getSaldo());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getFecha_creacion()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    entity.setId(rs.getInt(1));
            }
        }
    }

    @Override
    public List<CuentaEntity> findAll() throws SQLException {
        String sql = "SELECT * FROM cuentas";
        try (Connection connection = ConexionSQLite.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<CuentaEntity> cuentas = new ArrayList<>();
            while (rs.next()) {
                Optional<CuentaEntity> cuenta = resultToCuenta(rs);

                cuenta.ifPresent(cuentas::add);
            }
            return cuentas;
        }
    }

    @Override
    public Optional<CuentaEntity> findByID(Integer id) throws SQLException {
        String sql = "SELECT * FROM cuentas WHERE id = ?";
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return resultToCuenta(rs);
                }
                return Optional.empty();
            }
        }
    }


    @Override
    public void deleteByID(Integer id) throws SQLException {
        String sql = "DELETE FROM cuentas WHERE id = ?";
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(CuentaEntity entity) throws SQLException {
        String sql = "UPDATE cuentas SET saldo = ?, tipo = ? WHERE id = ?";
        try (Connection conn = ConexionSQLite.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setFloat(1, entity.getSaldo());
            ps.setString(2, entity.getTipo().name());
            ps.setInt(3, entity.getId());
            ps.executeUpdate();
        }
    }

}
