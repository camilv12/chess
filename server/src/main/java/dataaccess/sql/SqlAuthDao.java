package dataaccess.sql;

import dataaccess.AuthDao;
import dataaccess.DataAccessException;
import model.AuthData;

import java.sql.Connection;

public class SqlAuthDao implements AuthDao {
    private final Connection conn;

    public SqlAuthDao(Connection conn){
        this.conn = conn;
        initializeDatabase();
    }

    private void initializeDatabase(){
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }
}
