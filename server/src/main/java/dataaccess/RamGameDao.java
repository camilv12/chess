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
        GameData result =  games.get(gameID);
        if(result == null) throw new DataAccessException("Game not found");
        return result;
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

    public boolean isEmpty() { return games.isEmpty(); }

    public Collection<Integer> getGameIds() throws DataAccessException{
        try{
            return games.keySet();
        } catch (Exception e){
            throw new DataAccessException("Set failed: " + e.getMessage());
        }
    }
}
