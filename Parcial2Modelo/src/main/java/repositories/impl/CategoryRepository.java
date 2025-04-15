package repositories.impl;

import connection.DatabaseConnection;
import model.entities.CategoryEntity;
import repositories.interfaces.IRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

public class CategoryRepository implements IRepository<CategoryEntity> {
    private static CategoryRepository instance;

    private CategoryRepository (){}

    public static CategoryRepository getInstance () {
        if(instance==null) instance = new CategoryRepository();
        return instance;
    }

    @Override
    public void update(CategoryEntity categoryEntity) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {

        }
    }

    @Override
    public void save(CategoryEntity categoryEntity) {

    }

    @Override
    public List<CategoryEntity> findAll() {
        return List.of();
    }

    @Override
    public Optional<CategoryEntity> findByID(Integer id) {
        return Optional.empty();
    }

    @Override
    public void deleteByID(Integer id) {

    }
}
