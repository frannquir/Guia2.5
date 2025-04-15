package services;

import lombok.Getter;
import model.entities.CategoryEntity;
import repositories.impl.CategoryRepository;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Getter
    private static final CategoryService instance = new CategoryService();

    private CategoryService() {
        categoryRepository = CategoryRepository.getInstance();
    }
}
