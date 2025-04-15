package services;

import lombok.Getter;
import model.entities.CategoryEntity;
import repositories.impl.CategoryRepository;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CategoryService {
    private final CategoryRepository categoryRepository;
    @Getter
    private static final CategoryService instance = new CategoryService();

    private CategoryService() {
        categoryRepository = CategoryRepository.getInstance();
    }

    public void saveCategory (CategoryEntity category) {
        try {
            categoryRepository.save(category);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    public void deleteCategory (Integer id) {
        try {
            categoryRepository.deleteByID(id);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    public String findAllCategories () {
        try {
            if(categoryRepository.findAll().isEmpty())
                throw new NoSuchElementException();
            return categoryRepository.findAll().toString();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
    public CategoryEntity findByID (Integer id) {
        try {
            Optional<CategoryEntity> categoryOpt = categoryRepository.findByID(id);
            if(categoryOpt.isEmpty()) {
                throw new NoSuchElementException("Category not found");
            }
            return categoryOpt.get();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        return CategoryEntity.builder().build();
    }
    public void updateCategory (CategoryEntity category) {
        try {
            Optional<CategoryEntity> categoryOpt = categoryRepository.findByID(category.getId());
            if(categoryOpt.isEmpty())
                throw new NoSuchElementException("Category not found");
            categoryRepository.update(category);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }
}
