package services;

import lombok.Getter;
import model.entities.CategoryEntity;
import model.entities.GameEntity;
import repositories.impl.CategoryRepository;
import repositories.impl.GameRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GameService {
    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    @Getter
    private static final GameService instance = new GameService();

    private GameService  () {
        gameRepository = GameRepository.getInstance();
        categoryRepository = CategoryRepository.getInstance();
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

    public CategoryEntity categoryWithMostGames () {
        try{
            List<GameEntity> games = gameRepository.findAll();
            if(games.isEmpty()) {
                throw new NoSuchElementException("No games found");
            }

            Integer categoryWithMostGamesId = gameRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(GameEntity::getCategoryId, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue())
                    .orElseThrow(NoSuchElementException::new)
                    .getKey();

            return categoryRepository.findByID(categoryWithMostGamesId)
                    .orElseThrow(NoSuchElementException::new);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        return CategoryEntity.builder()
                .id(-1)
                .name("N/A")
                .build();
    }
    public List<GameEntity> listGamesByCategory (String name) {
        try{
            Integer categoryId = categoryRepository.findAll()
                    .stream()
                    .filter(c -> c.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .getId();

            List<GameEntity> games = gameRepository.findAll()
                    .stream()
                    .filter(g -> g.getCategoryId().equals(categoryId))
                    .toList();

            if(games.isEmpty()) {
                throw new NoSuchElementException("Games not found");
            }
            return games;
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    public Map<String, List<GameEntity>> findAllGamesCategory () {
        try{
            Map<Integer, String> categoryNames = findCategoryNames();
            Map<Integer, List<GameEntity>> gamesMap = gamesToMapByCategoryID();

            Map <String,List<GameEntity>> result = gamesMap.entrySet().stream()
                    .collect(Collectors.toMap(key -> categoryNames.get(key.getKey()),
                            Map.Entry::getValue));

            System.out.println(result);
            /*
            return gameRepository.findAll()
                    .stream()
                    .collect(Collectors.groupingBy(game -> categoryNames
                            .getOrDefault(game.getCategoryId(), "Uncategorized"),
                            HashMap::new,
                            Collectors.toList()));

             */
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
        return new HashMap<>();
    }
    private Map<Integer, String> findCategoryNames () throws SQLException {
        return categoryRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        CategoryEntity::getId,
                        CategoryEntity::getName));
    }
    private Map<Integer, List<GameEntity>> gamesToMapByCategoryID () throws SQLException {
        return gameRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(GameEntity::getCategoryId));
    }
}
