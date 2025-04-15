package repositories.impl;

import connection.DatabaseConnection;
import lombok.Getter;
import model.entities.CategoryEntity;
import repositories.interfaces.IRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository implements IRepository<CategoryEntity> {
    @Getter
    private static final CategoryRepository instance = new CategoryRepository();

    private CategoryRepository() {
    }

    private Optional<CategoryEntity> resultToCategory(ResultSet rs) throws SQLException {
        return Optional.of(CategoryEntity.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build());
    }

    @Override
    public void update(CategoryEntity categoryEntity) throws SQLException {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoryEntity.getName());
            ps.setInt(2, categoryEntity.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void save(CategoryEntity categoryEntity) throws SQLException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categoryEntity.getName());
            ps.executeUpdate();
        }
    }

    @Override
    public List<CategoryEntity> findAll() throws SQLException {
        String sql = "SELECT * FROM categories";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<CategoryEntity> categories = new ArrayList<>();
            while (rs.next()) {
                Optional<CategoryEntity> categoryOpt = resultToCategory(rs);

                categoryOpt.ifPresent(categories::add);
            }
            return categories;
        }
    }

    @Override
    public Optional<CategoryEntity> findByID(Integer id) throws SQLException {
       String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                   if(rs.next()) {
                    return resultToCategory(rs);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public void deleteByID(Integer id) throws SQLException{
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
