package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.GameDao;
import model.GameData;

import java.sql.Connection;
import java.util.Collection;

public class SqlGameDao implements GameDao {
    private final Connection conn;

    public SqlGameDao(Connection conn){
        this.conn = conn;
        initializeDatabase();
    }

    private void initializeDatabase(){
        throw new RuntimeException("Not implemented");
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
