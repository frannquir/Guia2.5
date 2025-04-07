package model.repositories;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexionSQLite {
    private static final DataSource dataSource;
    private static final String URL = "jdbc:sqlite:banco.db";

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setMaximumPoolSize(10);   // Máximo de conexiones en el pool
        config.setMinimumIdle(2);        // Mínimo de conexiones inactivas
        config.setIdleTimeout(30000);    // Tiempo antes de cerrar conexiones inactivas (30 seg)
        config.setConnectionTimeout(10000); // Timeout para obtener conexión (10 seg)
        config.setLeakDetectionThreshold(5000); // Detección de fugas de conexiones (5 seg)

        dataSource = new HikariDataSource(config);
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
