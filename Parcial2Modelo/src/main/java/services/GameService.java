package services;

import lombok.Getter;
import repositories.impl.CategoryRepository;
import repositories.impl.GameRepository;

public class GameService {
    private final GameRepository gameRepository;
    @Getter
    private static final GameService instance = new GameService();

    private GameService  () {
        gameRepository = GameRepository.getInstance();
    }
}
