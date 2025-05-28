package dataaccess.ram;


import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RamGameDao implements GameDao {
    private static final Map<Integer, GameData> GAME_DATA_MAP = new HashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if(GAME_DATA_MAP.containsKey(game.gameID())){
            throw new DataAccessException("Game already exists");
        }
        GAME_DATA_MAP.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData result =  GAME_DATA_MAP.get(gameID);
        if(result == null) { throw new DataAccessException("Game not found"); }
        return result;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try{
            return GAME_DATA_MAP.values();
        } catch (Exception e){
            throw new DataAccessException("List failed: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if(!GAME_DATA_MAP.containsKey(game.gameID())){
            throw new DataAccessException("Game not found");
        }
        GAME_DATA_MAP.put(game.gameID(), game);
    }

    @Override
    public void clear() throws DataAccessException {
        try{
            GAME_DATA_MAP.clear();
        } catch (Exception e) {
            throw new DataAccessException("Clear failed: " + e.getMessage());
        }
    }

    public boolean isEmpty() { return GAME_DATA_MAP.isEmpty(); }

    public Collection<Integer> getGameIds() throws DataAccessException{
        try{
            return GAME_DATA_MAP.keySet();
        } catch (Exception e){
            throw new DataAccessException("Set failed: " + e.getMessage());
        }
    }
}
