package repositories.impl;

public class GameRepository {
    private static GameRepository instance;

    private GameRepository (){}

    public static GameRepository getInstance () {
        if(instance==null) instance = new GameRepository();
        return instance;
    }
}
