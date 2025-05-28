package dataaccess;

import model.UserData;

import java.sql.Connection;

public class SqlUserDao implements UserDao {

    public SqlUserDao() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void clear() throws DataAccessException {
        throw new RuntimeException("Not implemented");
    }
}
