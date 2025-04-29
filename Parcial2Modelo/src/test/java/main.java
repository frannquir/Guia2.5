import model.entities.CategoryEntity;
import model.entities.GameEntity;
import services.CategoryService;
import services.GameService;

import java.util.List;
import java.util.Optional;

public class main {
    public static void main (String[] args) {
        final GameService gameService = GameService.getInstance();
        final CategoryService categoryService = CategoryService.getInstance();
        System.out.println(categoryService.findAllCategories());
        System.out.println(gameService.findAllGames());
        gameService.deleteGame(7);
        System.out.println(gameService.findAllGames());
        System.out.println(gameService.categoryWithMostGames());
        System.out.println(gameService.listGamesByCategory("Action4"));


        gameService.findAllGamesCategory();


    }
}