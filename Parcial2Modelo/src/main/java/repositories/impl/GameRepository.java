package repositories.impl;

import connection.DatabaseConnection;
import lombok.Getter;
import model.entities.CategoryEntity;
import model.entities.GameEntity;
import repositories.interfaces.IRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameRepository implements IRepository<GameEntity> {
    @Getter
    private static final GameRepository instance = new GameRepository();

    private GameRepository (){}

    private Optional<GameEntity> resultToGame(ResultSet rs) throws SQLException {
        return Optional.of(GameEntity.builder()
                .id(rs.getInt("id"))
                .title(rs.getString("title"))
                .categoryId(rs.getInt("category_id"))
                .build());
    }

    @Override
    public void update(GameEntity gameEntity) throws SQLException {
        String sql = "UPDATE games SET title = ?, category_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, gameEntity.getTitle());
            ps.setInt(2, gameEntity.getCategoryId());
            ps.setInt(3, gameEntity.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void save(GameEntity gameEntity) throws SQLException {
        String sql = "INSERT INTO games (title, category_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, gameEntity.getTitle());
            ps.setInt(2, gameEntity.getCategoryId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<GameEntity> findAll() throws SQLException {
        String sql = "SELECT * FROM games";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<GameEntity> games = new ArrayList<>();
            while (rs.next()) {
                Optional<GameEntity> gameOpt = resultToGame(rs);

                gameOpt.ifPresent(games::add);
            }
            return games;
        }
    }

    @Override
    public Optional<GameEntity> findByID(Integer id) throws SQLException {
        String sql = "SELECT * FROM games WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return resultToGame(rs);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public void deleteByID(Integer id) throws SQLException{
        String sql = "DELETE FROM games WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
