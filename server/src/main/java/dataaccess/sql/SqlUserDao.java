package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDao;
import model.UserData;

import java.sql.Connection;

public class SqlUserDao implements UserDao {
    private final Connection conn;

    public SqlUserDao(Connection conn){
        this.conn = conn;
        initializeDatabase();
    }

    private void initializeDatabase(){
        throw new RuntimeException("Not implemented");
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
