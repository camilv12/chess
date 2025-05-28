package service;

import dataaccess.DataAccessException;
import dataaccess.ram.RamGameDao;
import service.model.ListGamesResult;

public class ListGamesService {
    private final RamGameDao games = new RamGameDao();
    public ListGamesResult listGames() throws DataAccessException {
        return new ListGamesResult(games.listGames());
    }
}
