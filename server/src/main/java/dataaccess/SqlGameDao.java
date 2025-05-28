package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.util.Collection;

public class SqlGameDao implements GameDao {

    public SqlGameDao() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }
}
