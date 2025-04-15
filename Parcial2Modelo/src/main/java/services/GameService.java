package services;

import lombok.Getter;
import model.entities.CategoryEntity;
import model.entities.GameEntity;
import repositories.impl.CategoryRepository;
import repositories.impl.GameRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class GameService {
    private final GameRepository gameRepository;
    @Getter
    private static final GameService instance = new GameService();

    private GameService  () {
        gameRepository = GameRepository.getInstance();
    }
    public void saveGame (GameEntity game) {
        try {
            gameRepository.save(game);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    public void deleteGame (Integer id) {
        try {
            gameRepository.deleteByID(id);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    public String findAllGames () {
        try {
            if(gameRepository.findAll().isEmpty())
                throw new NoSuchElementException("No games found");
            return gameRepository.findAll().toString();
        } catch(SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
    public GameEntity findByID (Integer id) {
        try {
            Optional<GameEntity> gameOpt = gameRepository.findByID(id);
            if(gameOpt.isEmpty()) {
                throw new NoSuchElementException("Game not found");
            }
            return gameOpt.get();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        return GameEntity.builder().build();
    }
    public void updateGame (GameEntity game) {
        try {
            Optional<GameEntity> gameOpt = gameRepository.findByID(game.getId());
            if (gameOpt.isEmpty())
                throw new NoSuchElementException("Game not found");
            gameRepository.update(game);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    public void listGamesByCategory () {
        try {
            HashMap<Integer, GameEntity> gamesByCategory = gameRepository.findAll().stream()
                    .collect(Collectors.groupingBy(game -> game.getCategoryId()));

        }
    }
}
