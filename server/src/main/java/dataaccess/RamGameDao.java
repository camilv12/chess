package dataaccess;


import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RamGameDao implements GameDao{
    private static final Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if(games.containsKey(game.gameID())){
            throw new DataAccessException("Game already exists");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try{
            return games.get(gameID);
        } catch (Exception e){
            throw new DataAccessException("Game not found");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try{
            return games.values();
        } catch (Exception e){
            throw new DataAccessException("List failed: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if(!games.containsKey(game.gameID())){
            throw new DataAccessException("Game not found");
        }
        games.put(game.gameID(), game);
    }

    @Override
    public void clear() throws DataAccessException {
        try{
            games.clear();
        } catch (Exception e) {
            throw new DataAccessException("Clear failed: " + e.getMessage());
        }
    }
}
