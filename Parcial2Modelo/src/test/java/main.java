import model.entities.GameEntity;
import services.CategoryService;
import services.GameService;

import java.util.List;
import java.util.Optional;

public class main {
    public static void main (String[] args) {
        final GameService gameService = GameService.getInstance();
        final CategoryService categoryService = CategoryService.getInstance();

        gameService.findAllGamesCategory();


    }
}